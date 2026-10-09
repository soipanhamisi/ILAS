<template>
  <div class="navbar-wrapper">
    <aside :class="['sidebar', { 'drawer-open': isDrawerOpen }]">
      <div class="brand-block">
        <img src="/markrr-logo.svg" alt="" class="brand-logo" />
        <div>
          <h2>Markrr</h2>
          <p class="brand-subtitle">Workspace</p>
        </div>
      </div>

      <p class="section-label">Workspace</p>
      <nav class="nav-section">
        <router-link v-if="authStore.isInstructor" to="/instructor" class="nav-link" title="Instructor Dashboard" @click="closeDrawer">
          <span class="nav-icon">ID</span>
          <span class="nav-label">Overview</span>
        </router-link>
        <router-link v-if="authStore.isInstructor" to="/instructor/exams/create" class="nav-link" title="Create Assessment" @click="closeDrawer">
          <span class="nav-icon">CE</span>
          <span class="nav-label">Assessments</span>
        </router-link>
        <router-link v-if="authStore.isStudent" to="/student" class="nav-link" title="Student Dashboard" @click="closeDrawer">
          <span class="nav-icon">SD</span>
          <span class="nav-label">Overview</span>
        </router-link>
        <router-link v-if="authStore.isAdmin" to="/admin" class="nav-link" title="Admin Dashboard" @click="closeDrawer">
          <span class="nav-icon">AD</span>
          <span class="nav-label">Platform Monitor</span>
        </router-link>
      </nav>

      <nav class="nav-section secondary">
        <router-link to="/" class="nav-link" title="Home" @click="closeDrawer">
          <span class="nav-icon">HM</span>
          <span class="nav-label">Home</span>
        </router-link>
        <router-link to="/login" class="nav-link" title="Switch Account" @click="closeDrawer">
          <span class="nav-icon">SW</span>
          <span class="nav-label">Switch Account</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <div class="profile-card">
          <div class="user-avatar">{{ userInitials }}</div>
          <div class="profile-meta">
            <strong>{{ authStore.user?.name || 'User' }}</strong>
            <span>{{ authStore.userType || 'member' }}</span>
          </div>
        </div>
        <button @click="handleLogout" class="btn-logout">Logout</button>
      </div>
    </aside>

    <div v-if="isDrawerOpen" class="drawer-backdrop" @click="closeDrawer" />
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const isDrawerOpen = ref(false)

const userInitials = computed(() => {
  const name = authStore.user?.name || 'User'
  return name
    .split(' ')
    .map((part) => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase()
})

const toggleDrawer = () => {
  isDrawerOpen.value = !isDrawerOpen.value
}

const closeDrawer = () => {
  isDrawerOpen.value = false
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

defineExpose({ toggleDrawer })
</script>

<style scoped>
.navbar-wrapper {
  position: sticky;
  top: 0;
  height: 100vh;
  display: flex;
  flex-direction: column;
  z-index: 100;
}

.sidebar {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  height: 100%;
  background: #fffdf8;
  color: #4a2a1f;
  border-right: 1px solid rgba(116, 83, 64, 0.16);
  gap: 14px;
  padding: 24px 16px;
  overflow-y: auto;
  overflow-x: hidden;
}

.brand-block {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.brand-logo {
  width: 44px;
  height: 44px;
  display: block;
}

.brand-block h2 {
  color: #3a1c14;
  font-size: 36px;
  line-height: 0.9;
}

.brand-subtitle {
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #8b6b5a;
}

.section-label {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.13em;
  color: #9b7b6a;
  margin: 10px 0 4px;
}

.nav-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
  align-items: stretch;
}

.nav-section.secondary {
  margin-top: auto;
  padding-top: 8px;
}

.nav-link {
  text-decoration: none;
  color: #5d4337;
  width: 100%;
  min-height: 50px;
  border-radius: 14px;
  transition: all 0.25s ease;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 10px;
  border: 1px solid rgba(116, 83, 64, 0.12);
  padding: 10px 12px;
}

.nav-link:hover {
  background: #f3e6c9;
  color: #3e2319;
}

.nav-link.router-link-active {
  background: #efe1bf;
  color: #2f1a12;
  border-color: rgba(116, 83, 64, 0.25);
  box-shadow: 0 8px 18px rgba(63, 28, 18, 0.08);
}

.nav-icon {
  width: 28px;
  height: 28px;
  border-radius: 9px;
  border: 1px solid rgba(116, 83, 64, 0.3);
  background: rgba(232, 215, 181, 0.45);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.04em;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-label {
  font-size: 16px;
  font-weight: 600;
  line-height: 1.2;
}

.sidebar-footer {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: auto;
  padding-top: 14px;
  border-top: 1px solid rgba(116, 83, 64, 0.2);
  width: 100%;
  align-items: stretch;
}

.profile-card {
  background: rgba(239, 225, 191, 0.6);
  border: 1px solid rgba(116, 83, 64, 0.2);
  border-radius: 16px;
  padding: 12px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: #e4c88f;
  color: #164b47;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  flex-shrink: 0;
}

.profile-meta {
  display: grid;
  gap: 2px;
}

.profile-meta strong {
  font-size: 14px;
  color: #3d241a;
}

.profile-meta span {
  font-size: 12px;
  color: #785f53;
  text-transform: capitalize;
}

.btn-logout {
  width: 100%;
  background: rgba(236, 224, 198, 0.6);
  color: #3d241a;
  border: 1px solid rgba(116, 83, 64, 0.3);
  padding: 9px 10px;
  border-radius: 12px;
  font-size: 13px;
}

.btn-logout:hover {
  background: rgba(232, 215, 181, 0.85);
}

.drawer-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(49, 27, 18, 0.45);
  z-index: 999;
}

@media (max-width: 980px) {
  .navbar-wrapper {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    width: 100%;
    height: auto;
    z-index: 1000;
  }

  .sidebar {
    position: fixed;
    inset: 0;
    width: 290px;
    height: 100vh;
    z-index: 1000;
    transform: translateX(-100%);
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    border-right: none;
    box-shadow: 2px 0 16px rgba(0, 0, 0, 0.2);
  }

  .sidebar.drawer-open {
    transform: translateX(0);
  }

  .brand-block h2 {
    font-size: 30px;
  }
}
</style>
