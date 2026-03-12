<template>
  <div class="ai-chat-page">
    <!-- 左侧会话列表 -->
    <aside v-loading="sessionLoading" class="ai-chat-sidebar">
      <div class="ai-chat-sidebar-header">
        <span class="ai-chat-sidebar-title">历史对话</span>
        <el-button type="primary" size="small" @click="newChat">新对话</el-button>
      </div>
      <div class="ai-chat-session-list">
        <div
          v-for="s in sortedSessions"
          :key="s.id"
          class="ai-chat-session-item"
          :class="{ active: currentSessionId === s.id }"
          @click="selectSession(s.id)"
        >
          <div class="ai-chat-session-info" @click="editingSessionId === s.id && $event.stopPropagation()">
            <template v-if="editingSessionId === s.id">
              <el-input
                v-model="editingTitle"
                size="small"
                placeholder="对话名称"
                @keyup.enter="confirmEditTitle(s.id)"
                @keyup.escape="cancelEditTitle"
              />
            </template>
            <template v-else>
              <span class="ai-chat-session-title">
                <el-icon v-if="isPinned(s.id)" class="ai-chat-pin-icon"><Top /></el-icon>
                {{ s.title }}
              </span>
              <span class="ai-chat-session-time">{{ formatSessionTime(s.updateTime) }}</span>
            </template>
          </div>
          <div v-if="editingSessionId !== s.id" class="ai-chat-session-actions" @click.stop>
            <el-dropdown trigger="click" @command="(cmd) => handleSessionCommand(cmd, s)">
              <el-button type="primary" link size="small" class="ai-chat-more-btn">
                <el-icon><MoreFilled /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu class="ai-chat-session-dropdown-menu">
                  <el-dropdown-item command="pin">
                    <el-icon><Top /></el-icon>
                    <span>{{ isPinned(s.id) ? '取消固定' : '固定' }}</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="rename">
                    <el-icon><Edit /></el-icon>
                    <span>重命名</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided>
                    <el-icon><Delete /></el-icon>
                    <span>删除</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
        <div v-if="!sessions.length" class="ai-chat-session-empty">暂无对话，点击「新对话」开始</div>
      </div>
    </aside>

    <div class="ai-chat-main">
      <div class="ai-chat-header">
        <div class="ai-chat-title">AI 助手</div>
        <div class="ai-chat-subtitle">基于 OpenAI / DeepSeek 的智能问答</div>
      </div>

      <div class="ai-chat-body">
        <div class="ai-chat-messages" ref="messageListRef" @click="handleMarkdownCopy">
          <div
            v-for="(msg, index) in messages"
            :key="index"
            class="ai-chat-message"
            :class="msg.role === 'user' ? 'is-user' : 'is-assistant'"
          >
            <div class="ai-chat-avatar">
              <el-avatar v-if="msg.role === 'user'" :size="32">
                {{ userInitial }}
              </el-avatar>
              <el-avatar v-else :size="32">AI</el-avatar>
            </div>
            <div class="ai-chat-content">
              <div class="ai-chat-name">
                {{ msg.role === 'user' ? '我' : 'AI 助手' }}
              </div>
              <div class="ai-chat-bubble">
                <template v-if="msg.role === 'assistant'">
                  <div v-if="msg.content" class="ai-chat-markdown">
                    <template v-if="isTypingAssistant(index)">
                      <div class="ai-chat-typing-wrapper">
                        <span class="ai-chat-typing-visible">{{ msg.content }}</span>
                        <span class="ai-chat-typing-fog">{{ fogTail(msg.content) }}</span>
                      </div>
                    </template>
                    <template v-else>
                      <div v-html="renderMarkdown(msg.content)"></div>
                    </template>
                  </div>
                  <div
                    v-else-if="loading && isLastAssistant(index)"
                    class="ai-chat-text ai-chat-typing"
                  >
                    正在思考中...
                  </div>
                  <div v-else class="ai-chat-text">{{ msg.content }}</div>
                </template>
                <template v-else>
                  <div class="ai-chat-text">{{ msg.content }}</div>
                </template>
              </div>
              <div v-if="msg.role === 'assistant'" class="ai-chat-message-actions">
                <el-button type="primary" link size="small" @click="handleCopyMessage(msg)">
                  复制内容
                </el-button>
                <el-button
                  v-if="isLastAssistant(index)"
                  type="primary"
                  link
                  size="small"
                  :disabled="loading"
                  @click="handleRegenerateLast()"
                >
                  重新生成
                </el-button>
              </div>
            </div>
          </div>

        </div>

        <div class="ai-chat-input">
          <el-input
            v-model="input"
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 6 }"
            placeholder="请输入你的问题，例如：帮我分析一下这个 SQL 的性能问题..."
            @keyup.enter.exact.prevent="handleSend"
          />
          <div class="ai-chat-actions">
            <span class="ai-chat-tip">回车发送，Shift+回车换行</span>
            <el-button type="primary" :loading="loading" @click="handleSend">
              发送
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  streamChatOpenAi,
  getAiSessions,
  getAiSessionMessages,
  createAiSession,
  saveAiMessages,
  updateAiSessionTitle,
  deleteAiSession
} from '@/api/openAi'
import { ElMessageBox } from 'element-plus'
import { Top, MoreFilled, Edit, Delete } from '@element-plus/icons-vue'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'
import useUserStore from '@/store/modules/user'

const DEFAULT_WELCOME = [
  {
    role: 'assistant',
    content: '你好，我是集成在若依后台中的 AI 助手，可以帮你分析代码、生成接口文档、编写 SQL、排查问题等。',
    isCode: false
  }
]

const userStore = useUserStore()

const input = ref('')
const loading = ref(false)
const sessionLoading = ref(false)
const messageListRef = ref(null)
const sessions = ref([])
const currentSessionId = ref(null)
const messages = ref([...DEFAULT_WELCOME])
const editingSessionId = ref(null)
const editingTitle = ref('')
const PINNED_KEY = 'ai_pinned_sessions'
const pinnedSessionIds = ref(JSON.parse(localStorage.getItem(PINNED_KEY) || '[]'))

const userInitial = computed(() => {
  if (userStore.name) {
    return userStore.name.charAt(0).toUpperCase()
  }
  return '我'
})

/** 置顶的会话排在前面，其余按更新时间倒序 */
const sortedSessions = computed(() => {
  const list = [...(sessions.value || [])]
  const pinned = pinnedSessionIds.value || []
  return list.sort((a, b) => {
    const aPin = pinned.includes(a.id)
    const bPin = pinned.includes(b.id)
    if (aPin && !bPin) return -1
    if (!aPin && bPin) return 1
    if (aPin && bPin) return new Date(b.updateTime || 0) - new Date(a.updateTime || 0)
    return new Date(b.updateTime || 0) - new Date(a.updateTime || 0)
  })
})

function isLastAssistant(index) {
  for (let i = messages.value.length - 1; i >= 0; i -= 1) {
    if (messages.value[i].role === 'assistant') {
      return i === index
    }
  }
  return false
}

function isTypingAssistant(index) {
  return loading.value && isLastAssistant(index)
}

function fogTail(text) {
  if (!text) return ''
  const str = String(text)
  const tailLen = Math.min(20, str.length)
  return str.slice(-tailLen)
}

function isPinned(sessionId) {
  return (pinnedSessionIds.value || []).includes(sessionId)
}

function handleSessionCommand(command, s) {
  if (command === 'rename') startEditTitle(s)
  else if (command === 'pin') togglePin(s.id)
  else if (command === 'delete') removeSession(s.id)
}

function startEditTitle(s) {
  editingSessionId.value = s.id
  editingTitle.value = s.title || ''
}

function cancelEditTitle() {
  editingSessionId.value = null
  editingTitle.value = ''
}

async function confirmEditTitle(sessionId) {
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
    ElMessage.error(e?.response?.data?.msg || e?.message || '修改失败')
  }
  cancelEditTitle()
}

function togglePin(sessionId) {
  let list = [...(pinnedSessionIds.value || [])]
  if (list.includes(sessionId)) {
    list = list.filter((id) => id !== sessionId)
  } else {
    list = [sessionId, ...list]
  }
  pinnedSessionIds.value = list
  localStorage.setItem(PINNED_KEY, JSON.stringify(list))
}

async function removeSession(sessionId) {
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
    const list = (sessions.value || []).filter((s) => s.id !== sessionId)
    sessions.value = list
    const pinned = (pinnedSessionIds.value || []).filter((id) => id !== sessionId)
    pinnedSessionIds.value = pinned
    localStorage.setItem(PINNED_KEY, JSON.stringify(pinned))
    if (currentSessionId.value === sessionId) {
      currentSessionId.value = list.length ? list[0].id : null
      if (list.length) {
        const res = await getAiSessionMessages(list[0].id)
        const msgList = res?.data ?? res ?? []
        messages.value = Array.isArray(msgList) && msgList.length
          ? msgList.map(mapApiMessageToView)
          : [...DEFAULT_WELCOME]
      } else {
        messages.value = [...DEFAULT_WELCOME]
      }
    }
    ElMessage.success('已删除')
  } catch (e) {
    console.error(e)
    ElMessage.error(e?.response?.data?.msg || e?.message || '删除失败')
  }
}

// Markdown + 代码高亮，代码块带语言标签与复制按钮
function escapeHtml(s) {
  if (s == null) return ''
  const str = String(s)
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

const renderer = new marked.Renderer()
renderer.code = function (code, lang, escaped) {
  const langTrimmed = (lang || '').trim().split(/\s+/)[0] || ''
  const langLabel = langTrimmed || 'plaintext'
  const langDisplay = langLabel.charAt(0).toUpperCase() + langLabel.slice(1).toLowerCase()
  let highlighted = code
  if (this.options.highlight) {
    const out = this.options.highlight(code, langTrimmed)
    if (out != null && out !== code) highlighted = out
  }
  const copyTitle = '复制代码'
  return (
    '<div class="ai-code-block">' +
    '<div class="ai-code-block-header">' +
    '<span class="ai-code-block-lang">' + escapeHtml(langDisplay) + '</span>' +
    '<button type="button" class="ai-code-block-copy" title="' + escapeHtml(copyTitle) + '">复制</button>' +
    '</div>' +
    '<pre><code class="language-' + escapeHtml(langLabel) + '">' + highlighted + '</code></pre>' +
    '</div>'
  )
}

marked.setOptions({
  renderer,
  highlight(code, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(code, { language: lang }).value
      } catch (_) {}
    }
    try {
      return hljs.highlightAuto(code).value
    } catch (_) {
      return code
    }
  }
})
function renderMarkdown(content) {
  if (!content) return ''
  try {
    return marked.parse(content)
  } catch (e) {
    return content
  }
}

function handleMarkdownCopy(e) {
  const btn = e.target.closest('.ai-code-block-copy')
  if (!btn) return
  const block = btn.closest('.ai-code-block')
  if (!block) return
  const pre = block.querySelector('pre')
  const codeEl = block.querySelector('pre code')
  const text = (codeEl || pre) ? (codeEl || pre).textContent || '' : ''
  if (!text) {
    ElMessage.warning('没有可复制的内容')
    return
  }
  if (navigator.clipboard && navigator.clipboard.writeText) {
    navigator.clipboard.writeText(text).then(() => {
      ElMessage.success('已复制到剪贴板')
    }).catch(() => {
      fallbackCopy(text)
    })
  } else {
    fallbackCopy(text)
  }
}

function fallbackCopy(text) {
  try {
    const ta = document.createElement('textarea')
    ta.value = text
    ta.style.position = 'fixed'
    ta.style.opacity = '0'
    document.body.appendChild(ta)
    ta.select()
    document.execCommand('copy')
    document.body.removeChild(ta)
    ElMessage.success('已复制到剪贴板')
  } catch (err) {
    console.error(err)
    ElMessage.error('复制失败，请手动选择复制')
  }
}

function formatSessionTime(ts) {
  if (!ts) return ''
  const d = new Date(ts)
  const now = new Date()
  const sameDay = d.getDate() === now.getDate() && d.getMonth() === now.getMonth() && d.getFullYear() === now.getFullYear()
  if (sameDay) {
    return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  return d.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

function scrollToBottom() {
  nextTick(() => {
    const el = messageListRef.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

function mapApiMessageToView(m) {
  const role = (m.role || '').toUpperCase()
  return {
    role: role === 'USER' ? 'user' : 'assistant',
    content: m.content || '',
    isCode: false
  }
}

async function ensureCurrentSession() {
  if (currentSessionId.value) return currentSessionId.value
  try {
    const res = await createAiSession('新对话')
    const data = res?.data ?? res
    const sid = data?.id
    if (sid) {
      currentSessionId.value = sid
      sessions.value = [...(sessions.value || []), data].sort((a, b) => new Date(b.updateTime || 0) - new Date(a.updateTime || 0))
      messages.value = [...DEFAULT_WELCOME]
      return sid
    }
  } catch (e) {
    console.error(e)
    const msg = e?.response?.data?.msg || e?.msg || '创建会话失败'
    ElMessage.error(msg)
  }
  return null
}

async function persistCurrentMessages(title, lastUserContent, lastAssistantContent) {
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
    const list = res?.data ?? res ?? []
    sessions.value = Array.isArray(list) ? list : []
  } catch (e) {
    console.error(e)
  }
}

async function newChat() {
  try {
    sessionLoading.value = true
    const res = await createAiSession('新对话')
    const data = res?.data ?? res
    if (data?.id) {
      currentSessionId.value = data.id
      const listRes = await getAiSessions()
      const list = listRes?.data ?? listRes ?? []
      sessions.value = Array.isArray(list) ? list : []
      messages.value = [...DEFAULT_WELCOME]
      scrollToBottom()
    }
  } catch (e) {
    console.error(e)
    const msg = e?.response?.data?.msg || e?.msg || '创建会话失败'
    ElMessage.error(msg)
  } finally {
    sessionLoading.value = false
  }
}

async function selectSession(sessionId) {
  if (sessionId === currentSessionId.value) return
  currentSessionId.value = sessionId
  try {
    const res = await getAiSessionMessages(sessionId)
    const list = res?.data ?? res ?? []
    const arr = Array.isArray(list) ? list : []
    messages.value = arr.length ? arr.map(mapApiMessageToView) : [...DEFAULT_WELCOME]
  } catch (e) {
    console.error(e)
    messages.value = [...DEFAULT_WELCOME]
  }
  scrollToBottom()
}

const buildHistoryMessages = () => {
  return messages.value.map((m) => ({
    role: m.role === 'user' ? 'USER' : 'ASSISTANT',
    message: m.content
  }))
}

async function handleCopyMessage(msg) {
  const text = msg?.content || ''
  if (!text) {
    ElMessage.warning('没有可复制的内容')
    return
  }
  try {
    if (navigator.clipboard && navigator.clipboard.writeText) {
      await navigator.clipboard.writeText(text)
    } else {
      const textarea = document.createElement('textarea')
      textarea.value = text
      textarea.style.position = 'fixed'
      textarea.style.opacity = '0'
      document.body.appendChild(textarea)
      textarea.select()
      document.execCommand('copy')
      document.body.removeChild(textarea)
    }
    ElMessage.success('已复制到剪贴板')
  } catch (e) {
    console.error(e)
    ElMessage.error('复制失败，请手动选择文本复制')
  }
}

/** 打字机展示：将流式 fullText 逐步写入 assistantMsg.content，每 30ms 追加 1 个字符 */
const TYPEWRITER_INTERVAL = 30
const TYPEWRITER_STEP = 1

async function runTypewriter(assistantMsg, getStreamPromise, onScroll) {
  const streamTarget = ref('')
  let timer = null
  let streamDone = false
  let streamResult = ''
  const startTime = Date.now()
  const MAX_TYPEWRITER_DURATION = 20000 // 45 秒兜底，防止长答案被截断

  return new Promise((resolve, reject) => {
    timer = setInterval(() => {
      const targetLen = streamTarget.value.length
      const currentLen = assistantMsg.content.length

      // 超时兜底：直接展示全文并结束，避免一直卡住
      if (Date.now() - startTime > MAX_TYPEWRITER_DURATION) {
        if (timer) clearInterval(timer)
        // 如果流已经结束，优先使用最终结果；否则用当前已收到的内容
        assistantMsg.content = streamResult || streamTarget.value
        onScroll()
        resolve(streamResult)
        return
      }

      if (currentLen < targetLen) {
        // 根据剩余长度动态调整步长，避免长文打字时间过长
        const remaining = targetLen - currentLen
        let step = TYPEWRITER_STEP
        if (remaining > 500) step = 16
        else if (remaining > 300) step = 12
        else if (remaining > 150) step = 8
        else if (remaining > 80) step = 4
        assistantMsg.content = streamTarget.value.slice(0, currentLen + step)
        onScroll()
      } else if (streamDone) {
        if (timer) clearInterval(timer)
        assistantMsg.content = streamTarget.value
        onScroll()
        resolve(streamResult)
      }
    }, TYPEWRITER_INTERVAL)

    getStreamPromise(streamTarget)
      .then((res) => {
        streamResult = res
        streamDone = true
      })
      .catch((err) => {
        if (timer) clearInterval(timer)
        reject(err)
      })
  })
}

async function handleRegenerateLast() {
  if (loading.value) return
  if (!messages.value.length) return

  const lastAssistantIndex = (() => {
    for (let i = messages.value.length - 1; i >= 0; i -= 1) {
      if (messages.value[i].role === 'assistant') return i
    }
    return -1
  })()
  if (lastAssistantIndex === -1) return

  let lastUserIndex = -1
  for (let i = lastAssistantIndex - 1; i >= 0; i -= 1) {
    if (messages.value[i].role === 'user') {
      lastUserIndex = i
      break
    }
  }
  if (lastUserIndex === -1) {
    ElMessage.error('未找到对应的用户提问，无法重新生成')
    return
  }

  const sid = await ensureCurrentSession()
  if (!sid) return

  const historyMessages = messages.value
    .slice(0, lastAssistantIndex)
    .map((m) => ({
      role: m.role === 'user' ? 'USER' : 'ASSISTANT',
      message: m.content
    }))

  const assistantMsg = messages.value[lastAssistantIndex]
  assistantMsg.content = ''
  scrollToBottom()

  const lastUserContent = messages.value[lastUserIndex].content || ''

  loading.value = true
  try {
    const assistantContent = await runTypewriter(
      assistantMsg,
      (streamTarget) => streamChatOpenAi(historyMessages, (fullText) => { streamTarget.value = fullText }),
      scrollToBottom
    )
    await persistCurrentMessages(undefined, lastUserContent, assistantContent)
    scrollToBottom()
  } catch (e) {
    console.error(e)
    assistantMsg.content = e?.message || '重新生成失败，请稍后重试。'
    await persistCurrentMessages(undefined, lastUserContent, assistantMsg.content)
    scrollToBottom()
  } finally {
    loading.value = false
  }
}

const handleSend = async () => {
  const content = input.value.trim()
  if (!content || loading.value) return

  const sid = await ensureCurrentSession()
  if (!sid) return
  const isFirstUserMessage = messages.value.filter((m) => m.role === 'user').length === 0
  const sessionTitle = content.slice(0, 20) + (content.length > 20 ? '...' : '')

  messages.value.push({
    role: 'user',
    content,
    isCode: false
  })
  input.value = ''
  scrollToBottom()

  const history = buildHistoryMessages()

  loading.value = true
  const assistantMsg = {
    role: 'assistant',
    content: '',
    isCode: false
  }
  messages.value.push(assistantMsg)
  scrollToBottom()

  try {
    const assistantContent = await runTypewriter(
      assistantMsg,
      (streamTarget) => streamChatOpenAi(history, (fullText) => { streamTarget.value = fullText }),
      scrollToBottom
    )
    await persistCurrentMessages(isFirstUserMessage ? sessionTitle : undefined, content, assistantContent)
    scrollToBottom()
  } catch (e) {
    assistantMsg.content = e?.message || '调用 AI 接口失败，请稍后重试或联系管理员检查 `/api/ai/chat/stream` 接口。'
    await persistCurrentMessages(undefined, content, assistantMsg.content)
    scrollToBottom()
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    sessionLoading.value = true
    const res = await getAiSessions()
    const list = res?.data ?? res ?? []
    const arr = Array.isArray(list) ? list : []
    sessions.value = arr
    if (arr.length > 0) {
      const latest = arr[0]
      currentSessionId.value = latest.id
      const msgRes = await getAiSessionMessages(latest.id)
      const msgList = msgRes?.data ?? msgRes ?? []
      const msgArr = Array.isArray(msgList) ? msgList : []
      messages.value = msgArr.length ? msgArr.map(mapApiMessageToView) : [...DEFAULT_WELCOME]
    } else {
      const createRes = await createAiSession('新对话')
      const data = createRes?.data ?? createRes
      if (data?.id) {
        currentSessionId.value = data.id
        sessions.value = [data]
        messages.value = [...DEFAULT_WELCOME]
      }
    }
    scrollToBottom()
  } catch (e) {
    console.error(e)
    const msg = e?.response?.data?.msg || e?.msg || '加载会话列表失败'
    ElMessage.error(msg)
  } finally {
    sessionLoading.value = false
  }
})
</script>

<style scoped>
.ai-chat-page {
  display: flex;
  /* 固定高度，避免主根出现滚动条，仅 ai-chat-messages 内部滚动 */
  height: calc(100vh - 84px);
  max-height: calc(100vh - 84px);
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  overflow: hidden;
}

.ai-chat-sidebar {
  width: 220px;
  flex-shrink: 0;
  margin: 0;
  min-height: 0;
  border-right: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
  background: #fafafa;
}

.ai-chat-sidebar-header {
  padding: 12px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.ai-chat-sidebar-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.ai-chat-session-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.ai-chat-session-item {
  padding: 10px 12px;
  cursor: pointer;
  border-left: 3px solid transparent;
  transition: background 0.15s;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.ai-chat-session-item:hover {
  background: #f0f2f5;
}

.ai-chat-session-info {
  flex: 1;
  min-width: 0;
}

.ai-chat-session-info .el-input {
  width: 100%;
}

.ai-chat-session-actions {
  flex-shrink: 0;
  display: flex;
  gap: 0;
  opacity: 0.7;
}

.ai-chat-session-item:hover .ai-chat-session-actions {
  opacity: 1;
}

.ai-chat-pin-icon {
  margin-right: 4px;
  vertical-align: middle;
  font-size: 12px;
  color: #e6a23c;
}

.ai-chat-more-btn {
  padding: 2px 4px;
}

.ai-chat-more-btn .el-icon {
  font-size: 16px;
}


.ai-chat-session-item.active {
  background: #ecf5ff;
  border-left-color: #409eff;
}

.ai-chat-session-title {
  display: block;
  font-size: 13px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ai-chat-session-time {
  display: block;
  font-size: 11px;
  color: #909399;
  margin-top: 2px;
}

.ai-chat-session-empty {
  padding: 16px 12px;
  font-size: 13px;
  color: #909399;
  text-align: center;
}

.ai-chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  min-height: 0;
  padding: 0 16px;
  overflow: hidden;
}

.ai-chat-header {
  padding: 16px 0 8px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 12px;
}

.ai-chat-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.ai-chat-subtitle {
  margin-top: 4px;
  font-size: 13px;
  color: #909399;
}

.ai-chat-body {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  margin-bottom: 4px;
}

.ai-chat-messages {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 8px 4px;
  -webkit-overflow-scrolling: touch;
}

.ai-chat-message {
  display: flex;
  margin-bottom: 12px;
}

.ai-chat-message.is-user {
  flex-direction: row-reverse;
}

.ai-chat-avatar {
  flex: 0 0 auto;
}

.ai-chat-content {
  max-width: 70%;
}

.ai-chat-message.is-user .ai-chat-content {
  align-items: flex-end;
}

.ai-chat-message-actions {
  margin: 4px 8px 0;
  display: flex;
  gap: 8px;
  font-size: 12px;
}

.ai-chat-message.is-user .ai-chat-message-actions {
  justify-content: flex-end;
}

.ai-chat-name {
  font-size: 12px;
  color: #909399;
  margin: 0 8px 4px;
}

.ai-chat-bubble {
  margin: 0 8px;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.ai-chat-message.is-user .ai-chat-bubble {
  background-color: #409eff;
  color: #fff;
}

.ai-chat-message.is-assistant .ai-chat-bubble {
  background-color: #f5f7fa;
  color: #303133;
}

.ai-chat-typing-wrapper {
  position: relative;
  display: inline-block;
}

.ai-chat-typing-visible {
  position: relative;
  z-index: 1;
}

.ai-chat-typing-fog {
  position: absolute;
  left: 0;
  top: 0;
  color: rgba(0, 0, 0, 0.18);
  filter: blur(2px);
  pointer-events: none;
}

.ai-chat-bubble-loading {
  font-style: italic;
  color: #909399;
}

.ai-chat-typing {
  font-style: italic;
  color: #909399;
}

.ai-chat-markdown {
  white-space: normal;
  word-break: break-word;
}

/* 代码块：语言标签 + 复制按钮头部，背景与消息气泡区分 */
.ai-chat-markdown :deep(.ai-code-block) {
  margin: 8px 0;
  border-radius: 8px;
  overflow: hidden;
  background: #eef0f3;
  border: 1px solid #e4e7ed;
}
.ai-chat-markdown :deep(.ai-code-block-header) {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 12px;
  border-bottom: 1px solid #e4e7ed;
  background: #e8eaef;
}
.ai-chat-markdown :deep(.ai-code-block-lang) {
  font-size: 12px;
  color: #606266;
}
.ai-chat-markdown :deep(.ai-code-block-copy) {
  font-size: 12px;
  color: #409eff;
  background: none;
  border: none;
  cursor: pointer;
  padding: 2px 6px;
}
.ai-chat-markdown :deep(.ai-code-block-copy:hover) {
  color: #66b1ff;
}
.ai-chat-markdown :deep(.ai-code-block pre) {
  margin: 0;
  padding: 12px;
  border-radius: 0;
  overflow-x: auto;
  background: #eef0f3;
  font-size: 13px;
  line-height: 1.5;
}
.ai-chat-markdown :deep(.ai-code-block pre code) {
  padding: 0;
  background: none;
}

.ai-chat-markdown :deep(pre) {
  margin: 8px 0;
  padding: 12px;
  border-radius: 6px;
  overflow-x: auto;
  background: #f6f8fa;
  font-size: 13px;
  line-height: 1.5;
}
.ai-chat-markdown :deep(code) {
  padding: 2px 6px;
  border-radius: 4px;
  background: #f0f0f0;
  font-size: 13px;
}
.ai-chat-markdown :deep(pre code) {
  padding: 0;
  background: none;
}
.ai-chat-markdown :deep(p) {
  margin: 0 0 8px;
}
.ai-chat-markdown :deep(ul),
.ai-chat-markdown :deep(ol) {
  margin: 0 0 8px;
  padding-left: 1.5em;
}

.ai-chat-input {
  border-top: 1px solid #ebeef5;
  padding-top: 8px;
}

.ai-chat-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.ai-chat-tip {
  font-size: 12px;
  color: #909399;
}
</style>

<style>
/* 会话列表下拉菜单（teleport 到 body）图标与文字对齐 */
.ai-chat-session-dropdown-menu.el-dropdown-menu .el-dropdown-menu__item {
  display: flex;
  align-items: center;
  gap: 8px;
}
.ai-chat-session-dropdown-menu.el-dropdown-menu .el-dropdown-menu__item .el-icon {
  font-size: 14px;
}
</style>
