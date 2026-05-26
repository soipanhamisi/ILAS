<template>
  <div class="navbar-wrapper">
    <aside :class="['sidebar', { 'drawer-open': isDrawerOpen }]">
      <div class="brand-block">
        <h2>ILAS</h2>
        <p>Learning Hub</p>
      </div>

      <nav class="nav-section">
        <p class="section-label">Main</p>
        <router-link v-if="authStore.isInstructor" to="/instructor" class="nav-link" @click="closeDrawer">
          Instructor Dashboard
        </router-link>
        <router-link v-if="authStore.isInstructor" to="/instructor/exams/create" class="nav-link" @click="closeDrawer">
          Create Assessment
        </router-link>

        <router-link v-if="authStore.isStudent" to="/student" class="nav-link" @click="closeDrawer">
          Student Dashboard
        </router-link>

        <router-link v-if="authStore.isAdmin" to="/admin" class="nav-link" @click="closeDrawer">
          Admin Dashboard
        </router-link>
      </nav>

      <nav class="nav-section muted">
        <p class="section-label">Shortcuts</p>
        <router-link to="/" class="nav-link" @click="closeDrawer">
          Home
        </router-link>
        <router-link to="/login" class="nav-link" @click="closeDrawer">
          Switch Account
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <div class="user-info">
          <span class="user-name">{{ authStore.user?.name }}</span>
          <span class="user-badge">{{ authStore.userType }}</span>
        </div>
        <button @click="handleLogout" class="btn-logout">Logout</button>
      </div>
    </aside>

    <div v-if="isDrawerOpen" class="drawer-backdrop" @click="closeDrawer" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const isDrawerOpen = ref(false)

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

// Expose toggleDrawer for parent to use
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
  height: 100%;
  background: linear-gradient(180deg, #111827 0%, #0f172a 100%);
  color: #cbd5e1;
  border-right: 1px solid rgba(148, 163, 184, 0.2);
  gap: 20px;
  padding: 22px 16px;
  overflow-y: auto;
  overflow-x: hidden;
  transition: background-color 0.3s ease, border-color 0.3s ease, color 0.3s ease;
}

:global(html.dark-mode) .sidebar {
  background: linear-gradient(180deg, #0f172a 0%, #020617 100%);
  border-right: 1px solid rgba(71, 85, 105, 0.2);
  color: #cbd5e1;
}

.brand-block h2 {
  color: #f8fafc;
  font-size: 24px;
  letter-spacing: 0.04em;
}

.brand-block p {
  margin-top: 2px;
  color: #94a3b8;
  font-size: 13px;
}

.nav-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-section.muted {
  margin-top: auto;
}

.section-label {
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #94a3b8;
  padding: 0 12px 6px;
}

.nav-link {
  text-decoration: none;
  color: #cbd5e1;
  font-weight: 600;
  font-size: 14px;
  padding: 10px 12px;
  border-radius: 10px;
  transition: all 0.3s ease;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.nav-link:hover {
  background: rgba(51, 65, 85, 0.55);
  color: #ffffff;
}

:global(html.dark-mode) .nav-link:hover {
  background: rgba(71, 85, 105, 0.55);
  color: #ffffff;
}

.nav-link.router-link-active {
  background: rgba(37, 99, 235, 0.24);
  color: #dbeafe;
  box-shadow: inset 0 0 0 1px rgba(96, 165, 250, 0.35);
}

.sidebar-footer {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid rgba(148, 163, 184, 0.15);
  transition: border-color 0.3s ease;
}

:global(html.dark-mode) .sidebar-footer {
  border-top: 1px solid rgba(71, 85, 105, 0.15);
}

.user-info {
  display: grid;
  gap: 4px;
  width: 100%;
}

.user-name {
  font-weight: 600;
  color: #f8fafc;
  font-size: 14px;
}

.user-badge {
  width: fit-content;
  background: rgba(37, 99, 235, 0.22);
  color: #bfdbfe;
  padding: 3px 10px;
  border-radius: 999px;
  font-size: 12px;
  text-transform: uppercase;
  font-weight: 600;
}

.btn-logout {
  width: 100%;
  background: rgba(248, 250, 252, 0.12);
  color: #f8fafc;
  border: 1px solid rgba(148, 163, 184, 0.35);
  padding: 10px 16px;
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-logout:hover {
  background: rgba(248, 250, 252, 0.22);
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
    width: 280px;
    height: 100vh;
    z-index: 1000;
    transform: translateX(-100%);
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    overflow-y: auto;
    border-right: none;
    box-shadow: 2px 0 16px rgba(0, 0, 0, 0.3);
    max-height: none;
  }

  .sidebar.drawer-open {
    transform: translateX(0);
  }

  .drawer-backdrop {
    animation: fadeIn 0.3s ease-out;
  }

  @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }

  .brand-block p,
  .section-label {
    display: block;
  }

  .nav-link,
  .user-info {
    display: block;
  }

  .brand-block h2 {
    font-size: 24px;
    text-align: left;
  }

  .btn-logout {
    font-size: 14px;
    padding: 10px 16px;
  }
}
</style>
