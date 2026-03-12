import request from '@/utils/request'

// 创建或获取聊天会话
export const createOrGetSession = (otherUserId, goodsId) => {
  return request({
    url: '/chat/session',
    method: 'post',
    params: { otherUserId, goodsId }
  })
}

// 发送消息
export const sendChatMessage = (data) => {
  return request({
    url: '/chat/message',
    method: 'post',
    data
  })
}

// 获取我的会话列表
export const getMyChatSessions = () => {
  return request({
    url: '/chat/sessions',
    method: 'get'
  })
}

// 获取会话消息列表
export const getSessionMessages = (sessionId) => {
  return request({
    url: `/chat/session/${sessionId}/messages`,
    method: 'get'
  })
}

// 标记消息为已读
export const markChatMessagesAsRead = (sessionId) => {
  return request({
    url: `/chat/session/${sessionId}/read`,
    method: 'put'
  })
}

// 获取未读消息总数
export const getUnreadChatCount = () => {
  return request({
    url: '/chat/unread-count',
    method: 'get'
  })
}
