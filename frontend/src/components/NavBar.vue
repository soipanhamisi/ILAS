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
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 2px 8px rgba(56, 101, 71, 0.14);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid rgba(67, 96, 50, 0.12);
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
  border-radius: 8px;
  transition: all 0.3s ease;
}

.nav-link:hover {
  background: rgba(183, 203, 145, 0.45);
}

.nav-link.router-link-active {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
  color: var(--color-white);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-left: 20px;
  border-left: 2px solid rgba(112, 113, 77, 0.24);
}

.user-name {
  font-weight: 600;
  color: var(--color-text);
}

.user-badge {
  background: linear-gradient(135deg, var(--color-accent) 0%, var(--color-primary) 100%);
  color: var(--color-white);
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  text-transform: uppercase;
  font-weight: 600;
}

.btn-logout {
  background: var(--color-muted);
  color: var(--color-white);
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
}

.btn-logout:hover {
  background: var(--color-primary);
}
</style>
