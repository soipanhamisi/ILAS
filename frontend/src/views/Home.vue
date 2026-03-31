<template>
  <div class="home-container">
    <div class="hero">
      <h1 class="hero-title">🎓 ILAS</h1>
      <p class="hero-subtitle">Integrated Learning Assessment System</p>
      <p class="hero-description">
        A modern platform for managing exams, submissions, and grading
      </p>
      <router-link to="/login" class="btn-get-started">
        Get Started →
      </router-link>
    </div>

    <div class="features">
      <div class="feature-card">
        <div class="feature-icon">👨‍🏫</div>
        <h3>For Instructors</h3>
        <p>Create exams, grade submissions, and provide detailed feedback</p>
      </div>

      <div class="feature-card">
        <div class="feature-icon">👨‍🎓</div>
        <h3>For Students</h3>
        <p>Take exams, submit answers, and receive grades with feedback</p>
      </div>

      <div class="feature-card">
        <div class="feature-icon">📊</div>
        <h3>Track Progress</h3>
        <p>Monitor submissions and view detailed performance analytics</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getDashboardRoute } from '../utils/roleRedirect'

const router = useRouter()
const authStore = useAuthStore()

onMounted(() => {
  if (authStore.isAuthenticated) {
    router.push(getDashboardRoute(authStore.userType))
  }
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.hero {
  text-align: center;
  margin-bottom: 80px;
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: 28px;
  padding: 40px 28px;
  backdrop-filter: blur(14px);
  box-shadow: var(--shadow-soft);
}

.hero-title {
  font-size: 72px;
  font-weight: 800;
  color: var(--color-primary);
  margin-bottom: 16px;
}

.hero-subtitle {
  font-size: 32px;
  color: var(--color-text);
  font-weight: 600;
  margin-bottom: 16px;
}

.hero-description {
  font-size: 20px;
  color: var(--color-text-soft);
  margin-bottom: 32px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.btn-get-started {
  display: inline-block;
  background: linear-gradient(135deg, var(--color-surface) 0%, var(--color-accent) 100%);
  color: #224139;
  padding: 16px 48px;
  border-radius: 999px;
  font-size: 20px;
  font-weight: 700;
  text-decoration: none;
  transition: all 0.3s ease;
  box-shadow: var(--shadow-strong);
}

.btn-get-started:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-strong);
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 32px;
  max-width: 1000px;
  width: 100%;
}

.feature-card {
  background: var(--glass-bg-strong);
  padding: 40px;
  border-radius: 20px;
  text-align: center;
  transition: all 0.3s ease;
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(14px);
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-strong);
}

.feature-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.feature-card h3 {
  font-size: 24px;
  margin-bottom: 12px;
  color: var(--color-primary);
}

.feature-card p {
  color: var(--color-text-soft);
  line-height: 1.6;
}
</style>
