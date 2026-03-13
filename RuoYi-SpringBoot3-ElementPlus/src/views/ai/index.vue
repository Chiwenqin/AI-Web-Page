<template>
  <div class="ai-chat-page">
    <ChatSessionSidebar
      :sorted-sessions="session.sortedSessions?.value || []"
      :current-session-id="session.currentSessionId?.value"
      :session-loading="sessionLoading"
      :editing-session-id="session.editingSessionId?.value"
      :editing-title="session.editingTitle?.value"
      :format-session-time="session.formatSessionTime"
      :is-pinned="session.isPinned"
      @select="session.selectSession"
      @new="onNewChat"
      @rename="session.startEditTitle"
      @pin="session.togglePin"
      @delete="session.removeSession"
      @confirm-edit="session.confirmEditTitle"
      @cancel-edit="session.cancelEditTitle"
      @update:editing-title="updateSessionEditingTitle"
    />

    <div class="ai-chat-main">
      <div class="ai-chat-header">
        <div class="ai-chat-title">AI 助手</div>
        <div class="ai-chat-subtitle">基于 OpenAI / DeepSeek 的智能问答</div>
      </div>

      <div class="ai-chat-body">
        <ChatMessageList
          ref="messageListRef"
          :messages="messages"
          :loading="loading"
          :is-streaming="isStreaming"
          :user-initial="userInitial"
          :editing-message-index="editingMessageIndex"
          :editing-message-content="editingMessageContent"
          :collapsed-message-indexes="collapsedMessageIndexes"
          @start-edit="startEditUserMessage"
          @copy-user="handleCopyUserMessage"
          @copy-assistant="handleCopyMessage"
          @delete-user="handleDeleteUserMessage"
          @toggle-collapse="toggleCollapseMessage"
          @feedback="toggleAssistantFeedback"
          @confirm-edit="confirmEditUserMessage"
          @cancel-edit="cancelEditUserMessage"
          @regenerate="handleRegenerateLast"
          v-model:editing-message-content="editingMessageContent"
        />

        <div class="ai-chat-input">
          <el-input
            v-model="input"
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 6 }"
            placeholder="请输入你的问题，例如：帮我分析一下这个 SQL 的性能问题..."
            @keyup.enter.exact.prevent="handleSend"
          />
          <div class="ai-chat-actions">
            <div class="ai-chat-actions-left">
              <span class="ai-chat-tip">回车发送，Shift+回车换行</span>
              <el-tooltip
                effect="dark"
                content="关闭后仅使用本次提问，不携带历史对话，更适合一次性问题。"
                placement="top"
              >
                <div class="ai-chat-context-switch">
                  <span class="ai-chat-tip">携带上下文</span>
                  <el-switch v-model="useHistoryContext" size="small" />
                </div>
              </el-tooltip>
              <span v-if="isInputTooLong" class="ai-chat-length-tip">
                问题较长，可能影响响应速度
              </span>
            </div>
            <div class="ai-chat-actions-right">
              <el-dropdown
                v-hasPermi="['biz:ArchiveVillageList:export', 'biz:ArchiveBuildingList:export']"
                trigger="click"
                @command="handleExportData"
              >
                <el-button type="primary" link>
                  <el-icon><Download /></el-icon>
                  导出系统数据
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="village" v-hasPermi="['biz:ArchiveVillageList:export']">
                      导出小区管理数据
                    </el-dropdown-item>
                    <el-dropdown-item command="building" v-hasPermi="['biz:ArchiveBuildingList:export']">
                      导出楼栋管理数据
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-button type="primary" :loading="loading && !isStreaming" @click="handleSend">
                {{ isStreaming ? '停止' : '发送' }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { streamChatOpenAi } from '@/api/openAi'
import { Download } from '@element-plus/icons-vue'
import { download } from '@/utils/request'
import useUserStore from '@/store/modules/user'
import { copyText } from '@/composables/useCopy'
import { useAiChatSession } from '@/composables/useAiChatSession'
import ChatSessionSidebar from './ChatSessionSidebar.vue'
import ChatMessageList from './ChatMessageList.vue'

const DEFAULT_WELCOME = [
  {
    role: 'assistant',
    content: '你好，我是集成在若依后台中的 AI 助手，可以帮你分析代码、生成接口文档、编写 SQL、排查问题等。',
    isCode: false
  }
]

const userStore = useUserStore()
const messages = ref([...DEFAULT_WELCOME])
const session = useAiChatSession(messages, DEFAULT_WELCOME)

const input = ref('')
const loading = ref(false)
const isStreaming = ref(false)
const messageListRef = ref(null)
const editingMessageIndex = ref(null)
const editingMessageContent = ref('')
const collapsedMessageIndexes = ref([])
const useHistoryContext = ref(true)

let currentStreamAbort = null
let currentTypewriterStop = null
const MAX_HISTORY_TURNS = 10

const userInitial = computed(() => {
  if (userStore.name) {
    return userStore.name.charAt(0).toUpperCase()
  }
  return '我'
})

/** 解包 ref，确保子组件收到的 loading 是布尔值，能正确响应变化 */
const sessionLoading = computed(() => session.sessionLoading?.value ?? false)

const isInputTooLong = computed(() => {
  const text = input.value || ''
  return text.length > 2000
})

function updateSessionEditingTitle(v) {
  session.editingTitle.value = v
}

function scrollToBottom() {
  nextTick(() => {
    messageListRef.value?.scrollToBottom?.()
  })
}

function toggleCollapseMessage(index) {
  const arr = collapsedMessageIndexes.value.slice()
  const i = arr.indexOf(index)
  if (i >= 0) {
    arr.splice(i, 1)
  } else {
    arr.push(index)
  }
  collapsedMessageIndexes.value = arr
}

function startEditUserMessage({ msg, index }) {
  if (!msg || msg.role !== 'user') return
  editingMessageIndex.value = index
  editingMessageContent.value = msg.content || ''
}

function cancelEditUserMessage() {
  editingMessageIndex.value = null
  editingMessageContent.value = ''
}

async function confirmEditUserMessage(index) {
  if (
    index == null ||
    index < 0 ||
    index >= messages.value.length ||
    messages.value[index].role !== 'user'
  ) {
    cancelEditUserMessage()
    return
  }
  const newContent = (editingMessageContent.value || '').trim()
  if (!newContent) {
    ElMessage.warning('编辑后的内容不能为空')
    return
  }
  messages.value[index].content = newContent
  cancelEditUserMessage()
  if (loading.value || isStreaming.value) return
  await sendContentToAi(newContent, { fromReask: true })
}

function handleDeleteUserMessage(index) {
  if (index == null || index < 0 || index >= messages.value.length) return
  const msg = messages.value[index]
  if (!msg || msg.role !== 'user') return

  messages.value.splice(index, 1)
  collapsedMessageIndexes.value = (collapsedMessageIndexes.value || [])
    .filter((i) => i !== index)
    .map((i) => (i > index ? i - 1 : i))

  if (editingMessageIndex.value != null) {
    if (editingMessageIndex.value === index) {
      cancelEditUserMessage()
    } else if (editingMessageIndex.value > index) {
      editingMessageIndex.value -= 1
    }
  }
}

function toggleAssistantFeedback({ msg, type }) {
  if (!msg || msg.role !== 'assistant') return
  if (msg.feedback === type) {
    msg.feedback = undefined
  } else {
    msg.feedback = type
  }
}

function showAiError(targetMsg, message) {
  if (!targetMsg) return
  targetMsg.role = 'assistant'
  targetMsg.isError = true
  targetMsg.content = message || '调用 AI 接口失败，可稍后重试。'
}

async function callWithRetry(fn, retries = 1) {
  let lastError
  for (let attempt = 0; attempt <= retries; attempt += 1) {
    try {
      return await fn()
    } catch (e) {
      lastError = e
      const message = e?.message || ''
      const isNetworkError = !e?.response && /Network Error|Failed to fetch|网络错误/i.test(message)
      if (!isNetworkError || attempt === retries) {
        throw lastError
      }
    }
  }
  throw lastError
}

const buildHistoryMessages = () => {
  const list = messages.value || []
  if (!useHistoryContext.value) {
    for (let i = list.length - 1; i >= 0; i -= 1) {
      if (list[i].role === 'user') {
        return [{ role: 'USER', message: list[i].content || '' }]
      }
    }
    return []
  }

  const limited = []
  let userCount = 0
  for (let i = list.length - 1; i >= 0; i -= 1) {
    const m = list[i]
    limited.push(m)
    if (m.role === 'user') {
      userCount += 1
      if (userCount >= MAX_HISTORY_TURNS) break
    }
  }
  limited.reverse()

  return limited.map((m) => ({
    role: m.role === 'user' ? 'USER' : 'ASSISTANT',
    message: m.content
  }))
}

async function handleCopyMessage(msg) {
  await copyText(msg?.content || '')
}

async function handleCopyUserMessage(msg) {
  if (!msg || msg.role !== 'user') return
  await copyText(msg.content || '')
}

const TYPEWRITER_INTERVAL = 30
const TYPEWRITER_STEP = 1
const MAX_TYPEWRITER_DURATION = 20000

async function runTypewriter(assistantMsg, getStreamPromise, onScroll) {
  const streamTarget = ref('')
  let timer = null
  let streamDone = false
  let streamResult = ''
  const startTime = Date.now()

  let stopped = false
  currentTypewriterStop = () => {
    stopped = true
  }

  return new Promise((resolve, reject) => {
    timer = setInterval(() => {
      if (stopped) {
        if (timer) clearInterval(timer)
        assistantMsg.content = streamTarget.value
        onScroll()
        resolve(streamResult)
        return
      }

      const targetLen = streamTarget.value.length
      const currentLen = assistantMsg.content.length

      if (Date.now() - startTime > MAX_TYPEWRITER_DURATION) {
        if (timer) clearInterval(timer)
        assistantMsg.content = streamResult || streamTarget.value
        onScroll()
        resolve(streamResult)
        return
      }

      if (currentLen < targetLen) {
        const remaining = targetLen - currentLen
        if (streamDone && remaining > 800) {
          assistantMsg.content = streamTarget.value
          onScroll()
          if (timer) clearInterval(timer)
          resolve(streamResult)
          return
        }

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

function stopCurrentStream() {
  if (!isStreaming.value) return
  if (typeof currentStreamAbort === 'function') {
    try {
      currentStreamAbort()
    } catch (e) {
      console.error(e)
    }
  }
  if (typeof currentTypewriterStop === 'function') {
    try {
      currentTypewriterStop()
    } catch (e) {
      console.error(e)
    }
  }
  isStreaming.value = false
  loading.value = false
  currentStreamAbort = null
  currentTypewriterStop = null
}

async function handleRegenerateLast() {
  if (loading.value || isStreaming.value) return
  if (!messages.value.length) return

  let lastAssistantIndex = -1
  for (let i = messages.value.length - 1; i >= 0; i -= 1) {
    if (messages.value[i].role === 'assistant') {
      lastAssistantIndex = i
      break
    }
  }
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

  const sid = await session.ensureCurrentSession()
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
  isStreaming.value = true
  try {
    const assistantContent = await runTypewriter(
      assistantMsg,
      (streamTarget) =>
        callWithRetry(
          () =>
            streamChatOpenAi(
              historyMessages,
              (fullText) => {
                streamTarget.value = fullText
              },
              {
                onAbort(controller) {
                  currentStreamAbort = () => controller.abort()
                }
              }
            ),
          1
        ),
      scrollToBottom
    )
    await session.persistCurrentMessages(undefined, lastUserContent, assistantContent)
    scrollToBottom()
  } catch (e) {
    console.error(e)
    showAiError(assistantMsg, '重新生成失败，请稍后重试。')
    await session.persistCurrentMessages(undefined, lastUserContent, assistantMsg.content || '')
    scrollToBottom()
  } finally {
    loading.value = false
    isStreaming.value = false
    currentStreamAbort = null
    currentTypewriterStop = null
  }
}

function handleExportData(command) {
  if (command === 'village') {
    download('biz/ArchiveVillageList/export', {}, `小区列_${Date.now()}.xlsx`)
  } else if (command === 'building') {
    download('biz/ArchiveBuildingList/export', {}, `楼栋信息_${Date.now()}.xlsx`)
  }
}

async function onNewChat() {
  const ok = await session.newChat()
  if (ok) scrollToBottom()
}

const handleSend = async () => {
  const content = input.value.trim()
  if (!content) return

  if (isStreaming.value) {
    stopCurrentStream()
    return
  }
  if (loading.value) return

  const exportVillage = /\导出/.test(content) && /小区|小区管理/.test(content)
  const exportBuilding = /\导出/.test(content) && /楼栋|楼栋管理/.test(content)
  if (exportVillage || exportBuilding) {
    const sid = await session.ensureCurrentSession()
    if (!sid) return
    messages.value.push({ role: 'user', content, isCode: false })
    if (exportVillage) {
      download('biz/ArchiveVillageList/export', {}, `小区列_${Date.now()}.xlsx`)
      messages.value.push({ role: 'assistant', content: '已为你导出小区管理数据，请查看浏览器下载。', isCode: false })
    } else {
      download('biz/ArchiveBuildingList/export', {}, `楼栋信息_${Date.now()}.xlsx`)
      messages.value.push({ role: 'assistant', content: '已为你导出楼栋管理数据，请查看浏览器下载。', isCode: false })
    }
    input.value = ''
    scrollToBottom()
    return
  }
  await sendContentToAi(content)
  input.value = ''
}

async function sendContentToAi(content, { fromReask = false } = {}) {
  const text = (content || '').trim()
  if (!text) return

  const sid = await session.ensureCurrentSession()
  if (!sid) return
  const isFirstUserMessage = messages.value.filter((m) => m.role === 'user').length === 0
  const sessionTitle = text.slice(0, 20) + (text.length > 20 ? '...' : '')

  messages.value.push({
    role: 'user',
    content: text,
    isCode: false
  })
  scrollToBottom()

  const history = buildHistoryMessages()

  loading.value = true
  isStreaming.value = true
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
      (streamTarget) =>
        callWithRetry(
          () =>
            streamChatOpenAi(
              history,
              (fullText) => {
                streamTarget.value = fullText
              },
              {
                onAbort(controller) {
                  currentStreamAbort = () => controller.abort()
                }
              }
            ),
          1
        ),
      scrollToBottom
    )
    await session.persistCurrentMessages(isFirstUserMessage ? sessionTitle : undefined, text, assistantContent)
    scrollToBottom()
  } catch (e) {
    console.error(e)
    showAiError(assistantMsg, '调用 AI 接口失败，请稍后重试或联系管理员检查 `/api/ai/chat/stream` 接口。')
    await session.persistCurrentMessages(undefined, text, assistantMsg.content || '')
    scrollToBottom()
  } finally {
    loading.value = false
    isStreaming.value = false
    currentStreamAbort = null
    currentTypewriterStop = null
  }
}

onMounted(async () => {
  await session.loadSessions()
  scrollToBottom()
})

onBeforeUnmount(() => {
  stopCurrentStream()
})
</script>

<style scoped>
.ai-chat-page {
  display: flex;
  height: calc(100vh - 84px);
  max-height: calc(100vh - 84px);
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  overflow: hidden;
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

.ai-chat-actions-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-chat-actions-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-chat-context-switch {
  display: flex;
  align-items: center;
  gap: 6px;
}

.ai-chat-length-tip {
  font-size: 12px;
  color: #e6a23c;
}
</style>
