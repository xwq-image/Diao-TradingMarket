import request from '@/utils/request'

export const addFavorite = (goodsId) => {
  return request({
    url: '/favorite/add',
    method: 'post',
    params: { goodsId }
  })
}

export const removeFavorite = (goodsId) => {
  return request({
    url: '/favorite/remove',
    method: 'delete',
    params: { goodsId }
  })
}

export const checkFavorite = (goodsId) => {
  return request({
    url: '/favorite/check',
    method: 'get',
    params: { goodsId }
  })
}

export const getMyFavorites = (params) => {
  return request({
    url: '/favorite/my',
    method: 'get',
    params
  })
}
