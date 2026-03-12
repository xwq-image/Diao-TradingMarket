import request from '@/utils/request'

// 获取余额
export const getBalance = () => {
  return request({
    url: '/wallet/balance',
    method: 'get'
  })
}

// 充值
export const recharge = (amount) => {
  return request({
    url: '/wallet/recharge',
    method: 'post',
    params: { amount }
  })
}

// 获取交易记录
export const getTransactions = () => {
  return request({
    url: '/wallet/transactions',
    method: 'get'
  })
}
