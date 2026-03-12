import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register, getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'admin')

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
  }

  const loginAction = async (credentials) => {
    const res = await login(credentials)
    setToken(res.data.token)
    await fetchUserInfo()
    return res
  }

  const registerAction = async (data) => {
    return await register(data)
  }

  const fetchUserInfo = async () => {
    const res = await getUserInfo()
    setUserInfo(res.data)
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    loginAction,
    registerAction,
    fetchUserInfo,
    logout
  }
})
