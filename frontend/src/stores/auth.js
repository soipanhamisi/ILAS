import { defineStore } from 'pinia'
import { authAPI } from '../services/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    userType: null, // 'instructor' or 'student'
    isAuthenticated: false,
    token: null
  }),

  getters: {
    isInstructor: (state) => state.userType === 'instructor',
    isStudent: (state) => state.userType === 'student',
    userId: (state) => state.user?.userId
  },

  actions: {
    async login(username, password, userType) {
      try {
        const response = await authAPI.login(username, password, userType)
        if (response.data.success) {
          const user = response.data.data
          this.user = {
            userId: user.userId,
            name: user.name,
            username: user.username,
            email: user.email
          }
          this.userType = user.userType
          this.token = user.token
          this.isAuthenticated = true

          // Store in localStorage
          localStorage.setItem('userId', user.userId)
          localStorage.setItem('userType', user.userType)
          localStorage.setItem('userName', user.name)
          localStorage.setItem('token', user.token)
          localStorage.setItem('username', user.username)

          return true
        } else {
          throw new Error(response.data.message || 'Login failed')
        }
      } catch (error) {
        this.isAuthenticated = false
        throw error
      }
    },

    async signup(name, email, username, password, userType) {
      try {
        const response = await authAPI.signup(name, email, username, password, userType)
        if (response.data.success) {
          const user = response.data.data
          this.user = {
            userId: user.userId,
            name: user.name,
            username: user.username,
            email: user.email
          }
          this.userType = user.userType
          this.token = user.token
          this.isAuthenticated = true

          // Store in localStorage
          localStorage.setItem('userId', user.userId)
          localStorage.setItem('userType', user.userType)
          localStorage.setItem('userName', user.name)
          localStorage.setItem('token', user.token)
          localStorage.setItem('username', user.username)

          return true
        } else {
          throw new Error(response.data.message || 'Signup failed')
        }
      } catch (error) {
        this.isAuthenticated = false
        throw error
      }
    },

    logout() {
      this.user = null
      this.userType = null
      this.isAuthenticated = false
      this.token = null

      // Clear localStorage
      localStorage.removeItem('userId')
      localStorage.removeItem('userType')
      localStorage.removeItem('userName')
      localStorage.removeItem('token')
      localStorage.removeItem('username')
    },

    checkAuth() {
      const userId = localStorage.getItem('userId')
      const userType = localStorage.getItem('userType')
      const userName = localStorage.getItem('userName')
      const token = localStorage.getItem('token')
      const username = localStorage.getItem('username')

      if (userId && userType && userName && token) {
        this.user = { userId: parseInt(userId), name: userName, username, email: '' }
        this.userType = userType
        this.token = token
        this.isAuthenticated = true
        return true
      }
      return false
    }
  }
})

