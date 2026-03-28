<template>
  <div id="app">
    <div v-if="useLmsShell" class="lms-layout">
      <NavBar ref="navBarRef" />

      <div class="lms-main">
        <header class="top-utility-bar">
          <div class="utility-left">
            <button @click="toggleMobileMenu" class="btn-menu-toggle">
              <span class="menu-icon">☰</span>
            </button>
            <div>
              <p class="top-utility-label">Learning Management</p>
              <h1 class="top-utility-title">{{ currentSectionTitle }}</h1>
            </div>
          </div>

          <div class="top-utility-user">
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
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from './stores/auth'
import NavBar from './components/NavBar.vue'

const authStore = useAuthStore()
const route = useRoute()
const navBarRef = ref(null)

const routeTitleMap = {
  InstructorDashboard: 'Instructor Dashboard',
  CreateExam: 'Create Assessment',
  ExamSubmissions: 'Grade Center',
  StudentDashboard: 'Student Dashboard',
  TakeExam: 'Assessment Workspace',
  ViewResults: 'Results & Feedback'
}

const useLmsShell = computed(() => authStore.isAuthenticated && route.meta.requiresAuth)
const currentSectionTitle = computed(() => routeTitleMap[route.name] || 'Course Workspace')

const toggleMobileMenu = () => {
  navBarRef.value?.toggleDrawer()
}

onMounted(() => {
  authStore.checkAuth()
})
</script>

<style>
:root {
  --color-primary: #111827;
  --color-surface: #e5e7eb;
  --color-surface-strong: #f3f4f6;
  --color-muted: #6b7280;
  --color-accent: #2563eb;
  --color-bg: #f8fafc;
  --color-text: #111827;
  --color-text-soft: #4b5563;
  --color-white: #ffffff;
  --glass-bg: rgba(248, 250, 252, 0.72);
  --glass-bg-strong: rgba(255, 255, 255, 0.9);
  --glass-border: rgba(148, 163, 184, 0.28);
  --shadow-soft: 0 8px 20px rgba(15, 23, 42, 0.08);
  --shadow-strong: 0 20px 40px rgba(15, 23, 42, 0.16);
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
  background: linear-gradient(180deg, #f8fafc 0%, #eef2f7 100%);
  color: var(--color-text);
  line-height: 1.55;
}

button {
  cursor: pointer;
  border: none;
  border-radius: 12px;
  padding: 10px 20px;
  font-size: 16px;
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
  background: linear-gradient(135deg, #0f172a 0%, #1f2937 100%);
  color: #f8fafc;
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.96);
  color: var(--color-text);
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(10px);
}

.btn-success {
  background: linear-gradient(135deg, #1d4ed8 0%, var(--color-accent) 100%);
  color: #f8fafc;
}

.btn-danger {
  background: linear-gradient(135deg, #b91c1c 0%, #dc2626 100%);
  color: #fff5f5;
}

input, textarea, select {
  width: 100%;
  padding: 12px;
  border: 1px solid rgba(255, 255, 255, 0.55);
  border-radius: 12px;
  font-size: 16px;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  background: rgba(255, 255, 255, 0.66);
  backdrop-filter: blur(10px);
  color: var(--color-text);
}

input:focus, textarea:focus, select:focus {
  outline: none;
  border-color: #60a5fa;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.18);
}

.card {
  background: var(--glass-bg-strong);
  border-radius: 20px;
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
  grid-template-columns: 280px minmax(0, 1fr);
}

.lms-main {
  min-width: 0;
  display: grid;
  grid-template-rows: auto 1fr;
}

.top-utility-bar {
  padding: 18px 28px;
  background: rgba(255, 255, 255, 0.96);
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
  background: none;
  color: var(--color-primary);
  border: none;
  padding: 8px;
  cursor: pointer;
  font-size: 24px;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.btn-menu-toggle:hover {
  background: rgba(148, 163, 184, 0.15);
  border-radius: 8px;
}

.menu-icon {
  display: block;
  line-height: 1;
}

.top-utility-label {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--color-muted);
}

.top-utility-title {
  font-size: 22px;
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
  background: #e2e8f0;
  font-size: 12px;
  font-weight: 700;
  color: #0f172a;
}

.role-pill {
  text-transform: uppercase;
  background: #dbeafe;
  color: #1e3a8a;
}

.lms-workspace {
  min-width: 0;
  overflow: auto;
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
    display: none;
  }

  .top-utility-title {
    font-size: 18px;
  }
}
</style>
