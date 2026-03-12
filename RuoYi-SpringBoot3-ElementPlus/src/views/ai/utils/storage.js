/**
 * AI 助手会话与消息的 localStorage 读写，按 userId 隔离
 */

const SESSIONS_PREFIX = 'ai_sessions_'
const MESSAGES_PREFIX = 'ai_messages_'

function getSessionsKey(userId) {
  return SESSIONS_PREFIX + (userId || 'anonymous')
}

function getMessagesKey(sessionId) {
  return MESSAGES_PREFIX + sessionId
}

/** 生成会话唯一 id */
export function generateSessionId() {
  return 's_' + Date.now().toString(36) + '_' + Math.random().toString(36).slice(2, 10)
}

/**
 * 获取当前用户的会话列表
 * @param {string} userId
 * @returns {Array<{ id, title, updatedAt }>}
 */
export function getSessions(userId) {
  try {
    const key = getSessionsKey(userId)
    const raw = localStorage.getItem(key)
    if (!raw) return []
    const list = JSON.parse(raw)
    return Array.isArray(list) ? list : []
  } catch {
    return []
  }
}

/**
 * 保存当前用户的会话列表（按 updatedAt 倒序）
 * @param {string} userId
 * @param {Array<{ id, title, updatedAt }>} sessions
 */
export function saveSessions(userId, sessions) {
  const key = getSessionsKey(userId)
  const list = [...sessions].sort((a, b) => (b.updatedAt || 0) - (a.updatedAt || 0))
  localStorage.setItem(key, JSON.stringify(list))
}

/**
 * 获取某会话的消息列表
 * @param {string} sessionId
 * @returns {Array<{ role, content, isCode? }>}
 */
export function getMessages(sessionId) {
  if (!sessionId) return []
  try {
    const key = getMessagesKey(sessionId)
    const raw = localStorage.getItem(key)
    if (!raw) return []
    const list = JSON.parse(raw)
    return Array.isArray(list) ? list : []
  } catch {
    return []
  }
}

/**
 * 保存某会话的消息列表
 * @param {string} sessionId
 * @param {Array<{ role, content, isCode? }>} messages
 */
export function saveMessages(sessionId, messages) {
  if (!sessionId) return
  const key = getMessagesKey(sessionId)
  localStorage.setItem(key, JSON.stringify(messages))
}

/**
 * 添加会话（写入 sessions 并更新 localStorage）
 * @param {string} userId
 * @param {string} sessionId
 * @param {string} title
 */
export function addSession(userId, sessionId, title) {
  const sessions = getSessions(userId)
  const now = Date.now()
  sessions.unshift({ id: sessionId, title: title || '新对话', updatedAt: now })
  saveSessions(userId, sessions)
}

/**
 * 更新会话的 title 和 updatedAt
 * @param {string} userId
 * @param {string} sessionId
 * @param {string} title
 */
export function updateSession(userId, sessionId, title) {
  const sessions = getSessions(userId)
  const now = Date.now()
  const idx = sessions.findIndex((s) => s.id === sessionId)
  if (idx >= 0) {
    sessions[idx].title = title || sessions[idx].title
    sessions[idx].updatedAt = now
  } else {
    sessions.unshift({ id: sessionId, title: title || '新对话', updatedAt: now })
  }
  saveSessions(userId, sessions)
}

/**
 * 删除会话及其消息
 * @param {string} userId
 * @param {string} sessionId
 */
export function removeSession(userId, sessionId) {
  const sessions = getSessions(userId).filter((s) => s.id !== sessionId)
  saveSessions(userId, sessions)
  localStorage.removeItem(getMessagesKey(sessionId))
}
