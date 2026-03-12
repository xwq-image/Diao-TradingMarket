import request from '@/utils/request'

// 提交认证申请
export const applyVerification = (data) => {
  return request({
    url: '/verification/apply',
    method: 'post',
    data
  })
}

// 获取我的认证信息
export const getMyVerification = () => {
  return request({
    url: '/verification/my',
    method: 'get'
  })
}

// 获取认证列表（管理员）
export const getVerificationList = (status) => {
  return request({
    url: '/verification/list',
    method: 'get',
    params: { status }
  })
}

// 通过认证（管理员）
export const approveVerification = (id) => {
  return request({
    url: `/verification/${id}/approve`,
    method: 'put'
  })
}

// 驳回认证（管理员）
export const rejectVerification = (id, reason) => {
  return request({
    url: `/verification/${id}/reject`,
    method: 'put',
    params: { reason }
  })
}
