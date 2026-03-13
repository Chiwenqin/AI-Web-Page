<template>
  <aside v-loading="sessionLoading" class="ai-chat-sidebar">
    <div class="ai-chat-sidebar-header">
      <span class="ai-chat-sidebar-title">历史对话</span>
      <el-button type="primary" size="small" @click="$emit('new')">新对话</el-button>
    </div>
    <div class="ai-chat-session-list">
      <div
        v-for="s in sortedSessions"
        :key="s?.id"
        class="ai-chat-session-item"
        :class="{ active: s != null && currentSessionId == s.id }"
        @click="s != null && s.id != null && $emit('select', s.id)"
      >
        <div class="ai-chat-session-info" @click="s != null && editingSessionId === s.id && $event.stopPropagation()">
          <template v-if="s != null && editingSessionId === s.id">
            <el-input
              :model-value="editingTitle"
              size="small"
              placeholder="对话名称"
              @update:model-value="$emit('update:editingTitle', $event)"
              @keyup.enter="$emit('confirmEdit', s.id)"
              @keyup.escape="$emit('cancelEdit')"
            />
          </template>
          <template v-else-if="s != null">
            <span class="ai-chat-session-title">
              <el-icon v-if="isPinned(s.id)" class="ai-chat-pin-icon"><Top /></el-icon>
              {{ s.title }}
            </span>
            <span class="ai-chat-session-time">{{ formatSessionTime(s.updateTime) }}</span>
          </template>
        </div>
        <div v-if="s != null && editingSessionId !== s.id" class="ai-chat-session-actions" @click.stop>
          <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, s)">
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
      <div v-if="!sortedSessions.length" class="ai-chat-session-empty">暂无对话，点击「新对话」开始</div>
    </div>
  </aside>
</template>

<script setup>
import { Top, MoreFilled, Edit, Delete } from '@element-plus/icons-vue'

const props = defineProps({
  sortedSessions: { type: Array, default: () => [] },
  currentSessionId: { type: [String, Number], default: null },
  sessionLoading: { type: Boolean, default: false },
  editingSessionId: { type: [String, Number], default: null },
  editingTitle: { type: String, default: '' },
  formatSessionTime: { type: Function, required: true },
  isPinned: { type: Function, required: true }
})

const emit = defineEmits(['select', 'new', 'rename', 'pin', 'delete', 'confirmEdit', 'cancelEdit', 'update:editingTitle'])

function handleCommand(command, s) {
  if (command === 'rename') emit('rename', s)
  else if (command === 'pin') emit('pin', s.id)
  else if (command === 'delete') emit('delete', s.id)
}
</script>

<style scoped>
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

.ai-chat-session-info :deep(.el-input) {
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
</style>

<style>
.ai-chat-session-dropdown-menu.el-dropdown-menu .el-dropdown-menu__item {
  display: flex;
  align-items: center;
  gap: 8px;
}
.ai-chat-session-dropdown-menu.el-dropdown-menu .el-dropdown-menu__item .el-icon {
  font-size: 14px;
}
</style>
