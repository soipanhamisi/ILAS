<template>
  <div class="container dashboard">
    <h1 class="page-title">Admin Dashboard</h1>
    <p class="welcome-text">Live system observability and user activity</p>

    <div v-if="loading" class="loading">Loading monitoring data...</div>

    <div v-else class="dashboard-content">
      <div class="stats-grid">
        <div class="stat-card">
          <p class="stat-label">Total Students</p>
          <p class="stat-value">{{ stats.totalStudents }}</p>
        </div>
        <div class="stat-card">
          <p class="stat-label">Total Instructors</p>
          <p class="stat-value">{{ stats.totalInstructors }}</p>
        </div>
        <div class="stat-card">
          <p class="stat-label">Total Courses</p>
          <p class="stat-value">{{ stats.totalCourses }}</p>
        </div>
        <div class="stat-card">
          <p class="stat-label">Total Exams</p>
          <p class="stat-value">{{ stats.totalExams }}</p>
        </div>
      </div>

      <div class="stats-grid">
        <div class="stat-card emphasis">
          <p class="stat-label">Thread Pool Capacity</p>
          <p class="stat-value">{{ monitoring.threadPoolCapacity }}</p>
        </div>
        <div class="stat-card emphasis">
          <p class="stat-label">Global Active Requests</p>
          <p class="stat-value">{{ monitoring.globalActiveRequests }}</p>
        </div>
        <div class="stat-card emphasis">
          <p class="stat-label">Global Utilization</p>
          <p class="stat-value">{{ formatPercent(monitoring.globalUtilization) }}</p>
        </div>
        <div class="stat-card emphasis">
          <p class="stat-label">Active Users / Total Users</p>
          <p class="stat-value">{{ monitoring.users.activeUsers }} / {{ monitoring.users.totalUsers }}</p>
        </div>
      </div>

      <div class="chart-grid">
        <div class="section">
          <h2 class="section-title">Request Utilization Trend</h2>
          <svg class="sparkline" viewBox="0 0 320 120" preserveAspectRatio="none">
            <polyline :points="requestPoints" fill="none" stroke="#1d4ed8" stroke-width="3"/>
          </svg>
        </div>
        <div class="section">
          <h2 class="section-title">Active Users Trend</h2>
          <svg class="sparkline" viewBox="0 0 320 120" preserveAspectRatio="none">
            <polyline :points="activeUserPoints" fill="none" stroke="#16a34a" stroke-width="3"/>
          </svg>
        </div>
      </div>

      <div class="section">
        <h2 class="section-title">All Endpoint Health (Concurrent Requests vs Capacity)</h2>
        <div class="endpoint-table">
          <div class="endpoint-row header">
            <span>Endpoint</span>
            <span>Active</span>
            <span>RPS</span>
            <span>Total</span>
            <span>Utilization</span>
            <span>Status</span>
          </div>
          <div v-for="entry in monitoring.endpointHealth" :key="entry.endpoint" class="endpoint-row">
            <span class="endpoint-name">{{ entry.endpoint }}</span>
            <span>{{ entry.activeRequests }}</span>
            <span>{{ formatRate(entry.requestsPerSecond) }}</span>
            <span>{{ entry.totalRequests || 0 }}</span>
            <span>
              <div class="bar-wrap">
                <div class="bar-fill" :style="{ width: Math.min(entry.utilization * 100, 100) + '%' }"></div>
              </div>
              {{ formatPercent(entry.utilization) }}
            </span>
            <span :class="['status-pill', statusClass(entry.status)]">{{ entry.status }}</span>
          </div>
        </div>
      </div>

      <div class="status-line">Last updated: {{ lastUpdated }}</div>
    </div>

    <div v-if="error" class="error-message">{{ error }}</div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { adminAPI } from '../services/api'

const authStore = useAuthStore()
const router = useRouter()

// Long polling configuration for realtime monitoring
const refreshIntervalMs = 5000
let pollingTimer = null
let pollingActive = false
let monitoringRequestInFlight = false

const stats = ref({ totalStudents: 0, totalInstructors: 0, totalCourses: 0, totalExams: 0 })
const monitoringData = ref({
  threadPoolCapacity: 0,
  globalActiveRequests: 0,
  globalUtilization: 0,
  users: { totalUsers: 0, activeUsers: 0 },
  endpointHealth: [],
  series: { requestUtilization: [], activeUsers: [] }
})
const monitoring = computed(() => monitoringData.value)

const loading = ref(false)
const error = ref('')
const lastUpdated = ref('Never')

const resolveAdminId = () => {
  if (!authStore.userId) {
    authStore.checkAuth()
  }
  const candidate = Number(authStore.userId)
  return Number.isInteger(candidate) && candidate > 0 ? candidate : null
}

const handleInvalidAdminSession = () => {
  stopMonitoringPolling()
  authStore.logout()
  error.value = 'Your admin session is no longer valid. Please sign in again.'
  router.push('/login')
}

const formatPercent = (value) => `${Math.round((value || 0) * 100)}%`
const formatRate = (value) => `${Number(value || 0).toFixed(2)}/s`

const buildSparklinePoints = (series, yMax) => {
  if (!series || series.length === 0) return '0,120 320,120'
  const maxVal = yMax || Math.max(...series.map(p => Number(p.value || 0)), 1)
  return series.map((point, index) => {
    const x = (index / Math.max(series.length - 1, 1)) * 320
    const y = 120 - ((Number(point.value || 0) / maxVal) * 110)
    return `${x},${y}`
  }).join(' ')
}

const requestPoints = computed(() => buildSparklinePoints(monitoring.value.series?.requestUtilization, 1))
const activeUserPoints = computed(() => buildSparklinePoints(monitoring.value.series?.activeUsers))

const statusClass = (status) => {
  if (status === 'HIGH') return 'high'
  if (status === 'ELEVATED') return 'elevated'
  return 'healthy'
}

/**
 * Load dashboard summary statistics via HTTP
 */
const loadDashboardSummary = async () => {
  const adminId = resolveAdminId()
  if (!adminId) {
    return
  }

  try {
    const summaryRes = await adminAPI.getDashboardSummary(adminId)
    if (summaryRes.data.success) {
      const data = summaryRes.data.data
      stats.value = {
        totalStudents: data.totalStudents,
        totalInstructors: data.totalInstructors,
        totalCourses: data.totalCourses,
        totalExams: data.totalExams
      }
    }
  } catch (err) {
    console.error('Error loading dashboard summary:', err)
  }
}

const loadMonitoringSummary = async () => {
  if (monitoringRequestInFlight) {
    return
  }

  const adminId = resolveAdminId()
  if (!adminId) {
    error.value = 'Session expired. Please sign in again.'
    return
  }

  monitoringRequestInFlight = true
  try {
    const monitoringRes = await adminAPI.getMonitoringSummary(adminId)
    if (monitoringRes.data.success) {
      monitoringData.value = monitoringRes.data.data
      error.value = ''
      lastUpdated.value = new Date().toLocaleString()
    } else {
      if ((monitoringRes.data.message || '').includes('Admin not found')) {
        handleInvalidAdminSession()
        return
      }
      error.value = monitoringRes.data.message || 'Unable to load monitoring data'
    }
  } catch (err) {
    console.error('Error loading monitoring summary:', err)
    if ((err.response?.data?.message || '').includes('Admin not found')) {
      handleInvalidAdminSession()
      return
    }
    error.value = 'Unable to load monitoring data'
  } finally {
    monitoringRequestInFlight = false
  }
}

const runLongPollingCycle = async () => {
  if (!pollingActive) {
    return
  }

  await loadMonitoringSummary()

  if (!pollingActive) {
    return
  }

  pollingTimer = setTimeout(runLongPollingCycle, refreshIntervalMs)
}

const startMonitoringPolling = () => {
  if (pollingActive) {
    return
  }
  pollingActive = true
  runLongPollingCycle()
}

const stopMonitoringPolling = () => {
  pollingActive = false
  if (pollingTimer) {
    clearTimeout(pollingTimer)
    pollingTimer = null
  }
}

onMounted(async () => {
  loading.value = true
  await Promise.all([loadDashboardSummary(), loadMonitoringSummary()])
  loading.value = false
  startMonitoringPolling()
})

onBeforeUnmount(() => {
  stopMonitoringPolling()
})
</script>

<style scoped>
.dashboard { padding: 30px 20px; }
.page-title { font-size: 34px; color: var(--color-primary); }
.welcome-text { color: var(--color-text-soft); margin-bottom: 20px; }
.loading { padding: 32px; text-align: center; background: var(--glass-bg-strong); border-radius: 12px; }
.dashboard-content { display: grid; gap: 18px; }
.stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 12px; }
.stat-card { background: var(--glass-bg-strong); border: 1px solid var(--glass-border); border-radius: 12px; padding: 16px; }
.stat-card.emphasis { border-color: rgba(29, 78, 216, 0.3); }
.stat-label { font-size: 12px; text-transform: uppercase; color: var(--color-muted); }
.stat-value { font-size: 28px; font-weight: 700; color: var(--color-primary); }
.chart-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 12px; }
.section { background: var(--glass-bg-strong); border: 1px solid var(--glass-border); border-radius: 12px; padding: 16px; }
.section-title { font-size: 18px; margin-bottom: 10px; color: var(--color-primary); }
.sparkline { width: 100%; height: 130px; background: rgba(248, 250, 252, 0.8); border-radius: 10px; }
.endpoint-table { display: grid; gap: 8px; }
.endpoint-table { max-height: 380px; overflow-y: auto; }
.endpoint-row { display: grid; grid-template-columns: 2fr .4fr .6fr .6fr 1fr .6fr; gap: 10px; align-items: center; font-size: 14px; }
.endpoint-row.header { font-weight: 700; color: var(--color-muted); text-transform: uppercase; font-size: 12px; }
.endpoint-name { word-break: break-all; }
.bar-wrap { width: 100%; height: 8px; border-radius: 20px; background: #e5e7eb; margin-bottom: 4px; }
.bar-fill { height: 100%; background: linear-gradient(90deg, #2563eb, #1d4ed8); border-radius: 20px; }
.status-pill { padding: 4px 10px; border-radius: 20px; font-size: 12px; text-align: center; }
.status-pill.healthy { background: rgba(22, 163, 74, 0.15); color: #15803d; }
.status-pill.elevated { background: rgba(245, 158, 11, 0.2); color: #b45309; }
.status-pill.high { background: rgba(220, 38, 38, 0.2); color: #b91c1c; }
.status-line { color: var(--color-text-soft); font-size: 13px; }
.error-message { margin-top: 14px; background: rgba(255,255,255,0.8); color: #b91c1c; border: 1px solid var(--glass-border); border-radius: 10px; padding: 12px; }
</style>

