<template>
  <div class="container dashboard">
    <h1 class="page-title">👨‍🏫 Instructor Dashboard</h1>
    <p class="welcome-text">Welcome, {{ authStore.user?.name }}!</p>

    <div class="dashboard-actions">
      <router-link to="/instructor/exams/create" class="action-card">
        <div class="action-icon">➕</div>
        <h3>Create New Exam</h3>
        <p>Upload CSV template and create assessment</p>
      </router-link>
    </div>

    <div class="section">
      <h2 class="section-title">Your Courses</h2>

      <div class="form-group">
        <label>Select Course</label>
        <select v-model="selectedCourseId" @change="loadExams">
          <option value="">Choose a course</option>
          <option value="101">Introduction to Java (101)</option>
          <option value="102">Data Structures (102)</option>
          <option value="103">Web Development (103)</option>
        </select>
      </div>
    </div>

    <div v-if="loading" class="loading">Loading exams...</div>

    <div v-if="exams.length > 0" class="section">
      <h2 class="section-title">Exams</h2>
      <div class="exams-grid">
        <div v-for="exam in exams" :key="exam.examId" class="exam-card">
          <div class="exam-header">
            <h3>{{ exam.examTitle }}</h3>
            <span class="exam-score">{{ exam.maxScore }} pts</span>
          </div>
          <p class="exam-course">{{ exam.courseTitle }}</p>
          <div class="exam-actions">
            <router-link
              :to="`/instructor/exams/${exam.examId}`"
              class="btn-primary"
            >
              View Submissions
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
const exams = ref([])
const selectedCourseId = ref('')
const loading = ref(false)
const error = ref('')

const loadExams = async () => {
  if (!selectedCourseId.value) {
    exams.value = []
    return
  }

  loading.value = true
  error.value = ''

  try {
    const response = await instructorAPI.getExamsForCourse(
      parseInt(selectedCourseId.value),
      authStore.userId
    )

    if (response.data.success) {
      exams.value = response.data.data
    } else {
      error.value = response.data.message
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to load exams'
    console.error('Error loading exams:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  // Auto-load first course for demo
  selectedCourseId.value = '101'
  loadExams()
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

.dashboard-actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.action-card {
  background: rgba(255, 255, 255, 0.96);
  padding: 32px;
  border-radius: 16px;
  text-align: center;
  text-decoration: none;
  transition: all 0.3s ease;
  box-shadow: var(--shadow-soft);
  border: 1px solid rgba(67, 96, 50, 0.08);
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-strong);
}

.action-icon {
  font-size: 48px;
  margin-bottom: 16px;
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

.section {
  background: rgba(255, 255, 255, 0.96);
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: var(--shadow-soft);
  border: 1px solid rgba(67, 96, 50, 0.08);
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-primary);
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 8px;
}

.form-group select {
  width: 100%;
  max-width: 400px;
  padding: 12px;
  border: 2px solid rgba(112, 113, 77, 0.3);
  border-radius: 8px;
  font-size: 16px;
}

.exams-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.exam-card {
  border: 2px solid rgba(112, 113, 77, 0.22);
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
  background: rgba(207, 218, 197, 0.22);
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

.exam-course {
  color: var(--color-text-soft);
  font-size: 14px;
  margin-bottom: 16px;
}

.exam-actions {
  display: flex;
  gap: 8px;
}

.loading {
  background: rgba(255, 255, 255, 0.96);
  padding: 40px;
  text-align: center;
  border-radius: 16px;
  color: var(--color-text-soft);
  box-shadow: var(--shadow-soft);
}

.error-message {
  background: rgba(112, 113, 77, 0.16);
  color: var(--color-primary);
  padding: 16px;
  border-radius: 12px;
  text-align: center;
  border: 1px solid rgba(67, 96, 50, 0.18);
}
</style>

