import request from '@/utils/request'
import { getToken } from '@/utils/auth'

/**
 * 流式调用 AI 对话接口，边收边回调
 * @param {Array<{ role: string, message: string }>} historyMessages 历史消息（role: USER/ASSISTANT/SYSTEM）
 * @param { (fullText: string) => void } onChunk 每收到一段就调用，参数为当前已收到的完整文本
 * @param {{ signal?: AbortSignal, onAbort?: (controller: AbortController) => void }} [options] 取消控制等可选参数
 * @returns { Promise<string> } 流式结束后 resolve 最终完整内容（已去掉 [DONE]）
 */
export function streamChatOpenAi(historyMessages, onChunk, options = {}) {
  const baseURL = import.meta.env.VITE_APP_BASE_API || ''
  const url = baseURL + '/api/ai/chat/stream'
  const token = getToken()

  let fullText = ''

  return new Promise((resolve, reject) => {
    let controller
    let signal

    if (options.signal) {
      signal = options.signal
    } else if (typeof AbortController !== 'undefined') {
      controller = new AbortController()
      signal = controller.signal
      if (typeof options.onAbort === 'function') {
        options.onAbort(controller)
      }
    }

    const fetchOptions = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: 'Bearer ' + token } : {})
      },
      body: JSON.stringify(historyMessages)
    }

    if (signal) {
      fetchOptions.signal = signal
    }

    fetch(url, fetchOptions)
      .then(async (response) => {
        if (!response.ok) {
          const errText = await response.text()
          throw new Error(errText || '请求失败')
        }
        const reader = response.body.getReader()
        const decoder = new TextDecoder('utf-8')
        while (true) {
          const { done, value } = await reader.read()
          if (done) break
          fullText += decoder.decode(value, { stream: true })
          const doneIndex = fullText.indexOf('[DONE]')
          if (doneIndex !== -1) {
            fullText = fullText.slice(0, doneIndex).trimEnd()
            if (onChunk) onChunk(fullText)
            break
          }
          if (onChunk) onChunk(fullText)
        }
        resolve(fullText)
      })
      .catch((err) => {
        // 中断请求时返回当前已累积的内容，视为“正常停止”
        if (err && err.name === 'AbortError') {
          resolve(fullText)
          return
        }
        reject(err)
      })
  })
}

/** 当前用户的会话列表 */
export const getAiSessions = () => {
  return request({ url: '/api/ai/sessions', method: 'get' })
}

/** 某会话的消息列表 */
export const getAiSessionMessages = (sessionId) => {
  return request({ url: `/api/ai/sessions/${sessionId}/messages`, method: 'get' })
}

/** 创建会话，body 可选 { title } */
export const createAiSession = (title = '新对话') => {
  return request({ url: '/api/ai/sessions', method: 'post', data: { title } })
}

/** 更新会话标题 */
export const updateAiSessionTitle = (sessionId, title) => {
  return request({ url: `/api/ai/sessions/${sessionId}`, method: 'put', data: { title } })
}

/** 删除会话（及该会话下所有消息）。使用 POST 避免代理/网关不支持 DELETE 导致 500 */
export const deleteAiSession = (sessionId) => {
  return request({ url: `/api/ai/sessions/${sessionId}/delete`, method: 'post' })
}

/** 批量保存消息，messages: [{ role, content }] */
export const saveAiMessages = (sessionId, messages) => {
  return request({ url: `/api/ai/sessions/${sessionId}/messages`, method: 'post', data: messages })
}