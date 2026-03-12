import request from '@/utils/request'

// 用户管理
export const getUserList = (params) => {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

export const updateUserStatus = (id, status) => {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export const verifyUser = (id, status) => {
  return request({
    url: `/admin/users/${id}/verify`,
    method: 'put',
    params: { status }
  })
}

// 商品审核
export const getPendingGoods = (params) => {
  return request({
    url: '/admin/goods/pending',
    method: 'get',
    params
  })
}

export const approveGoods = (id) => {
  return request({
    url: `/admin/goods/${id}/approve`,
    method: 'put'
  })
}

export const rejectGoods = (id, reason) => {
  return request({
    url: `/admin/goods/${id}/reject`,
    method: 'put',
    params: { reason }
  })
}

export const offlineGoods = (id, reason) => {
  return request({
    url: `/admin/goods/${id}/offline`,
    method: 'put',
    params: { reason }
  })
}

// 数据统计
export const getStatistics = () => {
  return request({
    url: '/admin/statistics',
    method: 'get'
  })
}
