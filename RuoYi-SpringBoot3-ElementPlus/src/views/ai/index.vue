<template>
  <div v-loading="loading" :style="'height:' + height" class="ai-iframe-container">
    <iframe
      :src="url"
      frameborder="no"
      style="width: 100%; height: 100%"
      scrolling="auto"
      @load="onLoad"
    />
    <div v-if="error" class="error-tip">
      <el-result
        icon="warning"
        title="无法访问 AI 助手"
        sub-title="请确保 AI 项目已启动，访问地址：http://localhost:5173"
      >
        <template #extra>
          <el-button type="primary" @click="reload">重新加载</el-button>
          <el-button @click="openNewWindow">新窗口打开</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  src: {
    type: String,
    default: 'http://localhost:5173'
  }
})

const height = ref(document.documentElement.clientHeight - 94.5 + 'px;')
const loading = ref(true)
const error = ref(false)
const url = computed(() => props.src)

const onLoad = () => {
  setTimeout(() => {
    loading.value = false
  }, 300)
}

const reload = () => {
  error.value = false
  loading.value = true
  window.location.reload()
}

const openNewWindow = () => {
  window.open(url.value, '_blank')
}

onMounted(() => {
  window.onresize = function temp() {
    height.value = document.documentElement.clientHeight - 94.5 + 'px;'
  }
})
</script>

<style scoped>
.ai-iframe-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.error-tip {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  z-index: 10;
}
</style>
