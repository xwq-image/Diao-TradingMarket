import request from '@/utils/request'

export const sendMessage = (data) => {
  return request({
    url: '/message/send',
    method: 'post',
    data
  })
}

export const getGoodsMessages = (goodsId) => {
  return request({
    url: `/message/goods/${goodsId}`,
    method: 'get'
  })
}

export const getMyMessages = () => {
  return request({
    url: '/message/my',
    method: 'get'
  })
}

export const markAsRead = (id) => {
  return request({
    url: `/message/${id}/read`,
    method: 'put'
  })
}

export const getUnreadCount = () => {
  return request({
    url: '/message/unread-count',
    method: 'get'
  })
}
