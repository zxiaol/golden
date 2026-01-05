import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(null)
  
  const isLoggedIn = ref(!!token.value)
  
  const login = async (username, password) => {
    const response = await api.post('/auth/login', { username, password })
    token.value = response.data.token
    user.value = response.data.user
    isLoggedIn.value = true
    localStorage.setItem('token', token.value)
    return response
  }
  
  const register = async (data) => {
    await api.post('/auth/register', data)
  }
  
  const logout = () => {
    token.value = ''
    user.value = null
    isLoggedIn.value = false
    localStorage.removeItem('token')
  }
  
  const fetchProfile = async () => {
    const response = await api.get('/user/profile')
    user.value = response.data
    return response
  }
  
  return {
    token,
    user,
    isLoggedIn,
    login,
    register,
    logout,
    fetchProfile
  }
})

