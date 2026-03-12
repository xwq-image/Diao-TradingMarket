import request from '@/utils/request'

// 提交举报
export const submitReport = (data) => {
  return request({
    url: '/report/submit',
    method: 'post',
    data
  })
}

// 获取我的举报记录
export const getMyReports = () => {
  return request({
    url: '/report/my',
    method: 'get'
  })
}

// 获取举报列表（管理员）
export const getReportList = (status) => {
  return request({
    url: '/report/list',
    method: 'get',
    params: { status }
  })
}

// 通过举报（管理员）
export const approveReport = (id, creditDeduction, adminNote) => {
  return request({
    url: `/report/${id}/approve`,
    method: 'put',
    params: { creditDeduction, adminNote }
  })
}

// 驳回举报（管理员）
export const rejectReport = (id, adminNote) => {
  return request({
    url: `/report/${id}/reject`,
    method: 'put',
    params: { adminNote }
  })
}
