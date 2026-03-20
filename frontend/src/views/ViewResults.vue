<template>
  <div class="container">
    <div class="page-header">
      <h1 class="page-title">📊 Exam Results</h1>
      <router-link to="/student" class="btn-secondary">← Back</router-link>
    </div>

    <div v-if="loading" class="loading">Loading results...</div>

    <div v-else-if="submission" class="results-container">
      <!-- Exam Info -->
      <div class="exam-info-card">
        <h2>{{ submission.examTitle }}</h2>
        <div class="submission-date">
          Submitted: {{ formatDate(submission.submittedAt) }}
        </div>
      </div>

      <!-- Grade Card -->
      <div class="grade-card">
        <div v-if="submission.grade !== null" class="graded-content">
          <div class="grade-header">
            <h3>Your Grade</h3>
            <div class="grade-display">
              <span class="grade-value">{{ submission.grade }}</span>
              <span class="grade-max">/ {{ submission.maxScore }}</span>
            </div>
          </div>

          <div class="grade-percentage">
            {{ calculatePercentage(submission.grade, submission.maxScore) }}%
          </div>

          <div class="grade-bar">
            <div
              class="grade-bar-fill"
              :style="{ width: calculatePercentage(submission.grade, submission.maxScore) + '%' }"
            ></div>
          </div>

          <div class="grading-info">
            <p>
              <strong>Graded by:</strong> {{ submission.gradedByInstructorName }}
            </p>
            <p>
              <strong>Graded on:</strong> {{ formatDate(submission.gradedAt) }}
            </p>
          </div>
        </div>

        <div v-else class="pending-grade">
          <div class="pending-icon">⏳</div>
          <h3>Grade Pending</h3>
          <p>Your instructor hasn't graded this exam yet. Check back later!</p>
        </div>
      </div>

      <!-- Feedback Section -->
      <div v-if="submission.grade !== null" class="feedback-section">
        <div class="feedback-card">
          <h3>📝 Instructor Feedback</h3>
          <div class="feedback-content">
            {{ submission.feedback }}
          </div>
        </div>

        <div class="justification-card">
          <h3>💡 Grade Justification</h3>
          <div class="justification-content">
            {{ submission.gradeJustification }}
          </div>
        </div>
      </div>

      <!-- Performance Indicator -->
      <div v-if="submission.grade !== null" class="performance-card">
        <h3>Performance Analysis</h3>
        <div class="performance-indicator">
          <div
            v-if="getPerformanceLevel(submission.grade, submission.maxScore) === 'excellent'"
            class="performance-badge excellent"
          >
            🌟 Excellent Performance!
          </div>
          <div
            v-else-if="getPerformanceLevel(submission.grade, submission.maxScore) === 'good'"
            class="performance-badge good"
          >
            ✓ Good Job!
          </div>
          <div
            v-else-if="getPerformanceLevel(submission.grade, submission.maxScore) === 'average'"
            class="performance-badge average"
          >
            📚 Room for Improvement
          </div>
          <div
            v-else
            class="performance-badge needs-work"
          >
            💪 Keep Working Hard!
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
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { studentAPI } from '../services/api'

const route = useRoute()
const authStore = useAuthStore()

const submission = ref(null)
const loading = ref(false)
const error = ref('')

const examId = computed(() => parseInt(route.params.examId))

const loadResults = async () => {
  loading.value = true
  error.value = ''

  try {
    const response = await studentAPI.getGradeAndFeedback(
      examId.value,
      authStore.userId
    )

    if (response.data.success) {
      submission.value = response.data.data
    } else {
      error.value = response.data.message
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to load results'
    console.error('Error loading results:', err)
  } finally {
    loading.value = false
  }
}

const calculatePercentage = (grade, maxScore) => {
  return Math.round((grade / maxScore) * 100)
}

const getPerformanceLevel = (grade, maxScore) => {
  const percentage = (grade / maxScore) * 100
  if (percentage >= 90) return 'excellent'
  if (percentage >= 75) return 'good'
  if (percentage >= 60) return 'average'
  return 'needs-work'
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString()
}

onMounted(() => {
  loadResults()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--color-primary);
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

.results-container {
  max-width: 900px;
  margin: 0 auto;
}

.exam-info-card {
  background: var(--glass-bg-strong);
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(14px);
}

.exam-info-card h2 {
  font-size: 28px;
  color: var(--color-primary);
  margin-bottom: 8px;
}

.submission-date {
  color: var(--color-text-soft);
  font-size: 16px;
}

.grade-card {
  background: var(--glass-bg-strong);
  border-radius: 16px;
  padding: 40px;
  margin-bottom: 24px;
  box-shadow: var(--shadow-soft);
  text-align: center;
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(14px);
}

.graded-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.grade-header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.grade-header h3 {
  font-size: 24px;
  color: var(--color-primary);
}

.grade-display {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.grade-value {
  font-size: 64px;
  font-weight: 800;
  color: var(--color-primary);
}

.grade-max {
  font-size: 32px;
  color: var(--color-text-soft);
  font-weight: 600;
}

.grade-percentage {
  font-size: 36px;
  font-weight: 700;
  color: var(--color-text);
  margin-bottom: 20px;
}

.grade-bar {
  width: 100%;
  height: 20px;
  background: rgba(255, 255, 255, 0.62);
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 24px;
}

.grade-bar-fill {
  height: 100%;
  background: linear-gradient(135deg, var(--color-surface) 0%, var(--color-accent) 100%);
  transition: width 1s ease;
}

.grading-info {
  width: 100%;
  background: rgba(255, 255, 255, 0.5);
  padding: 16px;
  border-radius: 12px;
  text-align: left;
  border: 1px solid rgba(255, 255, 255, 0.45);
}

.grading-info p {
  margin: 8px 0;
  color: var(--color-text-soft);
}

.pending-grade {
  padding: 40px 20px;
}

.pending-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.pending-grade h3 {
  font-size: 24px;
  color: var(--color-primary);
  margin-bottom: 12px;
}

.pending-grade p {
  color: var(--color-text-soft);
  font-size: 16px;
}

.feedback-section {
  display: grid;
  gap: 20px;
  margin-bottom: 24px;
}

.feedback-card,
.justification-card {
  background: var(--glass-bg-strong);
  border-radius: 16px;
  padding: 32px;
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(14px);
}

.feedback-card h3,
.justification-card h3 {
  font-size: 20px;
  color: var(--color-primary);
  margin-bottom: 16px;
}

.feedback-content,
.justification-content {
  color: var(--color-text-soft);
  font-size: 16px;
  line-height: 1.8;
  white-space: pre-wrap;
}

.performance-card {
  background: var(--glass-bg-strong);
  border-radius: 16px;
  padding: 32px;
  box-shadow: var(--shadow-soft);
  text-align: center;
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(14px);
}

.performance-card h3 {
  font-size: 20px;
  color: var(--color-primary);
  margin-bottom: 20px;
}

.performance-badge {
  display: inline-block;
  padding: 16px 32px;
  border-radius: 12px;
  font-size: 20px;
  font-weight: 600;
}

.performance-badge.excellent {
  background: linear-gradient(135deg, var(--color-surface) 0%, var(--color-accent) 100%);
  color: #27423a;
}

.performance-badge.good {
  background: linear-gradient(135deg, #c7e8b7 0%, #b6dfd9 100%);
  color: #27423a;
}

.performance-badge.average {
  background: linear-gradient(135deg, #ebeeb9 0%, #d0e4a1 100%);
  color: #425335;
}

.performance-badge.needs-work {
  background: linear-gradient(135deg, #f6f5e6 0%, #d7e6c0 100%);
  color: var(--color-text);
}

.error-message {
  background: rgba(255, 255, 255, 0.56);
  color: var(--color-primary);
  padding: 16px;
  border-radius: 12px;
  text-align: center;
  margin-top: 20px;
  border: 1px solid var(--glass-border);
}
</style>
