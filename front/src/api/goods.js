import request from '@/utils/request'

export const getGoodsList = (params) => {
  return request({
    url: '/goods/list',
    method: 'get',
    params
  })
}

export const getMyGoodsList = (params) => {
  return request({
    url: '/user/goods',  // 修改为 /user/goods
    method: 'get',
    params
  })
}

export const getGoodsDetail = (id) => {
  return request({
    url: `/goods/${id}`,
    method: 'get'
  })
}

export const publishGoods = (data) => {
  return request({
    url: '/goods/publish',
    method: 'post',
    data
  })
}

export const updateGoods = (id, data) => {
  return request({
    url: `/goods/${id}`,
    method: 'put',
    data
  })
}

export const deleteGoods = (id) => {
  return request({
    url: `/goods/${id}`,
    method: 'delete'
  })
}

export const updateGoodsStatus = (id, status) => {
  return request({
    url: `/goods/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export const uploadGoodsImages = (files) => {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  return request({
    url: '/goods/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
