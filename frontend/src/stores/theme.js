import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const isDarkMode = ref(false)

  const initializeTheme = () => {
    isDarkMode.value = false
    applyTheme()
  }

  const applyTheme = () => {
    const htmlElement = document.documentElement
    htmlElement.classList.add('warm-mode')
    htmlElement.classList.remove('dark-mode')
    htmlElement.removeAttribute('data-theme')
    localStorage.setItem('theme-mode', 'warm')
  }

  const toggleDarkMode = () => {
    isDarkMode.value = false
    applyTheme()
  }

  watch(isDarkMode, () => {
    if (isDarkMode.value) {
      isDarkMode.value = false
    }
    applyTheme()
  })

  return {
    isDarkMode,
    initializeTheme,
    applyTheme,
    toggleDarkMode
  }
})
