import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const isDarkMode = ref(false)

  // Initialize theme from localStorage and system preference
  const initializeTheme = () => {
    // Check localStorage first
    const savedTheme = localStorage.getItem('theme-mode')
    if (savedTheme) {
      isDarkMode.value = savedTheme === 'dark'
    } else {
      // Fall back to system preference
      isDarkMode.value = window.matchMedia('(prefers-color-scheme: dark)').matches
    }
    applyTheme()
  }

  // Apply theme to document
  const applyTheme = () => {
    const htmlElement = document.documentElement
    if (isDarkMode.value) {
      htmlElement.setAttribute('data-theme', 'dark')
      htmlElement.classList.add('dark-mode')
      localStorage.setItem('theme-mode', 'dark')
    } else {
      htmlElement.removeAttribute('data-theme')
      htmlElement.classList.remove('dark-mode')
      localStorage.setItem('theme-mode', 'light')
    }
  }

  // Toggle dark mode
  const toggleDarkMode = () => {
    isDarkMode.value = !isDarkMode.value
  }

  // Watch for changes and apply theme
  watch(isDarkMode, () => {
    applyTheme()
  })

  return {
    isDarkMode,
    initializeTheme,
    applyTheme,
    toggleDarkMode
  }
})

