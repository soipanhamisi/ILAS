<template>
  <div class="container dashboard">
    <h1 class="page-title">👨‍🎓 Student Dashboard</h1>
    <p class="welcome-text">Welcome, {{ authStore.user?.name }}!</p>

    <div v-if="loading" class="loading">Loading exams...</div>

    <div v-else class="dashboard-content">
      <!-- Available Exams Section -->
      <div class="section">
        <h2 class="section-title">📚 Available Exams</h2>

        <div v-if="availableExams.length === 0" class="empty-state">
          <p>No exams available at the moment</p>
        </div>

        <div v-else class="exams-grid">
          <div
            v-for="exam in availableExams"
            :key="exam.examId"
            class="exam-card"
          >
            <div class="exam-header">
              <h3>{{ exam.examTitle }}</h3>
              <span class="exam-score">{{ exam.maxScore }} pts</span>
            </div>
            <p class="exam-course">{{ exam.courseTitle }}</p>
            <p class="exam-instructor">Instructor: {{ exam.instructorName }}</p>

            <div class="exam-actions">
              <button
                v-if="!isSubmitted(exam.examId)"
                @click="goToExam(exam.examId)"
                class="btn-primary"
              >
                Take Exam
              </button>
              <button
                v-else
                @click="viewResults(exam.examId)"
                class="btn-success"
              >
                View Results
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- My Submissions Section -->
      <div class="section">
        <h2 class="section-title">📝 My Submissions</h2>

        <div v-if="submissions.length === 0" class="empty-state">
          <p>You haven't submitted any exams yet</p>
        </div>

        <div v-else class="submissions-list">
          <div
            v-for="submission in submissions"
            :key="submission.submissionId"
            class="submission-item"
          >
            <div class="submission-info">
              <h4>{{ submission.examTitle }}</h4>
              <p class="submission-date">
                Submitted: {{ formatDate(submission.submittedAt) }}
              </p>
            </div>

            <div class="submission-status">
              <div v-if="submission.grade !== null" class="graded">
                <span class="grade-badge">
                  {{ submission.grade }} / {{ submission.maxScore }}
                </span>
                <button
                  @click="viewResults(submission.examId)"
                  class="btn-secondary btn-sm"
                >
                  View Details
                </button>
              </div>
              <div v-else class="pending">
                <span class="status-badge pending">Pending Grade</span>
              </div>
            </div>
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
import { studentAPI } from '../services/api'

const router = useRouter()
const authStore = useAuthStore()

const availableExams = ref([])
const submissions = ref([])
const submittedExamIds = ref(new Set())
const loading = ref(false)
const error = ref('')

const loadData = async () => {
  loading.value = true
  error.value = ''

  try {
    // Load available exams
    const examsResponse = await studentAPI.getAvailableExams(authStore.userId)
    if (examsResponse.data.success) {
      availableExams.value = examsResponse.data.data
    }

    // Load submissions
    const submissionsResponse = await studentAPI.getAllSubmissions(authStore.userId)
    if (submissionsResponse.data.success) {
      submissions.value = submissionsResponse.data.data

      // Build set of submitted exam IDs
      submittedExamIds.value = new Set(
        submissions.value.map(s => s.examId)
      )
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to load data'
    console.error('Error loading data:', err)
  } finally {
    loading.value = false
  }
}

const isSubmitted = (examId) => {
  return submittedExamIds.value.has(examId)
}

const goToExam = (examId) => {
  router.push(`/student/exams/${examId}`)
}

const viewResults = (examId) => {
  router.push(`/student/results/${examId}`)
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString()
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
  color: var(--color-white);
  margin-bottom: 8px;
}

.welcome-text {
  color: rgba(255, 255, 255, 0.88);
  font-size: 18px;
  margin-bottom: 32px;
}

.loading {
  background: rgba(255, 255, 255, 0.96);
  padding: 60px 20px;
  text-align: center;
  border-radius: 16px;
  color: var(--color-text-soft);
  box-shadow: var(--shadow-soft);
}

.dashboard-content {
  display: grid;
  gap: 32px;
}

.section {
  background: rgba(255, 255, 255, 0.96);
  border-radius: 16px;
  padding: 32px;
  box-shadow: var(--shadow-soft);
  border: 1px solid rgba(67, 96, 50, 0.08);
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-primary);
  margin-bottom: 24px;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: var(--color-text-soft);
}

.exams-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.exam-card {
  border: 2px solid rgba(112, 113, 77, 0.22);
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
  background: rgba(207, 218, 197, 0.2);
}

.exam-card:hover {
  border-color: var(--color-accent);
  box-shadow: 0 4px 12px rgba(56, 101, 71, 0.18);
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 12px;
}

.exam-header h3 {
  font-size: 18px;
  color: var(--color-text);
  flex: 1;
}

.exam-score {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
  color: var(--color-white);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
}

.exam-course,
.exam-instructor {
  color: var(--color-text-soft);
  font-size: 14px;
  margin-bottom: 8px;
}

.exam-actions {
  margin-top: 16px;
}

.submissions-list {
  display: grid;
  gap: 16px;
}

.submission-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 2px solid rgba(112, 113, 77, 0.22);
  border-radius: 12px;
  background: rgba(207, 218, 197, 0.18);
}

.submission-info h4 {
  color: var(--color-text);
  font-size: 16px;
  margin-bottom: 4px;
}

.submission-date {
  color: var(--color-text-soft);
  font-size: 14px;
}

.submission-status {
  display: flex;
  align-items: center;
  gap: 12px;
}

.graded,
.pending {
  display: flex;
  align-items: center;
  gap: 12px;
}

.grade-badge {
  background: linear-gradient(135deg, var(--color-accent) 0%, var(--color-primary) 100%);
  color: var(--color-white);
  padding: 8px 16px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 16px;
}

.status-badge {
  padding: 8px 16px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 14px;
}

.status-badge.pending {
  background: rgba(207, 218, 197, 0.85);
  color: var(--color-muted);
}

.btn-sm {
  padding: 8px 16px;
  font-size: 14px;
}

.error-message {
  background: rgba(112, 113, 77, 0.16);
  color: var(--color-primary);
  padding: 16px;
  border-radius: 12px;
  text-align: center;
  margin-top: 20px;
  border: 1px solid rgba(67, 96, 50, 0.18);
}
</style>

