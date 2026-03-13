import { ref, computed, type Ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAiSessions,
  getAiSessionMessages,
  createAiSession,
  saveAiMessages,
  updateAiSessionTitle,
  deleteAiSession
} from '@/api/openAi'

const PINNED_KEY = 'ai_pinned_sessions'

export function mapApiMessageToView(m: { role?: string; content?: string }) {
  const role = (m.role || '').toUpperCase()
  return {
    role: role === 'USER' ? 'user' : 'assistant',
    content: m.content || '',
    isCode: false
  }
}

export function useAiChatSession(messagesRef: Ref<{ role: string; content: string; isCode?: boolean; isError?: boolean; feedback?: string }[]>, defaultMessages: { role: string; content: string; isCode?: boolean }[]) {
  const sessions = ref<{ id: string; title?: string; updateTime?: string }[]>([])
  const currentSessionId = ref<string | null>(null)
  const sessionLoading = ref(false)
  const pinnedSessionIds = ref<string[]>(JSON.parse(localStorage.getItem(PINNED_KEY) || '[]'))
  const editingSessionId = ref<string | null>(null)
  const editingTitle = ref('')

  const sortedSessions = computed(() => {
    const raw = sessions.value || []
    const list = raw.filter((s) => s != null && (s as { id?: unknown }).id != null)
    const pinned = pinnedSessionIds.value || []
    return list.sort((a, b) => {
      const aId = (a as { id: string }).id
      const bId = (b as { id: string }).id
      const aPin = pinned.includes(String(aId))
      const bPin = pinned.includes(String(bId))
      if (aPin && !bPin) return -1
      if (!aPin && bPin) return 1
      if (aPin && bPin) return new Date(b.updateTime || 0).getTime() - new Date(a.updateTime || 0).getTime()
      return new Date(b.updateTime || 0).getTime() - new Date(a.updateTime || 0).getTime()
    })
  })

  function formatSessionTime(ts: string | number | undefined) {
    if (!ts) return ''
    const d = new Date(ts)
    const now = new Date()
    const sameDay = d.getDate() === now.getDate() && d.getMonth() === now.getMonth() && d.getFullYear() === now.getFullYear()
    if (sameDay) {
      return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    }
    return d.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
  }

  function isPinned(sessionId: string) {
    return (pinnedSessionIds.value || []).includes(sessionId)
  }

  function togglePin(sessionId: string) {
    let list = [...(pinnedSessionIds.value || [])]
    if (list.includes(sessionId)) {
      list = list.filter((id) => id !== sessionId)
    } else {
      list = [sessionId, ...list]
    }
    pinnedSessionIds.value = list
    localStorage.setItem(PINNED_KEY, JSON.stringify(list))
  }

  function startEditTitle(s: { id: string; title?: string }) {
    editingSessionId.value = s.id
    editingTitle.value = s.title || ''
  }

  function cancelEditTitle() {
    editingSessionId.value = null
    editingTitle.value = ''
  }

  async function confirmEditTitle(sessionId: string) {
    const title = (editingTitle.value || '').trim()
    if (!title) {
      cancelEditTitle()
      return
    }
    try {
      await updateAiSessionTitle(sessionId, title)
      const s = sessions.value.find((x) => x.id === sessionId)
      if (s) s.title = title
      ElMessage.success('已修改对话名称')
    } catch (e) {
      console.error(e)
      ElMessage.error((e as { response?: { data?: { msg?: string } }; message?: string })?.response?.data?.msg || (e as Error)?.message || '修改失败')
    }
    cancelEditTitle()
  }

  async function ensureCurrentSession() {
    if (currentSessionId.value) return currentSessionId.value
    try {
      const res = await createAiSession('新对话')
      const data = res?.data ?? res
      const sid = data?.id
      if (sid) {
        currentSessionId.value = sid
        sessions.value = [...(sessions.value || []), data].sort((a, b) => new Date(b.updateTime || 0).getTime() - new Date(a.updateTime || 0).getTime())
        messagesRef.value = [...defaultMessages]
        return sid
      }
    } catch (e) {
      console.error(e)
      const msg = (e as { response?: { data?: { msg?: string }; msg?: string }; msg?: string })?.response?.data?.msg || (e as { msg?: string })?.msg || '创建会话失败'
      ElMessage.error(msg)
    }
    return null
  }

  async function persistCurrentMessages(title: string | undefined, lastUserContent: string, lastAssistantContent: string) {
    const sid = currentSessionId.value
    if (!sid || lastUserContent === undefined) return
    try {
      const payload = [
        { role: 'USER', content: lastUserContent },
        { role: 'ASSISTANT', content: lastAssistantContent }
      ]
      await saveAiMessages(sid, payload)
      if (title) await updateAiSessionTitle(sid, title)
      const res = await getAiSessions()
      sessions.value = normalizeSessionList(res?.data ?? res ?? [])
    } catch (e) {
      console.error(e)
    }
  }

  function normalizeSessionList(raw: unknown): { id: string; title?: string; updateTime?: string }[] {
    let list: unknown[] = []

    // 1. 先把外层的数组拿出来（兼容若依常见结构）
    if (Array.isArray(raw)) {
      list = raw
    } else if (raw && typeof raw === 'object') {
      const o = raw as Record<string, unknown>
      if (Array.isArray(o.rows)) list = o.rows as unknown[]
      else if (Array.isArray(o.list)) list = o.list as unknown[]
      else if (Array.isArray(o.data)) list = o.data as unknown[]
      else if (Array.isArray(o.content)) list = o.content as unknown[]
      else if (Array.isArray(o.records)) list = o.records as unknown[]
      else if (Array.isArray(o.items)) list = o.items as unknown[]
    }

    // 2. 把每一项统一映射成 { id, title, updateTime }
    return list
      .filter((s) => s != null && typeof s === 'object')
      .map((s) => {
        const item = s as Record<string, unknown>

        // ID 兼容多种命名
        const rawId =
          item.id ??
          item.sessionId ??
          item.session_id ??
          item.sid

        if (rawId == null) return null

        // 标题兼容多种命名
        const rawTitle =
          item.title ??
          item.name ??
          item.sessionTitle ??
          item.session_title

        // 更新时间兼容多种命名（你当前接口是 updateTime）
        const rawUpdateTime =
          item.updateTime ??
          item.update_time ??
          item.gmtModified ??
          item.gmt_modified ??
          item.createTime

        return {
          id: String(rawId),
          title: rawTitle != null && String(rawTitle).trim() ? String(rawTitle) : '未命名对话',
          updateTime: rawUpdateTime as string | undefined
        } as { id: string; title?: string; updateTime?: string }
      })
      .filter((item): item is { id: string; title?: string; updateTime?: string } => item != null)
  }

  const LOAD_SESSIONS_TIMEOUT = 15000

  async function loadSessions() {
    sessionLoading.value = true
    try {
      const res = await Promise.race([
        getAiSessions(),
        new Promise<never>((_, reject) =>
          setTimeout(() => reject(new Error('加载会话列表超时，请检查网络或后端服务')), LOAD_SESSIONS_TIMEOUT)
        )
      ])
      // 若依接口返回 { code, msg, data }，data 可能为数组或 { rows/list }
      const raw = res?.data ?? res ?? []
      const arr = normalizeSessionList(raw)
      sessions.value = arr
      if (arr.length > 0) {
        const latest = arr[0]
        const latestId = latest?.id
        if (latestId != null) {
          currentSessionId.value = latestId
          const msgRes = await getAiSessionMessages(latestId)
          const msgList = msgRes?.data ?? msgRes ?? []
          const msgArr = Array.isArray(msgList) ? msgList : []
          messagesRef.value = msgArr.length ? msgArr.map(mapApiMessageToView) : [...defaultMessages]
        }
      } else {
        const createRes = await createAiSession('新对话')
        const data = createRes?.data ?? createRes
        if (data?.id != null) {
          currentSessionId.value = data.id
          sessions.value = [data]
          messagesRef.value = [...defaultMessages]
        }
      }
      return true
    } catch (e) {
      console.error(e)
      const msg = (e as Error)?.message || (e as { response?: { data?: { msg?: string }; msg?: string }; msg?: string })?.response?.data?.msg || (e as { msg?: string })?.msg || '加载会话列表失败'
      ElMessage.error(msg)
      return false
    } finally {
      sessionLoading.value = false
    }
  }

  async function newChat() {
    try {
      sessionLoading.value = true
      const res = await createAiSession('新对话')
      const data = res?.data ?? res
      if (data?.id != null) {
        currentSessionId.value = data.id
        const listRes = await getAiSessions()
        sessions.value = normalizeSessionList(listRes?.data ?? listRes ?? [])
        messagesRef.value = [...defaultMessages]
        return true
      }
    } catch (e) {
      console.error(e)
      const msg = (e as { response?: { data?: { msg?: string }; msg?: string }; msg?: string })?.response?.data?.msg || (e as { msg?: string })?.msg || '创建会话失败'
      ElMessage.error(msg)
    } finally {
      sessionLoading.value = false
    }
    return false
  }

  async function selectSession(sessionId: string) {
    if (sessionId === currentSessionId.value) return
    currentSessionId.value = sessionId
    try {
      const res = await getAiSessionMessages(sessionId)
      const list = res?.data ?? res ?? []
      const arr = Array.isArray(list) ? list : []
      messagesRef.value = arr.length ? arr.map(mapApiMessageToView) : [...defaultMessages]
    } catch (e) {
      console.error(e)
      messagesRef.value = [...defaultMessages]
    }
  }

  async function removeSession(sessionId: string) {
    try {
      await ElMessageBox.confirm('确定删除该对话吗？删除后无法恢复。', '删除对话', {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
      })
    } catch {
      return
    }
    try {
      await deleteAiSession(sessionId)
      const list = (sessions.value || []).filter((s) => s != null && s.id != null && String(s.id) !== String(sessionId))
      sessions.value = list
      const pinned = (pinnedSessionIds.value || []).filter((id) => String(id) !== String(sessionId))
      pinnedSessionIds.value = pinned
      localStorage.setItem(PINNED_KEY, JSON.stringify(pinned))
      if (currentSessionId.value == null ? false : String(currentSessionId.value) === String(sessionId)) {
        const first = list[0]
        currentSessionId.value = first != null && first.id != null ? first.id : null
        if (first != null && first.id != null) {
          const res = await getAiSessionMessages(first.id)
          const msgList = res?.data ?? res ?? []
          messagesRef.value = Array.isArray(msgList) && msgList.length ? msgList.map(mapApiMessageToView) : [...defaultMessages]
        } else {
          messagesRef.value = [...defaultMessages]
        }
      }
      ElMessage.success('已删除')
    } catch (e) {
      console.error(e)
      ElMessage.error((e as { response?: { data?: { msg?: string } }; message?: string })?.response?.data?.msg || (e as Error)?.message || '删除失败')
    }
  }

  return {
    sessions,
    currentSessionId,
    sessionLoading,
    pinnedSessionIds,
    sortedSessions,
    editingSessionId,
    editingTitle,
    formatSessionTime,
    isPinned,
    togglePin,
    startEditTitle,
    cancelEditTitle,
    confirmEditTitle,
    ensureCurrentSession,
    persistCurrentMessages,
    loadSessions,
    newChat,
    selectSession,
    removeSession,
    mapApiMessageToView
  }
}
