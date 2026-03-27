<template>
  <aside class="sidebar">
    <div class="brand-block">
      <h2>ILAS</h2>
      <p>Learning Hub</p>
    </div>

    <nav class="nav-section">
      <p class="section-label">Main</p>
      <router-link v-if="authStore.isInstructor" to="/instructor" class="nav-link">
        Instructor Dashboard
      </router-link>
      <router-link v-if="authStore.isInstructor" to="/instructor/exams/create" class="nav-link">
        Create Assessment
      </router-link>

      <router-link v-if="authStore.isStudent" to="/student" class="nav-link">
        Student Dashboard
      </router-link>
    </nav>

    <nav class="nav-section muted">
      <p class="section-label">Shortcuts</p>
      <router-link to="/" class="nav-link">
        Home
      </router-link>
      <router-link to="/login" class="nav-link">
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
.sidebar {
  min-height: 100vh;
  background: linear-gradient(180deg, #111827 0%, #0f172a 100%);
  color: #cbd5e1;
  border-right: 1px solid rgba(148, 163, 184, 0.2);
  display: grid;
  grid-template-rows: auto auto 1fr auto;
  gap: 20px;
  padding: 22px 16px;
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
  display: grid;
  gap: 8px;
}

.nav-section.muted {
  align-content: start;
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
}

.nav-link:hover {
  background: rgba(51, 65, 85, 0.55);
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
}

.btn-logout:hover {
  background: rgba(248, 250, 252, 0.22);
}

@media (max-width: 980px) {
  .sidebar {
    padding: 14px 10px;
  }

  .brand-block p,
  .section-label,
  .nav-link,
  .user-info {
    display: none;
  }

  .brand-block h2 {
    text-align: center;
    font-size: 18px;
  }

  .btn-logout {
    font-size: 12px;
    padding: 8px 10px;
  }
}
</style>
