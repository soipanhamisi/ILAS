<template>
  <div id="app">
    <div v-if="useLmsShell" class="lms-layout">
      <NavBar ref="navBarRef" />

      <div class="lms-main">
        <header class="top-utility-bar">
          <div class="utility-left">
            <button @click="toggleMobileMenu" class="btn-menu-toggle" aria-label="Open navigation menu">
              <span class="menu-icon">|||</span>
            </button>
            <div>
              <p class="top-utility-label">Teacher Workspace</p>
              <h1 class="top-utility-title text-enter">{{ currentSectionTitle }}</h1>
            </div>
          </div>

          <div class="top-utility-user">
            <span class="header-chip">Purpose-built for learning</span>
            <span class="user-pill">{{ authStore.user?.name }}</span>
            <span class="role-pill">{{ authStore.userType }}</span>
          </div>
        </header>

        <main class="lms-workspace">
          <router-view v-slot="{ Component, route: currentRoute }">
            <transition name="page" mode="out-in" appear>
              <component :is="Component" :key="currentRoute.fullPath" />
            </transition>
          </router-view>
        </main>
      </div>
    </div>

    <router-view v-else v-slot="{ Component, route: currentRoute }">
      <transition name="page" mode="out-in" appear>
        <component :is="Component" :key="currentRoute.fullPath" />
      </transition>
    </router-view>
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
  Home: 'Markrr Platform',
  Login: 'Welcome',
  AdminDashboard: 'Admin Dashboard',
  InstructorDashboard: 'Instructor Dashboard',
  CreateExam: 'Create Assessment',
  ExamSubmissions: 'Grade Center',
  StudentDashboard: 'Student Dashboard',
  TakeExam: 'Assessment Workspace',
  ViewResults: 'Results and Feedback'
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
  themeStore.initializeTheme()
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
@import url('https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700;800&display=swap');

:root {
  --color-primary: #3b1212;
  --color-surface: #fffefb;
  --color-surface-strong: #ffffff;
  --color-muted: #755e55;
  --color-accent: #b85b0a;
  --color-bg: #f7f0e1;
  --color-text: #3b1212;
  --color-text-soft: #755e55;
  --color-white: #ffffff;
  --glass-bg: rgba(255, 255, 255, 0.88);
  --glass-bg-strong: rgba(255, 255, 255, 0.95);
  --glass-border: rgba(117, 94, 85, 0.2);
  --shadow-soft: 0 14px 34px rgba(63, 28, 18, 0.1);
  --shadow-strong: 0 24px 56px rgba(63, 28, 18, 0.16);
  --brand-ink: #3b1212;
  --brand-gold: #b85b0a;
  --brand-green: #164541;
  --brand-green-soft: #1f615b;
  --brand-card: #decaa0;
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
  font-family: 'Manrope', 'Segoe UI', Roboto, system-ui, sans-serif;
  background: var(--color-bg);
  color: var(--color-text);
  line-height: 1.55;
  -webkit-font-smoothing: antialiased;
}

::selection {
  background: var(--color-accent);
  color: var(--color-surface-strong);
}

button {
  cursor: pointer;
  border: 1px solid var(--glass-border);
  border-radius: 14px;
  padding: 10px 20px;
  font-size: 15px;
  transition: all 0.25s ease;
}

button:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-soft);
}

button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

input, textarea, select {
  width: 100%;
  padding: 12px;
  border: 1px solid rgba(116, 83, 64, 0.26);
  border-radius: 12px;
  font-size: 16px;
  transition: border-color 0.25s ease, box-shadow 0.25s ease;
  background: rgba(255, 252, 246, 0.95);
  color: var(--color-text);
}

input:focus, textarea:focus, select:focus {
  outline: none;
  border-color: #ca6a07;
  box-shadow: 0 0 0 4px rgba(202, 106, 7, 0.18);
}

.lms-layout {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 290px minmax(0, 1fr);
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
  position: sticky;
  top: 0;
  z-index: 30;
  padding: 18px 28px 14px;
  background: rgba(248, 237, 214, 0.88);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--glass-border);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
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
  background: transparent;
  color: var(--color-text);
  border: 1px solid var(--glass-border);
  padding: 8px;
  font-size: 20px;
  line-height: 1;
  flex-shrink: 0;
}

.header-chip {
  padding: 7px 12px;
  border-radius: 999px;
  background: rgba(222, 203, 161, 0.58);
  color: #5f4537;
  font-size: 12px;
  border: 1px solid rgba(116, 83, 64, 0.2);
}

.menu-icon {
  display: block;
  line-height: 1;
}

.top-utility-label {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.14em;
  color: #846453;
}

.top-utility-title {
  font-size: 30px;
  color: var(--brand-ink);
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
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(232, 215, 181, 0.9);
  font-size: 12px;
  font-weight: 700;
  color: #3d241a;
}

.role-pill {
  text-transform: uppercase;
  background: rgba(23, 79, 74, 0.14);
  color: #1f615b;
}

.lms-workspace {
  min-width: 0;
  overflow: auto;
  display: flex;
  flex-direction: column;
  padding: 12px 24px 24px;
}

.text-enter {
  animation: textLiftIn 0.7s ease-out both;
}

@keyframes textLiftIn {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (prefers-reduced-motion: reduce) {
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

  .header-chip,
  .user-pill,
  .role-pill {
    display: none;
  }
}
</style>
