<template>
  <div class="container">
    <div class="page-header">
      <h1 class="page-title">📝 Take Exam</h1>
      <router-link to="/student" class="btn-secondary">← Back</router-link>
    </div>

    <div v-if="loading" class="loading">Loading exam details...</div>

    <div v-else-if="exam" class="exam-container">
      <div class="exam-info-card">
        <h2>{{ exam.examTitle }}</h2>
        <div class="exam-meta">
          <div class="meta-item">
            <strong>Course:</strong> {{ exam.courseTitle }}
          </div>
          <div class="meta-item">
            <strong>Instructor:</strong> {{ exam.instructorName }}
          </div>
          <div class="meta-item">
            <strong>Maximum Score:</strong> {{ exam.maxScore }} points
          </div>
        </div>
      </div>

      <div v-if="alreadySubmitted" class="info-message">
        ℹ️ You have already submitted this exam.
        <router-link :to="`/student/results/${exam.examId}`">
          View your results
        </router-link>
      </div>

      <div v-else class="submission-card">
        <h3>Submit Your Exam</h3>
        <p class="instructions">
          Upload your completed exam CSV file. Make sure it includes your responses
          to all questions.
        </p>

        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label>Exam Response CSV File</label>
            <div class="file-upload">
              <input
                type="file"
                @change="handleFileChange"
                accept=".csv"
                required
                ref="fileInput"
                id="examFile"
              />
              <label for="examFile" class="file-label">
                <span v-if="!selectedFile">📄 Choose CSV file</span>
                <span v-else>✓ {{ selectedFile.name }}</span>
              </label>
            </div>
            <small class="help-text">
              Upload your completed exam with all responses filled in
            </small>
          </div>

          <div class="csv-format-info">
            <h4>Expected CSV Format:</h4>
            <pre>question,learnerResponses,maxGrade,grade
What is 2+2?,4,[10],
Define OOP?,Object-oriented programming is...,[20],</pre>
          </div>

          <div class="form-actions">
            <button type="submit" class="btn-primary" :disabled="submitting">
              {{ submitting ? 'Submitting...' : 'Submit Exam' }}
            </button>
          </div>
        </form>

        <div v-if="success" class="success-message">
          ✓ Exam submitted successfully! Redirecting...
        </div>

        <div v-if="error" class="error-message">
          {{ error }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { studentAPI } from '../services/api'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const exam = ref(null)
const selectedFile = ref(null)
const fileInput = ref(null)
const loading = ref(false)
const submitting = ref(false)
const error = ref('')
const success = ref(false)
const alreadySubmitted = ref(false)

const examId = computed(() => parseInt(route.params.examId))

const loadExamDetails = async () => {
  loading.value = true
  error.value = ''

  try {
    // Load exam details
    const examResponse = await studentAPI.getExamDetails(
      examId.value,
      authStore.userId
    )

    if (examResponse.data.success) {
      exam.value = examResponse.data.data
    }

    // Check if already submitted
    const submittedResponse = await studentAPI.hasSubmitted(
      examId.value,
      authStore.userId
    )

    if (submittedResponse.data.success) {
      alreadySubmitted.value = submittedResponse.data.data
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to load exam'
    console.error('Error loading exam:', err)
  } finally {
    loading.value = false
  }
}

const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    if (!file.name.endsWith('.csv')) {
      error.value = 'Please select a CSV file'
      event.target.value = ''
      return
    }
    selectedFile.value = file
    error.value = ''
  }
}

const handleSubmit = async () => {
  if (!selectedFile.value) {
    error.value = 'Please select a CSV file'
    return
  }

  submitting.value = true
  error.value = ''
  success.value = false

  try {
    const response = await studentAPI.submitExam(
      examId.value,
      authStore.userId,
      selectedFile.value
    )

    if (response.data.success) {
      success.value = true

      // Redirect after 2 seconds
      setTimeout(() => {
        router.push('/student')
      }, 2000)
    } else {
      error.value = response.data.message
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to submit exam'
    console.error('Error submitting exam:', err)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadExamDetails()
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

.exam-container {
  max-width: 800px;
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
  margin-bottom: 20px;
}

.exam-meta {
  display: grid;
  gap: 12px;
}

.meta-item {
  color: var(--color-text-soft);
  font-size: 16px;
}

.meta-item strong {
  color: var(--color-text);
  margin-right: 8px;
}

.info-message {
  background: rgba(255, 255, 255, 0.56);
  color: var(--color-primary);
  padding: 16px;
  border-radius: 12px;
  margin-bottom: 24px;
  text-align: center;
  border: 1px solid var(--glass-border);
}

.info-message a {
  color: var(--color-accent);
  font-weight: 700;
}

.submission-card {
  background: var(--glass-bg-strong);
  border-radius: 16px;
  padding: 32px;
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(14px);
}

.submission-card h3 {
  font-size: 24px;
  color: var(--color-primary);
  margin-bottom: 12px;
}

.instructions {
  color: var(--color-text-soft);
  margin-bottom: 24px;
  line-height: 1.6;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 8px;
}

.file-upload {
  position: relative;
}

.file-upload input[type="file"] {
  opacity: 0;
  position: absolute;
  z-index: -1;
}

.file-label {
  display: block;
  padding: 20px;
  border: 2px dashed rgba(255, 255, 255, 0.62);
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.42);
  font-size: 16px;
  color: var(--color-text);
}

.file-label:hover {
  border-color: var(--color-accent);
  background: rgba(182, 223, 217, 0.3);
}

.help-text {
  display: block;
  margin-top: 8px;
  color: var(--color-text-soft);
  font-size: 14px;
}

.csv-format-info {
  background: rgba(255, 255, 255, 0.48);
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 24px;
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(10px);
}

.csv-format-info h4 {
  margin-bottom: 12px;
  color: var(--color-primary);
}

.csv-format-info pre {
  background: rgba(246, 245, 230, 0.72);
  padding: 16px;
  border-radius: 8px;
  overflow-x: auto;
  font-size: 14px;
  line-height: 1.6;
  color: var(--color-text);
}

.form-actions button {
  width: 100%;
  padding: 16px;
  font-size: 18px;
}

.success-message {
  background: rgba(255, 255, 255, 0.56);
  color: var(--color-primary);
  padding: 16px;
  border-radius: 12px;
  text-align: center;
  margin-top: 20px;
  font-weight: 600;
  font-size: 16px;
  border: 1px solid var(--glass-border);
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

