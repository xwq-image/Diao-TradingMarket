import request from '@/utils/request'

export const createOrder = (data) => {
  return request({
    url: '/order/create',
    method: 'post',
    data
  })
}

export const getMyBuyOrders = (params) => {
  return request({
    url: '/order/buy',
    method: 'get',
    params
  })
}

export const getMySellOrders = (params) => {
  return request({
    url: '/order/sell',
    method: 'get',
    params
  })
}

export const confirmOrder = (id) => {
  return request({
    url: `/order/${id}/confirm`,
    method: 'put'
  })
}

export const payOrder = (id, password) => {
  return request({
    url: `/order/${id}/pay`,
    method: 'put',
    params: { password }
  })
}

export const shipOrder = (id) => {
  return request({
    url: `/order/${id}/ship`,
    method: 'put'
  })
}

export const completeOrder = (id) => {
  return request({
    url: `/order/${id}/complete`,
    method: 'put'
  })
}

export const cancelOrder = (id) => {
  return request({
    url: `/order/${id}/cancel`,
    method: 'put'
  })
}
