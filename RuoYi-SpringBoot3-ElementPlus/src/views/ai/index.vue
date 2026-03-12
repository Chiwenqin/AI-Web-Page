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
          v-for="s in sessions"
          :key="s.id"
          class="ai-chat-session-item"
          :class="{ active: currentSessionId === s.id }"
          @click="selectSession(s.id)"
        >
          <span class="ai-chat-session-title">{{ s.title }}</span>
          <span class="ai-chat-session-time">{{ formatSessionTime(s.updateTime) }}</span>
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
        <div class="ai-chat-messages" ref="messageListRef">
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
                  <div v-if="msg.content" class="ai-chat-markdown" v-html="renderMarkdown(msg.content)"></div>
                  <div v-else-if="loading" class="ai-chat-text ai-chat-typing">正在思考中...</div>
                  <div v-else class="ai-chat-text">{{ msg.content }}</div>
                </template>
                <template v-else>
                  <div class="ai-chat-text">{{ msg.content }}</div>
                </template>
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
  updateAiSessionTitle
} from '@/api/openAi'
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

const userInitial = computed(() => {
  if (userStore.name) {
    return userStore.name.charAt(0).toUpperCase()
  }
  return '我'
})

// Markdown + 代码高亮
marked.setOptions({
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
    const assistantContent = await streamChatOpenAi(history, (fullText) => {
      assistantMsg.content = fullText
      scrollToBottom()
    })
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
}

.ai-chat-session-item:hover {
  background: #f0f2f5;
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
