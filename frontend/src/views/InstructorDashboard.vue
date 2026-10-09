<template>
  <div class="container dashboard">
    <h1 class="page-title text-enter">Good day, {{ authStore.user?.name }}.</h1>
    <p class="welcome-text">Here is what is happening across your classes today.</p>

    <div class="dashboard-actions">
      <router-link to="/instructor/exams/create" class="action-card">
        <div class="action-icon">+</div>
        <h3>Create assessment</h3>
        <p>Upload a template or build questions manually</p>
      </router-link>
    </div>

    <div v-if="loading" class="loading">Loading dashboard...</div>

    <div v-else class="dashboard-content">
      <div class="stats-grid">
        <div class="stat-card">
          <p class="stat-label">Courses taught</p>
          <p class="stat-value">{{ dashboard.coursesTaught }}</p>
        </div>
        <div class="stat-card">
          <p class="stat-label">New enrollments (7d)</p>
          <p class="stat-value">{{ dashboard.newEnrollments }}</p>
        </div>
        <div class="stat-card">
          <p class="stat-label">Average performance</p>
          <p class="stat-value">{{ formatPercent(dashboard.averagePerformancePct) }}</p>
        </div>
        <div class="stat-card">
          <p class="stat-label">Tests to be graded</p>
          <p class="stat-value">{{ dashboard.testsToBeGraded }}</p>
        </div>
      </div>

      <div class="section">
        <h2 class="section-title">Student activity trend (14d)</h2>
        <p class="section-subtitle">
          Unique students submitting course work per day across your courses.
        </p>
        <div class="trend-metric">
          Latest active students: {{ getLatestTrendCount(dashboard.activeStudentsTrend) }}
        </div>
        <svg class="sparkline" viewBox="0 0 320 120" preserveAspectRatio="none">
          <polyline :points="overallActiveStudentPoints" fill="none" stroke="#bd680a" stroke-width="3" />
        </svg>
      </div>

      <div class="section">
        <h2 class="section-title">Courses you teach</h2>

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
            <p class="course-meta">Latest active students: {{ getLatestTrendCount(course.activeStudentsTrend) }}</p>
            <svg class="course-sparkline" viewBox="0 0 320 100" preserveAspectRatio="none">
              <polyline
                :points="buildSparklinePoints(course.activeStudentsTrend, null, 100)"
                fill="none"
                stroke="#1f615b"
                stroke-width="3"
              />
            </svg>
          </div>
        </div>
      </div>

      <div class="section">
        <h2 class="section-title">Tests to be graded</h2>

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
              Grade now
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
import { computed, ref, onMounted } from 'vue'
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
  activeStudentsTrend: [],
  courses: [],
  testsToGrade: []
})

const buildSparklinePoints = (series, yMax = null, chartHeight = 120) => {
  if (!series || series.length === 0) {
    return `0,${chartHeight} 320,${chartHeight}`
  }

  const maxVal = yMax || Math.max(...series.map(point => Number(point.activeStudents || point.value || 0)), 1)
  return series.map((point, index) => {
    const x = (index / Math.max(series.length - 1, 1)) * 320
    const rawValue = Number(point.activeStudents || point.value || 0)
    const y = chartHeight - ((rawValue / maxVal) * (chartHeight - 10))
    return `${x},${y}`
  }).join(' ')
}

const getLatestTrendCount = (series) => {
  if (!series || series.length === 0) {
    return 0
  }
  const latest = series[series.length - 1]
  return Number(latest.activeStudents || latest.value || 0)
}

const overallActiveStudentPoints = computed(() => buildSparklinePoints(dashboard.value.activeStudentsTrend))

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
  padding: 10px 2px 16px;
}

.page-title {
  font-size: clamp(38px, 5vw, 68px);
  font-weight: 800;
  line-height: 0.95;
  color: #3b1712;
}

.welcome-text {
  color: #705548;
  font-size: 18px;
  margin-top: 10px;
  margin-bottom: 24px;
}

.dashboard-actions {
  display: grid;
  grid-template-columns: minmax(240px, 1fr);
  margin-bottom: 20px;
}

.action-card {
  background: #1f5a54;
  padding: 28px;
  border-radius: 22px;
  text-decoration: none;
  color: #f6f8ea;
  display: grid;
  gap: 6px;
  border: 1px solid rgba(26, 87, 81, 0.6);
  box-shadow: var(--shadow-soft);
}

.action-card h3 {
  font-size: 28px;
}

.action-card p {
  color: #c8d29f;
}

.action-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: rgba(230, 206, 146, 0.24);
  color: #f4deab;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
}

.loading {
  background: #fffefb;
  padding: 40px;
  text-align: center;
  border-radius: 18px;
  color: #705548;
  border: 1px solid rgba(116, 83, 64, 0.16);
}

.dashboard-content {
  display: grid;
  gap: 22px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 14px;
}

.stat-card {
  background: #fffefb;
  border-radius: 18px;
  padding: 20px;
  border: 1px solid rgba(116, 83, 64, 0.16);
}

.stat-label {
  color: #81685a;
  font-size: 14px;
  margin-bottom: 8px;
}

.stat-value {
  color: #3b1712;
  font-size: 40px;
  font-weight: 800;
}

.section {
  background: #fffefb;
  border-radius: 24px;
  padding: 24px;
  border: 1px solid rgba(116, 83, 64, 0.16);
}

.section-title {
  font-size: 34px;
  font-weight: 800;
  color: #3b1712;
  margin-bottom: 10px;
}

.section-subtitle {
  color: #6f5447;
  margin-bottom: 8px;
}

.trend-metric {
  color: #4a2a1f;
  font-weight: 700;
  margin-bottom: 12px;
}

.sparkline,
.course-sparkline {
  width: 100%;
  background: #f7edda;
  border-radius: 12px;
  border: 1px solid rgba(116, 83, 64, 0.14);
}

.sparkline {
  height: 130px;
}

.empty-state {
  text-align: center;
  padding: 24px;
  color: #81685a;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.course-card {
  border: 1px solid rgba(116, 83, 64, 0.15);
  border-radius: 16px;
  padding: 18px;
  background: #fff;
  display: grid;
  gap: 6px;
}

.course-card h3 {
  color: #3e2117;
  font-size: 20px;
  margin-bottom: 4px;
}

.course-meta {
  color: #705548;
  font-size: 14px;
}

.course-sparkline {
  height: 90px;
  margin-top: 8px;
}

.queue-list {
  display: grid;
  gap: 12px;
}

.queue-item {
  border: 1px solid rgba(116, 83, 64, 0.16);
  border-radius: 16px;
  padding: 16px;
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.queue-item h3 {
  color: #402118;
  font-size: 19px;
  margin-bottom: 4px;
}

.error-message {
  margin-top: 20px;
  background: rgba(184, 72, 52, 0.14);
  color: #8c2316;
  padding: 16px;
  border-radius: 12px;
  text-align: center;
  border: 1px solid rgba(184, 72, 52, 0.24);
}
</style>

