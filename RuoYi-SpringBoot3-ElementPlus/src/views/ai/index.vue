<template>
  <div class="ai-iframe-container">
    <p>正在打开 AI 助手...</p>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'

// 组件挂载时自动在新标签页中打开AI助手
onMounted(() => {
  // 打开新标签页
  window.open('http://localhost:5173', '_blank')
  // 关闭当前页面
  window.close()
})
</script>

<style scoped>
.ai-iframe-container {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
}
</style>
