import { ElMessage } from 'element-plus'

function fallbackCopy(text: string) {
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

export async function copyText(text: string) {
  const value = text || ''
  if (!value) {
    ElMessage.warning('没有可复制的内容')
    return
  }
  try {
    if (navigator.clipboard && navigator.clipboard.writeText) {
      await navigator.clipboard.writeText(value)
    } else {
      const textarea = document.createElement('textarea')
      textarea.value = value
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

/**
 * 处理 Markdown 代码块复制按钮点击，委托给父元素使用 @click="handleMarkdownCopy"
 */
export function handleMarkdownCopy(e: MouseEvent) {
  const btn = (e.target as HTMLElement).closest('.ai-code-block-copy')
  if (!btn) return
  const block = (e.target as HTMLElement).closest('.ai-code-block')
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
