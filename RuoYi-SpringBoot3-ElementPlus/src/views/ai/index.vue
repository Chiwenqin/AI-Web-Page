<template>
  <div class="ai-chat-page">
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
              <pre v-if="msg.isCode" class="ai-chat-code">{{ msg.content }}</pre>
              <div v-else class="ai-chat-text">
                {{ msg.content }}
              </div>
            </div>
          </div>
        </div>

        <div v-if="loading" class="ai-chat-message is-assistant">
          <div class="ai-chat-avatar">
            <el-avatar :size="32">AI</el-avatar>
          </div>
          <div class="ai-chat-content">
            <div class="ai-chat-name">AI 助手</div>
            <div class="ai-chat-bubble ai-chat-bubble-loading">
              正在思考中，请稍候...
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
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { listenOpenAiApi } from '@/api/openAi'
import useUserStore from '@/store/modules/user'

const userStore = useUserStore()

const input = ref('')
const loading = ref(false)
const messageListRef = ref(null)

const messages = ref([
  {
    role: 'assistant',
    content: '你好，我是集成在若依后台中的 AI 助手，可以帮你分析代码、生成接口文档、编写 SQL、排查问题等。',
    isCode: false
  }
])

const userInitial = computed(() => {
  if (userStore.name) {
    return userStore.name.charAt(0).toUpperCase()
  }
  return '我'
})

const scrollToBottom = () => {
  nextTick(() => {
    const el = messageListRef.value
    if (el) {
      el.scrollTop = el.scrollHeight
    }
  })
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

  // 追加用户消息
  messages.value.push({
    role: 'user',
    content,
    isCode: false
  })
  input.value = ''
  scrollToBottom()

  loading.value = true
  try {
    const history = buildHistoryMessages()
    const res = await listenOpenAiApi(history)
    const reply =
      typeof res === 'string'
        ? res
        : res?.data ?? res?.content ?? 'AI 未返回内容，请检查后端配置。'

    messages.value.push({
      role: 'assistant',
      content: reply,
      isCode: false
    })
    scrollToBottom()
  } catch (e) {
    messages.value.push({
      role: 'assistant',
      content: '调用 AI 接口失败，请稍后重试或联系管理员检查 `/api/ai/chat/stream` 接口。',
      isCode: false
    })
    scrollToBottom()
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.ai-chat-page {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 16px;
  box-sizing: border-box;
}

.ai-chat-header {
  padding-bottom: 8px;
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
}

.ai-chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 8px 4px;
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
