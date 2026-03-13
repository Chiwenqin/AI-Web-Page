<template>
  <div class="ai-chat-messages" ref="scrollRef" @click="onMarkdownCopy">
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
        <div
          class="ai-chat-bubble"
          :class="{ 'ai-chat-bubble-error': msg.role === 'assistant' && msg.isError }"
        >
          <template v-if="msg.role === 'assistant'">
            <div
              v-if="msg.content"
              class="ai-chat-markdown"
              :class="{ 'is-collapsed': isCollapsed(index, msg) }"
            >
              <template v-if="isTyping(index)">
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
            <div v-if="editingMessageIndex === index" class="ai-chat-editing">
              <el-input
                :model-value="editingMessageContent"
                type="textarea"
                :autosize="{ minRows: 1, maxRows: 4 }"
                size="small"
                @update:model-value="$emit('update:editingMessageContent', $event)"
              />
              <div class="ai-chat-editing-actions">
                <el-button type="primary" size="small" @click="$emit('confirmEdit', index)">
                  完成编辑
                </el-button>
                <el-button size="small" @click="$emit('cancelEdit')">
                  取消
                </el-button>
              </div>
            </div>
            <div v-else class="ai-chat-text">{{ msg.content }}</div>
          </template>
        </div>
        <div
          v-if="msg.role === 'assistant' && shouldShowCollapse(msg)"
          class="ai-chat-collapse-toggle"
        >
          <el-button type="primary" link size="small" @click="$emit('toggleCollapse', index)">
            {{ isCollapsed(index, msg) ? '展开全部' : '收起' }}
          </el-button>
        </div>
        <div v-if="msg.role === 'assistant'" class="ai-chat-message-actions">
          <el-button type="primary" link size="small" @click="$emit('feedback', { msg, type: 'like' })">
            {{ msg.feedback === 'like' ? '已赞' : '赞' }}
          </el-button>
          <el-button type="primary" link size="small" @click="$emit('feedback', { msg, type: 'dislike' })">
            {{ msg.feedback === 'dislike' ? '已踩' : '踩' }}
          </el-button>
          <el-button type="primary" link size="small" @click="$emit('copyAssistant', msg)">
            复制内容
          </el-button>
          <el-button
            v-if="isLastAssistant(index)"
            type="primary"
            link
            size="small"
            :disabled="loading"
            @click="$emit('regenerate')"
          >
            重新生成
          </el-button>
        </div>
        <div v-else class="ai-chat-message-actions">
          <el-button type="primary" link size="small" @click="$emit('startEdit', { msg, index })">
            编辑
          </el-button>
          <el-button type="primary" link size="small" @click="$emit('copyUser', msg)">
            复制
          </el-button>
          <el-button type="primary" link size="small" @click="$emit('deleteUser', index)">
            删除
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'
import { handleMarkdownCopy } from '@/composables/useCopy'

const props = defineProps({
  messages: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  isStreaming: { type: Boolean, default: false },
  userInitial: { type: String, default: '我' },
  editingMessageIndex: { type: Number, default: null },
  editingMessageContent: { type: String, default: '' },
  collapsedMessageIndexes: { type: Array, default: () => [] }
})

defineEmits([
  'startEdit',
  'copyUser',
  'copyAssistant',
  'deleteUser',
  'toggleCollapse',
  'feedback',
  'confirmEdit',
  'cancelEdit',
  'regenerate',
  'update:editingMessageContent'
])

const scrollRef = ref(null)

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
renderer.code = function (code, lang) {
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

function fogTail(text) {
  if (!text) return ''
  const str = String(text)
  const tailLen = Math.min(20, str.length)
  return str.slice(-tailLen)
}

function isLastAssistant(index) {
  for (let i = props.messages.length - 1; i >= 0; i -= 1) {
    if (props.messages[i].role === 'assistant') {
      return i === index
    }
  }
  return false
}

function isTyping(index) {
  return props.loading && isLastAssistant(index)
}

function shouldShowCollapse(msg) {
  if (!msg || msg.role !== 'assistant') return false
  const text = msg.content || ''
  return text.length > 800
}

function isCollapsed(index, msg) {
  if (!shouldShowCollapse(msg)) return false
  return (props.collapsedMessageIndexes || []).includes(index)
}

function onMarkdownCopy(e) {
  handleMarkdownCopy(e)
}

function scrollToBottom() {
  nextTick(() => {
    const el = scrollRef.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

defineExpose({ scrollToBottom })
</script>

<style scoped>
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
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.15s;
}

.ai-chat-message:hover .ai-chat-message-actions {
  opacity: 1;
  pointer-events: auto;
}

.ai-chat-message.is-user .ai-chat-message-actions {
  justify-content: flex-end;
}

.ai-chat-editing {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.ai-chat-editing-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  font-size: 12px;
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

.ai-chat-bubble-error {
  background-color: #fff7e6;
  border: 1px solid #f3d19e;
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

.ai-chat-typing {
  font-style: italic;
  color: #909399;
}

.ai-chat-markdown {
  white-space: normal;
  word-break: break-word;
}

.ai-chat-markdown.is-collapsed {
  max-height: 200px;
  overflow: hidden;
}

.ai-chat-collapse-toggle {
  margin: 4px 8px 0;
  display: flex;
  justify-content: flex-end;
}

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
</style>
