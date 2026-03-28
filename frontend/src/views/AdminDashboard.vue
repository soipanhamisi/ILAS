<template>
  <div class="container dashboard">
    <h1 class="page-title">⚙️ Admin Dashboard</h1>
    <p class="welcome-text">Welcome, {{ authStore.user?.name }}! System Management & Oversight</p>

    <div v-if="loading" class="loading">Loading system data...</div>

    <div v-else class="dashboard-content">
      <div class="stats-grid">
        <div class="stat-card">
          <p class="stat-label">Total Students</p>
          <p class="stat-value">{{ stats.totalStudents }}</p>
          <p class="stat-change">Active learners in system</p>
        </div>

        <div class="stat-card">
          <p class="stat-label">Total Instructors</p>
          <p class="stat-value">{{ stats.totalInstructors }}</p>
          <p class="stat-change">Faculty members</p>
        </div>

        <div class="stat-card">
          <p class="stat-label">Total Courses</p>
          <p class="stat-value">{{ stats.totalCourses }}</p>
          <p class="stat-change">Active courses</p>
        </div>

        <div class="stat-card">
          <p class="stat-label">Total Exams</p>
          <p class="stat-value">{{ stats.totalExams }}</p>
          <p class="stat-change">Assessments in system</p>
        </div>
      </div>

      <div class="section">
        <div class="section-header">
          <h2 class="section-title">📊 System Overview</h2>
        </div>

        <div class="overview-content">
          <div class="overview-item">
            <span class="overview-icon">👥</span>
            <div class="overview-info">
              <h3>User Management</h3>
              <p>View and manage all students and instructors in the system</p>
            </div>
            <button class="btn-secondary" disabled>Coming Soon</button>
          </div>

          <div class="overview-item">
            <span class="overview-icon">📚</span>
            <div class="overview-info">
              <h3>Course Management</h3>
              <p>Monitor course creation and enrollment statistics</p>
            </div>
            <button class="btn-secondary" disabled>Coming Soon</button>
          </div>

          <div class="overview-item">
            <span class="overview-icon">📋</span>
            <div class="overview-info">
              <h3>Exam Analytics</h3>
              <p>Track assessment creation, submissions, and grading status</p>
            </div>
            <button class="btn-secondary" disabled>Coming Soon</button>
          </div>

          <div class="overview-item">
            <span class="overview-icon">📈</span>
            <div class="overview-info">
              <h3>System Reports</h3>
              <p>Generate comprehensive reports on system usage and performance</p>
            </div>
            <button class="btn-secondary" disabled>Coming Soon</button>
          </div>
        </div>
      </div>

      <div class="section">
        <div class="section-header">
          <h2 class="section-title">🔔 System Status</h2>
        </div>

        <div class="status-content">
          <div class="status-item success">
            <p class="status-badge">✓ Operational</p>
            <p class="status-message">All systems running normally</p>
          </div>
          <div class="status-item">
            <p class="status-label">Last Updated</p>
            <p class="status-message">{{ lastUpdated }}</p>
          </div>
        </div>
      </div>
    </div>

    <div v-if="error" class="error-message">
      {{ error }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { adminAPI } from '../services/api'

const router = useRouter()
const authStore = useAuthStore()

const stats = ref({
  totalStudents: 0,
  totalInstructors: 0,
  totalCourses: 0,
  totalExams: 0
})

const loading = ref(false)
const error = ref('')
const lastUpdated = ref('')

const loadData = async () => {
  loading.value = true
  error.value = ''

  try {
    const response = await adminAPI.getDashboardSummary(authStore.userId)

    if (response.data.success) {
      const data = response.data.data
      stats.value = {
        totalStudents: data.totalStudents,
        totalInstructors: data.totalInstructors,
        totalCourses: data.totalCourses,
        totalExams: data.totalExams
      }
      lastUpdated.value = new Date().toLocaleString()
    } else {
      error.value = response.data.message || 'Failed to load dashboard data'
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Error loading dashboard data'
    console.error('Error loading data:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dashboard {
  padding: 40px 20px;
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  color: var(--color-primary);
  margin-bottom: 8px;
}

.welcome-text {
  color: var(--color-text-soft);
  font-size: 18px;
  margin-bottom: 32px;
}

.loading {
  background: var(--glass-bg-strong);
  padding: 60px 20px;
  text-align: center;
  border-radius: 16px;
  color: var(--color-text-soft);
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(14px);
}

.dashboard-content {
  display: grid;
  gap: 32px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  border: 1px solid rgba(148, 163, 184, 0.28);
  padding: 24px;
  box-shadow: var(--shadow-soft);
  backdrop-filter: blur(10px);
}

.stat-label {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--color-muted);
  margin-bottom: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: var(--color-primary);
  margin-bottom: 4px;
}

.stat-change {
  font-size: 13px;
  color: var(--color-text-soft);
}

.section {
  background: var(--glass-bg-strong);
  border-radius: 16px;
  padding: 32px;
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(14px);
}

.section-header {
  margin-bottom: 24px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-primary);
}

.overview-content {
  display: grid;
  gap: 16px;
}

.overview-item {
  display: grid;
  grid-template-columns: 48px 1fr auto;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.42);
  backdrop-filter: blur(10px);
}

.overview-icon {
  font-size: 24px;
  line-height: 1;
}

.overview-info h3 {
  color: var(--color-text);
  font-size: 16px;
  margin-bottom: 4px;
}

.overview-info p {
  color: var(--color-text-soft);
  font-size: 14px;
}

.status-content {
  display: grid;
  gap: 12px;
}

.status-item {
  padding: 16px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.42);
  border: 1px solid rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
}

.status-item.success {
  background: rgba(34, 197, 94, 0.1);
  border-color: rgba(34, 197, 94, 0.3);
}

.status-badge {
  color: #22c55e;
  font-weight: 600;
  font-size: 14px;
}

.status-label {
  color: var(--color-text-soft);
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  margin-bottom: 4px;
}

.status-message {
  color: var(--color-text);
  font-size: 14px;
}

.error-message {
  background: rgba(255, 255, 255, 0.56);
  color: #b91c1c;
  padding: 16px;
  border-radius: 12px;
  text-align: center;
  margin-top: 20px;
  border: 1px solid var(--glass-border);
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .overview-item {
    grid-template-columns: 1fr;
  }

  .overview-icon {
    order: -1;
    font-size: 20px;
  }
}
</style>

