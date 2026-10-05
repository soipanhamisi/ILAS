<template>
  <div class="navbar-wrapper">
    <aside :class="['sidebar', { 'drawer-open': isDrawerOpen }]">
      <div class="brand-block">
        <h2>Next<br>Skill</h2>
      </div>

      <nav class="nav-section">
        <router-link v-if="authStore.isInstructor" to="/instructor" class="nav-link" title="Instructor Dashboard" @click="closeDrawer">
          <span class="nav-icon">ID</span>
          <span class="nav-label">Instructor Dashboard</span>
        </router-link>
        <router-link v-if="authStore.isInstructor" to="/instructor/exams/create" class="nav-link" title="Create Assessment" @click="closeDrawer">
          <span class="nav-icon">CE</span>
          <span class="nav-label">Create Assessment</span>
        </router-link>
        <router-link v-if="authStore.isStudent" to="/student" class="nav-link" title="Student Dashboard" @click="closeDrawer">
          <span class="nav-icon">SD</span>
          <span class="nav-label">Student Dashboard</span>
        </router-link>
        <router-link v-if="authStore.isAdmin" to="/admin" class="nav-link" title="Admin Dashboard" @click="closeDrawer">
          <span class="nav-icon">AD</span>
          <span class="nav-label">Admin Dashboard</span>
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
        <div class="user-avatar">{{ userInitials }}</div>
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
  background: #1a1f2b;
  color: #cbd5e1;
  border-right: 1px solid rgba(130, 142, 181, 0.18);
  gap: 18px;
  padding: 20px 12px;
  overflow-y: auto;
  overflow-x: hidden;
}

.brand-block h2 {
  color: #ecf0ff;
  font-size: 38px;
  line-height: 0.82;
  letter-spacing: 0.01em;
  text-align: left;
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
}

.nav-link {
  text-decoration: none;
  color: #cfd5e6;
  width: 100%;
  min-height: 46px;
  border-radius: 14px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 10px;
  border: 1px solid rgba(130, 142, 181, 0.12);
  padding: 8px 10px;
}

.nav-link:hover {
  background: rgba(92, 103, 133, 0.3);
  color: #ffffff;
}

.nav-link.router-link-active {
  background: #e7de8a;
  color: #1a1d24;
  border-color: rgba(255, 255, 255, 0.45);
}

.nav-icon {
  width: 26px;
  height: 26px;
  border-radius: 8px;
  border: 1px solid rgba(130, 142, 181, 0.35);
  background: rgba(92, 103, 133, 0.24);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.04em;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-label {
  font-size: 12px;
  font-weight: 600;
  line-height: 1.2;
}

.sidebar-footer {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: auto;
  padding-top: 14px;
  border-top: 1px solid rgba(130, 142, 181, 0.2);
  width: 100%;
  align-items: stretch;
}

.user-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, #c5b1ff 0%, #927ce2 100%);
  color: #17181f;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  margin: 0 auto;
}

.btn-logout {
  width: 100%;
  background: rgba(92, 103, 133, 0.25);
  color: #f8fbff;
  border: 1px solid rgba(130, 142, 181, 0.32);
  padding: 8px 10px;
  border-radius: 10px;
  font-size: 12px;
}

.btn-logout:hover {
  background: rgba(92, 103, 133, 0.4);
}

.drawer-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
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
    width: 270px;
    height: 100vh;
    z-index: 1000;
    transform: translateX(-100%);
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    border-right: none;
    box-shadow: 2px 0 16px rgba(0, 0, 0, 0.3);
  }

  .sidebar.drawer-open {
    transform: translateX(0);
  }

  .brand-block h2 {
    font-size: 34px;
    text-align: left;
  }

  .btn-logout {
    font-size: 11px;
    padding: 7px 8px;
  }
}
</style>
