import request from '@/utils/request'

export const listenOpenAiApi = (data) => {
  return request({
    url: '/api/ai/chat/stream',
    method: 'post',
    data
  })
}