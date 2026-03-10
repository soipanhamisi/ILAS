import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    userType: null, // 'instructor' or 'student'
    isAuthenticated: false
  }),

  getters: {
    isInstructor: (state) => state.userType === 'instructor',
    isStudent: (state) => state.userType === 'student',
    userId: (state) => state.user?.id
  },

  actions: {
    login(userId, userType, name) {
      this.user = { id: userId, name }
      this.userType = userType
      this.isAuthenticated = true

      // Store in localStorage
      localStorage.setItem('userId', userId)
      localStorage.setItem('userType', userType)
      localStorage.setItem('userName', name)
    },

    logout() {
      this.user = null
      this.userType = null
      this.isAuthenticated = false

      // Clear localStorage
      localStorage.removeItem('userId')
      localStorage.removeItem('userType')
      localStorage.removeItem('userName')
    },

    checkAuth() {
      const userId = localStorage.getItem('userId')
      const userType = localStorage.getItem('userType')
      const userName = localStorage.getItem('userName')

      if (userId && userType && userName) {
        this.user = { id: parseInt(userId), name: userName }
        this.userType = userType
        this.isAuthenticated = true
        return true
      }
      return false
    }
  }
})

