<template>
  <nav class="navbar">
    <div class="container">
      <div class="nav-content">
        <div class="logo">
          <h2>🎓 ILAS</h2>
        </div>

        <div class="nav-links">
          <router-link v-if="authStore.isInstructor" to="/instructor" class="nav-link">
            Dashboard
          </router-link>
          <router-link v-if="authStore.isInstructor" to="/instructor/exams/create" class="nav-link">
            Create Exam
          </router-link>

          <router-link v-if="authStore.isStudent" to="/student" class="nav-link">
            Dashboard
          </router-link>

          <div class="user-info">
            <span class="user-name">{{ authStore.user?.name }}</span>
            <span class="user-badge">{{ authStore.userType }}</span>
            <button @click="handleLogout" class="btn-logout">Logout</button>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  background: rgba(246, 245, 230, 0.42);
  box-shadow: 0 10px 26px rgba(79, 106, 72, 0.12);
  backdrop-filter: blur(14px);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid var(--glass-border);
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
}

.logo h2 {
  color: var(--color-primary);
  font-size: 28px;
  letter-spacing: 0.3px;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-link {
  text-decoration: none;
  color: var(--color-text);
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 999px;
  transition: all 0.3s ease;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.5);
}

.nav-link.router-link-active {
  background: linear-gradient(135deg, var(--color-surface) 0%, var(--color-accent) 100%);
  color: #27423a;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.45);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-left: 20px;
  border-left: 1px solid rgba(255, 255, 255, 0.5);
}

.user-name {
  font-weight: 600;
  color: var(--color-text);
}

.user-badge {
  background: rgba(255, 255, 255, 0.56);
  color: var(--color-text);
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  text-transform: uppercase;
  font-weight: 600;
}

.btn-logout {
  background: rgba(255, 255, 255, 0.62);
  color: var(--color-text);
  border: 1px solid rgba(255, 255, 255, 0.5);
  padding: 8px 16px;
  border-radius: 999px;
  font-size: 14px;
}

.btn-logout:hover {
  background: rgba(255, 255, 255, 0.82);
}
</style>
