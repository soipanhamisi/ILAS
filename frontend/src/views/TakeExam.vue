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

      <div class="questions-card">
        <h3>Exam Questions</h3>

        <p v-if="questionLoadError" class="question-error">
          {{ questionLoadError }}
        </p>

        <div v-else-if="examQuestions.length === 0" class="question-empty">
          Questions are not available for this exam yet.
        </div>

        <ol v-else class="question-list">
          <li
            v-for="(question, index) in examQuestions"
            :key="`question-${index}`"
            class="question-item"
          >
            {{ question }}
          </li>
        </ol>
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
          Answer each question in its own text box before submitting.
        </p>

        <form @submit.prevent="handleSubmit">
          <div
            v-for="(question, index) in examQuestions"
            :key="`answer-box-${index}`"
            class="form-group"
          >
            <label :for="`answer-${index}`">Question {{ index + 1 }}</label>
            <p class="question-caption">{{ question }}</p>
            <textarea
              :id="`answer-${index}`"
              v-model="questionAnswers[index]"
              class="answer-input"
              rows="5"
              maxlength="2000"
              placeholder="Write your answer for this question..."
              required
            ></textarea>
            <small class="help-text">
              {{ answerLength(index) }} / 2000 characters
            </small>
          </div>

          <div class="form-actions">
            <button type="submit" class="btn-primary" :disabled="submitting || !canSubmit">
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
const examQuestions = ref([])
const questionAnswers = ref([])
const loading = ref(false)
const submitting = ref(false)
const error = ref('')
const questionLoadError = ref('')
const success = ref(false)
const alreadySubmitted = ref(false)

const examId = computed(() => parseInt(route.params.examId))
const canSubmit = computed(() => {
  if (examQuestions.value.length === 0) {
    return false
  }

  if (questionAnswers.value.length !== examQuestions.value.length) {
    return false
  }

  return questionAnswers.value.every(answer => answer && answer.trim().length > 0)
})

const answerLength = (index) => {
  const value = questionAnswers.value[index] || ''
  return value.trim().length
}

const loadExamDetails = async () => {
  loading.value = true
  error.value = ''
  questionLoadError.value = ''

  try {
    const [examResponse, questionsResponse, submittedResponse] = await Promise.all([
      studentAPI.getExamDetails(
        examId.value,
        authStore.userId
      ),
      studentAPI.getExamQuestions(
        examId.value,
        authStore.userId
      ),
      studentAPI.hasSubmitted(
        examId.value,
        authStore.userId
      )
    ])

    // Load exam details
    if (examResponse.data.success) {
      exam.value = examResponse.data.data
    }

    // Load exam questions
    if (questionsResponse.data.success) {
      examQuestions.value = questionsResponse.data.data || []
      questionAnswers.value = examQuestions.value.map(() => '')
    }

    // Check if already submitted
    if (submittedResponse.data.success) {
      alreadySubmitted.value = submittedResponse.data.data
    }
  } catch (err) {
    const backendMessage = err.response?.data?.message || 'Failed to load exam'

    // Keep page usable if only question loading fails.
    if (err.config?.url?.includes('/questions')) {
      questionLoadError.value = backendMessage

      try {
        const [examResponse, submittedResponse] = await Promise.all([
          studentAPI.getExamDetails(examId.value, authStore.userId),
          studentAPI.hasSubmitted(examId.value, authStore.userId)
        ])

        if (examResponse.data.success) {
          exam.value = examResponse.data.data
        }

        if (submittedResponse.data.success) {
          alreadySubmitted.value = submittedResponse.data.data
        }
      } catch (fallbackErr) {
        error.value = fallbackErr.response?.data?.message || 'Failed to load exam'
      }
    } else {
      error.value = backendMessage
      console.error('Error loading exam:', err)
    }
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (examQuestions.value.length === 0) {
    error.value = 'Exam questions are unavailable, so submission is disabled'
    return
  }

  if (!canSubmit.value) {
    error.value = 'Please answer every question before submitting'
    return
  }

  submitting.value = true
  error.value = ''
  success.value = false

  try {
    const response = await studentAPI.submitExam(
      examId.value,
      authStore.userId,
      questionAnswers.value.map(answer => answer.trim())
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

.questions-card {
  background: var(--glass-bg-strong);
  border-radius: 16px;
  padding: 24px 28px;
  margin-bottom: 24px;
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(14px);
}

.questions-card h3 {
  color: var(--color-primary);
  margin-bottom: 14px;
}

.question-list {
  display: grid;
  gap: 12px;
  margin: 0;
  padding-left: 20px;
}

.question-item {
  color: var(--color-text);
  line-height: 1.55;
}

.question-empty,
.question-error {
  color: var(--color-text-soft);
  background: rgba(255, 255, 255, 0.42);
  border: 1px solid rgba(255, 255, 255, 0.45);
  border-radius: 10px;
  padding: 12px;
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

.question-caption {
  color: var(--color-text-soft);
  margin-bottom: 10px;
  line-height: 1.45;
}

.answer-input {
  width: 100%;
  padding: 14px;
  border: 2px solid rgba(255, 255, 255, 0.62);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.42);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  font-size: 15px;
  line-height: 1.6;
  color: var(--color-text);
  resize: vertical;
}

.answer-input:focus {
  outline: none;
  border-color: var(--color-accent);
  box-shadow: 0 0 0 3px rgba(182, 223, 217, 0.35);
}

.help-text {
  display: block;
  margin-top: 8px;
  color: var(--color-text-soft);
  font-size: 14px;
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

