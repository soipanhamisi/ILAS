<template>
  <div class="container dashboard">
    <h1 class="page-title">Instructor Dashboard</h1>
    <p class="welcome-text">Welcome, {{ authStore.user?.name }}!</p>

    <div class="dashboard-actions">
      <router-link to="/instructor/exams/create" class="action-card">
        <div class="action-icon">+</div>
        <h3>Create New Exam</h3>
        <p>Upload CSV template and create assessment</p>
      </router-link>
    </div>

    <div v-if="loading" class="loading">Loading dashboard...</div>

    <div v-else class="dashboard-content">
      <div class="stats-grid">
        <div class="stat-card">
          <p class="stat-label">Courses Taught</p>
          <p class="stat-value">{{ dashboard.coursesTaught }}</p>
        </div>
        <div class="stat-card">
          <p class="stat-label">New Enrollments (7d)</p>
          <p class="stat-value">{{ dashboard.newEnrollments }}</p>
        </div>
        <div class="stat-card">
          <p class="stat-label">Average Performance</p>
          <p class="stat-value">{{ formatPercent(dashboard.averagePerformancePct) }}</p>
        </div>
        <div class="stat-card">
          <p class="stat-label">Tests To Be Graded</p>
          <p class="stat-value">{{ dashboard.testsToBeGraded }}</p>
        </div>
      </div>

      <div class="section">
        <h2 class="section-title">Courses You Teach</h2>

        <div v-if="dashboard.courses.length === 0" class="empty-state">
          <p>No courses assigned yet</p>
        </div>

        <div v-else class="courses-grid">
          <div
            v-for="course in dashboard.courses"
            :key="course.courseId"
            class="course-card"
          >
            <h3>{{ course.courseTitle }}</h3>
            <p class="course-meta">Course ID: {{ course.courseId }}</p>
            <p class="course-meta">Enrollments: {{ course.enrollmentCount }}</p>
            <p class="course-meta">New enrollments (7d): {{ course.newEnrollments }}</p>
            <p class="course-meta">Average performance: {{ formatPercent(course.averagePerformancePct) }}</p>
            <p class="course-meta">Pending grading: {{ course.testsToBeGraded }}</p>
          </div>
        </div>
      </div>

      <div class="section">
        <h2 class="section-title">Tests To Be Graded</h2>

        <div v-if="dashboard.testsToGrade.length === 0" class="empty-state">
          <p>No tests pending grading</p>
        </div>

        <div v-else class="queue-list">
          <div
            v-for="test in dashboard.testsToGrade"
            :key="test.examId"
            class="queue-item"
          >
            <div>
              <h3>{{ test.examTitle }}</h3>
              <p class="course-meta">{{ test.courseTitle }} ({{ test.courseId }})</p>
              <p class="course-meta">Ungraded submissions: {{ test.ungradedCount }}</p>
            </div>
            <router-link :to="`/instructor/exams/${test.examId}`" class="btn-primary">
              Grade Now
            </router-link>
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
import { useAuthStore } from '../stores/auth'
import { instructorAPI } from '../services/api'

const authStore = useAuthStore()
const loading = ref(false)
const error = ref('')
const dashboard = ref({
  coursesTaught: 0,
  newEnrollments: 0,
  averagePerformancePct: 0,
  testsToBeGraded: 0,
  courses: [],
  testsToGrade: []
})

const formatPercent = (value) => {
  if (value === null || value === undefined) {
    return '0.0%'
  }

  return `${Number(value).toFixed(1)}%`
}

const loadDashboard = async () => {
  loading.value = true
  error.value = ''

  try {
    const response = await instructorAPI.getDashboardSummary(authStore.userId)

    if (response.data.success) {
      dashboard.value = response.data.data
    } else {
      error.value = response.data.message || 'Failed to load dashboard'
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to load dashboard'
    console.error('Error loading dashboard:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDashboard()
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

.dashboard-actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.action-card {
  background: var(--glass-bg-strong);
  padding: 28px;
  border-radius: 16px;
  text-align: center;
  text-decoration: none;
  transition: all 0.3s ease;
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(14px);
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-strong);
}

.action-icon {
  font-size: 42px;
  margin-bottom: 10px;
  font-weight: 700;
}

.action-card h3 {
  font-size: 20px;
  color: var(--color-primary);
  margin-bottom: 8px;
}

.action-card p {
  color: var(--color-text-soft);
  font-size: 14px;
}

.loading {
  background: var(--glass-bg-strong);
  padding: 40px;
  text-align: center;
  border-radius: 16px;
  color: var(--color-text-soft);
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--glass-border);
}

.dashboard-content {
  display: grid;
  gap: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.stat-card {
  background: var(--glass-bg-strong);
  border-radius: 14px;
  padding: 20px;
  border: 1px solid var(--glass-border);
  box-shadow: var(--shadow-soft);
}

.stat-label {
  color: var(--color-text-soft);
  font-size: 14px;
  margin-bottom: 8px;
}

.stat-value {
  color: var(--color-primary);
  font-size: 28px;
  font-weight: 700;
}

.section {
  background: var(--glass-bg-strong);
  border-radius: 16px;
  padding: 28px;
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(14px);
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-primary);
  margin-bottom: 20px;
}

.empty-state {
  text-align: center;
  padding: 24px;
  color: var(--color-text-soft);
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.course-card {
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  padding: 18px;
  background: rgba(255, 255, 255, 0.42);
  display: grid;
  gap: 6px;
}

.course-card h3 {
  color: var(--color-text);
  font-size: 18px;
  margin-bottom: 2px;
}

.course-meta {
  color: var(--color-text-soft);
  font-size: 14px;
}

.queue-list {
  display: grid;
  gap: 12px;
}

.queue-item {
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.42);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.queue-item h3 {
  color: var(--color-text);
  font-size: 18px;
  margin-bottom: 4px;
}

.error-message {
  margin-top: 20px;
  background: rgba(255, 255, 255, 0.55);
  color: var(--color-primary);
  padding: 16px;
  border-radius: 12px;
  text-align: center;
  border: 1px solid var(--glass-border);
}
</style>

