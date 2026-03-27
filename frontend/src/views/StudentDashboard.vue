<template>
  <div class="container dashboard">
    <h1 class="page-title">👨‍🎓 Student Dashboard</h1>
    <p class="welcome-text">Welcome, {{ authStore.user?.name }}!</p>

    <div v-if="loading" class="loading">Loading exams...</div>

    <div v-else class="dashboard-content">
      <div class="overview-grid">
        <div class="overview-card">
          <p class="overview-label">Enrolled Courses</p>
          <p class="overview-value">{{ enrolledCourses.length }}</p>
        </div>
        <div class="overview-card">
          <p class="overview-label">Pending Assessments</p>
          <p class="overview-value">{{ pendingAssessments.length }}</p>
        </div>
        <div class="overview-card">
          <p class="overview-label">Total Submissions</p>
          <p class="overview-value">{{ submissions.length }}</p>
        </div>
        <div class="overview-card">
          <p class="overview-label">Graded Results</p>
          <p class="overview-value">{{ gradedSubmissionsCount }}</p>
        </div>
      </div>

      <div class="section">
        <div class="section-header">
          <h2 class="section-title">✅ Enrolled Courses</h2>
          <span class="pill">{{ enrolledCourses.length }} active</span>
        </div>

        <div v-if="enrolledCourses.length === 0" class="empty-state">
          <p>You are not enrolled in any courses yet</p>
        </div>

        <div v-else class="courses-grid">
          <div
            v-for="course in enrolledCourses"
            :key="`enrolled-${course.courseId}`"
            class="course-card"
          >
            <h3>{{ course.courseTitle }}</h3>
            <p class="course-meta">Instructor: {{ course.instructorName || 'TBA' }}</p>
            <span class="status-badge enrolled">Enrolled</span>
          </div>
        </div>
      </div>

      <div class="section">
        <div class="section-header">
          <h2 class="section-title">⏳ Pending Assessments</h2>
          <span class="pill">{{ pendingAssessments.length }} pending</span>
        </div>

        <div v-if="pendingAssessments.length === 0" class="empty-state">
          <p>Great work! You have no pending assessments.</p>
        </div>

        <div v-else class="exams-grid">
          <div
            v-for="exam in pendingAssessments"
            :key="`pending-${exam.examId}`"
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
                @click="goToExam(exam.examId)"
                class="btn-primary"
              >
                Take Exam
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="section">
        <div class="section-header">
          <h2 class="section-title">🧭 Course Enrollment</h2>
          <span class="pill">{{ enrolledCourseIds.size }} enrolled</span>
        </div>

        <div v-if="allCourses.length === 0" class="empty-state">
          <p>No courses available right now</p>
        </div>

        <div v-else class="courses-grid">
          <div
            v-for="course in allCourses"
            :key="course.courseId"
            class="course-card"
          >
            <h3>{{ course.courseTitle }}</h3>
            <p class="course-meta">Instructor: {{ course.instructorName || 'TBA' }}</p>

            <button
              v-if="!isEnrolled(course.courseId)"
              @click="enrollInCourse(course.courseId)"
              class="btn-primary"
              :disabled="enrollingCourseId === course.courseId"
            >
              {{ enrollingCourseId === course.courseId ? 'Enrolling...' : 'Enroll' }}
            </button>

            <button v-else class="btn-success" disabled>
              Enrolled
            </button>
          </div>
        </div>
      </div>

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

    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { studentAPI } from '../services/api'

const router = useRouter()
const authStore = useAuthStore()

const availableExams = ref([])
const submissions = ref([])
const allCourses = ref([])
const enrolledCourses = ref([])
const submittedExamIds = ref(new Set())
const enrolledCourseIds = ref(new Set())
const enrollingCourseId = ref(null)
const loading = ref(false)
const error = ref('')
const successMessage = ref('')

const pendingAssessments = computed(() => {
  return availableExams.value.filter(exam => !submittedExamIds.value.has(exam.examId))
})

const gradedSubmissionsCount = computed(() => {
  return submissions.value.filter(submission => submission.grade !== null).length
})

const loadData = async () => {
  loading.value = true
  error.value = ''

  try {
    const [coursesResponse, enrolledCoursesResponse, examsResponse, submissionsResponse] = await Promise.all([
      studentAPI.getAllCourses(authStore.userId),
      studentAPI.getEnrolledCourses(authStore.userId),
      studentAPI.getAvailableExams(authStore.userId),
      studentAPI.getAllSubmissions(authStore.userId)
    ])

    if (coursesResponse.data.success) {
      allCourses.value = coursesResponse.data.data
    }

    if (enrolledCoursesResponse.data.success) {
      enrolledCourses.value = enrolledCoursesResponse.data.data
      enrolledCourseIds.value = new Set(
        enrolledCoursesResponse.data.data.map(course => course.courseId)
      )
    }

    // Load available exams
    if (examsResponse.data.success) {
      availableExams.value = examsResponse.data.data
    }

    // Load submissions
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

const isEnrolled = (courseId) => {
  return enrolledCourseIds.value.has(courseId)
}

const enrollInCourse = async (courseId) => {
  if (enrollingCourseId.value) {
    return
  }

  enrollingCourseId.value = courseId
  error.value = ''
  successMessage.value = ''

  try {
    const response = await studentAPI.enrollInCourse(authStore.userId, courseId)
    successMessage.value = response.data.message || 'You are now enrolled in the course'
    await loadData()
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to enroll in course'
  } finally {
    enrollingCourseId.value = null
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

.overview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 16px;
}

.overview-card {
  background: rgba(255, 255, 255, 0.42);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  padding: 16px;
  backdrop-filter: blur(10px);
}

.overview-label {
  margin: 0;
  font-size: 13px;
  color: var(--color-text-soft);
}

.overview-value {
  margin: 8px 0 0;
  font-size: 28px;
  font-weight: 700;
  color: var(--color-primary);
}

.section {
  background: var(--glass-bg-strong);
  border-radius: 16px;
  padding: 32px;
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(14px);
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-primary);
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.pill {
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.56);
  color: var(--color-primary);
  font-size: 13px;
  font-weight: 700;
  border: 1px solid var(--glass-border);
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}

.course-card {
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  padding: 18px;
  background: rgba(255, 255, 255, 0.42);
  backdrop-filter: blur(10px);
  display: grid;
  gap: 10px;
}

.course-card h3 {
  color: var(--color-text);
  font-size: 17px;
}

.course-meta {
  color: var(--color-text-soft);
  font-size: 14px;
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
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.42);
  backdrop-filter: blur(10px);
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
  background: linear-gradient(135deg, var(--color-surface) 0%, var(--color-accent) 100%);
  color: #27423a;
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
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.42);
  backdrop-filter: blur(10px);
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
  background: linear-gradient(135deg, var(--color-surface) 0%, var(--color-accent) 100%);
  color: #27423a;
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
  background: rgba(255, 255, 255, 0.6);
  color: var(--color-muted);
}

.status-badge.enrolled {
  background: linear-gradient(135deg, var(--color-surface) 0%, var(--color-accent) 100%);
  color: #27423a;
  width: fit-content;
}

.btn-sm {
  padding: 8px 16px;
  font-size: 14px;
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

.success-message {
  background: rgba(255, 255, 255, 0.56);
  color: #2f6e5c;
  padding: 16px;
  border-radius: 12px;
  text-align: center;
  margin-top: 20px;
  border: 1px solid rgba(61, 130, 110, 0.25);
}
</style>
