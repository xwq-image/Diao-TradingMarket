import request from '@/utils/request'

export const getMyNotifications = (params) => {
  return request({
    url: '/notification/my',
    method: 'get',
    params
  })
}

export const markNotificationAsRead = (id) => {
  return request({
    url: `/notification/${id}/read`,
    method: 'put'
  })
}

export const markAllNotificationsAsRead = () => {
  return request({
    url: '/notification/read-all',
    method: 'put'
  })
}

export const getUnreadNotificationCount = () => {
  return request({
    url: '/notification/unread-count',
    method: 'get'
  })
}
