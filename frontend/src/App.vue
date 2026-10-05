<template>
  <div id="app">
    <div v-if="useLmsShell" class="lms-layout">
      <NavBar ref="navBarRef" />

      <div class="lms-main">
        <header class="top-utility-bar">
          <div class="utility-left">
            <button @click="toggleMobileMenu" class="btn-menu-toggle">
              <span class="menu-icon">|||</span>
            </button>
            <div>
              <p class="top-utility-label">ILAS Learning Workspace</p>
              <h1 class="top-utility-title">{{ currentSectionTitle }}</h1>
            </div>
          </div>

          <div class="top-utility-user">
            <span class="header-chip">Dark Baseline</span>
            <span class="user-pill">{{ authStore.user?.name }}</span>
            <span class="role-pill">{{ authStore.userType }}</span>
          </div>
        </header>

        <main class="lms-workspace">
          <router-view />
        </main>
      </div>
    </div>

    <router-view v-else />
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from './stores/auth'
import { useThemeStore } from './stores/theme'
import NavBar from './components/NavBar.vue'
import { adminAPI } from './services/api'

const authStore = useAuthStore()
const themeStore = useThemeStore()
const route = useRoute()
const navBarRef = ref(null)

const routeTitleMap = {
  AdminDashboard: 'Admin Dashboard',
  InstructorDashboard: 'Instructor Dashboard',
  CreateExam: 'Create Assessment',
  ExamSubmissions: 'Grade Center',
  StudentDashboard: 'Student Dashboard',
  TakeExam: 'Assessment Workspace',
  ViewResults: 'Results & Feedback'
}

const useLmsShell = computed(() => authStore.isAuthenticated && route.meta.requiresAuth)
const currentSectionTitle = computed(() => routeTitleMap[route.name] || 'Course Workspace')
let heartbeatTimer = null

const toggleMobileMenu = () => {
  navBarRef.value?.toggleDrawer()
}

const sendHeartbeat = async () => {
  if (!authStore.isAuthenticated || !authStore.userId || !authStore.userType) return
  try {
    await adminAPI.sendHeartbeat(authStore.userType, authStore.userId)
  } catch (err) {
    console.debug('Heartbeat failed', err)
  }
}

const startHeartbeat = () => {
  if (heartbeatTimer) return
  sendHeartbeat()
  heartbeatTimer = setInterval(sendHeartbeat, 30000)
}

const stopHeartbeat = () => {
  if (heartbeatTimer) {
    clearInterval(heartbeatTimer)
    heartbeatTimer = null
  }
}

onMounted(() => {
  themeStore.isDarkMode = true
  themeStore.applyTheme()
  authStore.checkAuth()
  if (authStore.isAuthenticated) {
    startHeartbeat()
  }
})

watch(() => authStore.isAuthenticated, (isAuthenticated) => {
  if (isAuthenticated) {
    startHeartbeat()
  } else {
    stopHeartbeat()
  }
})

onBeforeUnmount(() => {
  stopHeartbeat()
})
</script>

<style>
:root {
  --color-primary: #f4f6fd;
  --color-surface: #2a2f3d;
  --color-surface-strong: #1f232f;
  --color-muted: #a7aec6;
  --color-accent: #c5b1ff;
  --color-bg: #13161f;
  --color-text: #eef1f8;
  --color-text-soft: #b4bad0;
  --color-white: #2b3141;
  --glass-bg: rgba(31, 35, 47, 0.88);
  --glass-bg-strong: rgba(38, 43, 57, 0.96);
  --glass-border: rgba(130, 142, 181, 0.24);
  --shadow-soft: 0 12px 32px rgba(0, 0, 0, 0.35);
  --shadow-strong: 0 22px 48px rgba(0, 0, 0, 0.52);
  transition: background-color 0.3s ease, color 0.3s ease;
}

html.dark-mode {
  --color-primary: #f4f6fd;
  --color-surface: #2a2f3d;
  --color-surface-strong: #1f232f;
  --color-muted: #a7aec6;
  --color-accent: #c5b1ff;
  --color-bg: #13161f;
  --color-text: #eef1f8;
  --color-text-soft: #b4bad0;
  --color-white: #2b3141;
  --glass-bg: rgba(31, 35, 47, 0.88);
  --glass-bg-strong: rgba(38, 43, 57, 0.96);
  --glass-border: rgba(130, 142, 181, 0.24);
  --shadow-soft: 0 12px 32px rgba(0, 0, 0, 0.35);
  --shadow-strong: 0 22px 48px rgba(0, 0, 0, 0.52);
}

#app {
  width: 100%;
  min-height: 100vh;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
  background: radial-gradient(circle at top right, #1f2330 0%, #13161f 60%);
  color: var(--color-text);
  line-height: 1.55;
  transition: background 0.3s ease, color 0.3s ease;
}

html.dark-mode body {
  background: radial-gradient(circle at top right, #1f2330 0%, #13161f 60%);
}

button {
  cursor: pointer;
  border: 1px solid var(--glass-border);
  border-radius: 14px;
  padding: 10px 20px;
  font-size: 15px;
  transition: all 0.3s ease;
}

button:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-soft);
}

button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-primary {
  background: linear-gradient(135deg, #d8ccff 0%, #bca6ff 100%);
  color: #17181f;
  font-weight: 700;
}

.btn-secondary {
  background: rgba(64, 71, 94, 0.65);
  color: var(--color-text);
}

.btn-success {
  background: linear-gradient(135deg, #d8ccff 0%, #bca6ff 100%);
  color: #17181f;
  font-weight: 700;
}

.btn-danger {
  background: rgba(187, 90, 99, 0.3);
  color: #ffd2d7;
}

input, textarea, select {
  width: 100%;
  padding: 12px;
  border: 1px solid rgba(130, 142, 181, 0.28);
  border-radius: 12px;
  font-size: 16px;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  background: rgba(29, 34, 46, 0.9);
  backdrop-filter: blur(10px);
  color: var(--color-text);
}

input:focus, textarea:focus, select:focus {
  outline: none;
  border-color: #bca6ff;
  box-shadow: 0 0 0 4px rgba(197, 177, 255, 0.2);
}

.card {
  background: var(--glass-bg-strong);
  border-radius: 22px;
  padding: 24px;
  box-shadow: var(--shadow-soft);
  margin-bottom: 20px;
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(14px);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.glass-panel {
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: 20px;
  box-shadow: var(--shadow-soft);
  backdrop-filter: blur(16px);
}

.lms-layout {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 270px minmax(0, 1fr);
  overflow: hidden;
}

.lms-main {
  min-width: 0;
  display: grid;
  grid-template-rows: auto 1fr;
  max-height: 100vh;
  overflow: hidden;
}

.top-utility-bar {
  padding: 18px 28px 14px;
  background: rgba(19, 22, 31, 0.85);
  border-bottom: 1px solid var(--glass-border);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  transition: background-color 0.3s ease, border-color 0.3s ease;
}

.utility-left {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
  flex: 1;
}

.btn-menu-toggle {
  display: none;
  background: none;
  color: var(--color-text);
  border: none;
  padding: 8px;
  cursor: pointer;
  font-size: 24px;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.btn-menu-toggle:hover {
  background: rgba(130, 142, 181, 0.2);
  border-radius: 8px;
}

.header-chip {
  padding: 7px 12px;
  border-radius: 999px;
  background: rgba(64, 71, 94, 0.65);
  color: var(--color-text-soft);
  font-size: 12px;
  border: 1px solid var(--glass-border);
}

.menu-icon {
  display: block;
  line-height: 1;
}

.top-utility-label {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #979eb7;
}

.top-utility-title {
  font-size: 28px;
  color: var(--color-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.top-utility-user {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.user-pill,
.role-pill {
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(64, 71, 94, 0.7);
  font-size: 12px;
  font-weight: 700;
  color: #f2f4fb;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.role-pill {
  text-transform: uppercase;
  background: rgba(197, 177, 255, 0.25);
  color: #d9ccff;
}

.lms-workspace {
  min-width: 0;
  overflow: auto;
  display: flex;
  flex-direction: column;
  padding: 12px 24px 24px;
}

.lms-workspace .container {
  max-width: 100%;
  margin: 0;
  padding: 0;
}

@media (max-width: 980px) {
  .lms-layout {
    grid-template-columns: 1fr;
  }

  .top-utility-bar {
    padding: 14px 16px;
  }

  .btn-menu-toggle {
    display: block;
  }

  .top-utility-user {
    display: flex;
    gap: 12px;
  }

  .top-utility-title {
    font-size: 22px;
  }

  .user-pill,
  .role-pill {
    display: none;
  }
}
</style>
