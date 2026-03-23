<template>
  <div class="container">
    <div class="page-header">
      <h1 class="page-title">📝 Exam Submissions</h1>
      <router-link to="/instructor" class="btn-secondary">← Back</router-link>
    </div>

    <div v-if="loading" class="loading">Loading submissions...</div>

    <div v-else-if="submissions.length === 0" class="empty-state">
      <div class="empty-icon">📭</div>
      <h3>No submissions yet</h3>
      <p>Students haven't submitted any exams yet</p>
    </div>

    <div v-else class="submissions-container">
      <div class="filters">
        <button
          @click="filter = 'all'"
          :class="['filter-btn', { active: filter === 'all' }]"
        >
          All ({{ submissions.length }})
        </button>
        <button
          @click="filter = 'ungraded'"
          :class="['filter-btn', { active: filter === 'ungraded' }]"
        >
          Ungraded ({{ ungradedCount }})
        </button>
        <button
          @click="filter = 'graded'"
          :class="['filter-btn', { active: filter === 'graded' }]"
        >
          Graded ({{ gradedCount }})
        </button>
      </div>

      <div class="submissions-grid">
        <div
          v-for="submission in filteredSubmissions"
          :key="submission.submissionId"
          class="submission-card"
        >
          <div class="submission-header">
            <div>
              <h3>{{ submission.studentName }}</h3>
              <p class="submission-date">
                Submitted: {{ formatDate(submission.submittedAt) }}
              </p>
            </div>
            <span
              :class="['status-badge', submission.grade !== null ? 'graded' : 'pending']"
            >
              {{ submission.grade !== null ? 'Graded' : 'Pending' }}
            </span>
          </div>

          <div v-if="submission.grade !== null" class="grade-display">
            <div class="grade-value">
              {{ submission.grade }} / {{ submission.maxScore }}
            </div>
            <div class="feedback-preview">
              <strong>Per-question feedback:</strong>
              <pre>{{ submission.feedback }}</pre>
            </div>
          </div>

          <div v-if="submission.submissionText" class="answer-preview">
            <strong>Student responses:</strong>
            <div class="student-answers">
              <div
                v-for="(question, index) in examQuestionDetails"
                :key="`answer-${submission.submissionId}-${index}`"
                class="student-answer-item"
              >
                <p class="question-title">
                  Q{{ index + 1 }}: {{ question.questionText }}
                  <span class="question-max">(Max: {{ question.maxGrade }})</span>
                </p>
                <p>{{ getAnswerForQuestion(submission, index) }}</p>
              </div>
            </div>
          </div>

          <div v-if="!gradingMode[submission.submissionId]" class="card-actions">
            <button
              @click="startGrading(submission)"
              class="btn-primary"
            >
              {{ submission.grade !== null ? 'Update Grade' : 'Grade' }}
            </button>
          </div>

          <div v-else class="grading-form">
            <div class="form-group" v-for="(question, index) in examQuestionDetails" :key="`grade-q-${submission.submissionId}-${index}`">
              <label>Question {{ index + 1 }} Grade</label>
              <p class="question-title">
                {{ question.questionText }}
                <span class="question-max">(Max: {{ question.maxGrade }})</span>
              </p>
              <p class="student-answer-inline">Answer: {{ getAnswerForQuestion(submission, index) }}</p>
              <input
                v-model.number="gradeForm[submission.submissionId].questionGrades[index].grade"
                type="number"
                min="0"
                :max="getQuestionMaxGrade(index)"
                @input="onGradeInput(submission, index)"
                required
              />
              <small class="grade-limit-hint">
                Max for this question: {{ getQuestionMaxGrade(index) }}
              </small>

              <textarea
                v-model="gradeForm[submission.submissionId].questionGrades[index].feedback"
                rows="2"
                placeholder="Feedback for this question"
                required
              ></textarea>

              <textarea
                v-model="gradeForm[submission.submissionId].questionGrades[index].justification"
                rows="2"
                placeholder="Justification for this question"
                required
              ></textarea>
            </div>

            <div class="aggregate-grade">
              Total Grade: {{ getTotalGrade(submission) }} / {{ submission.maxScore }}
            </div>

            <div class="form-actions">
              <button
                @click="submitGrade(submission)"
                class="btn-success"
                :disabled="submitting"
              >
                {{ submitting ? 'Submitting...' : 'Submit Grade' }}
              </button>
              <button
                @click="cancelGrading(submission.submissionId)"
                class="btn-secondary"
              >
                Cancel
              </button>
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
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { instructorAPI } from '../services/api'

const route = useRoute()
const authStore = useAuthStore()

const submissions = ref([])
const loading = ref(false)
const error = ref('')
const filter = ref('all')
const gradingMode = ref({})
const gradeForm = ref({})
const submitting = ref(false)
const examQuestionDetails = ref([])

const examId = computed(() => parseInt(route.params.examId))

const filteredSubmissions = computed(() => {
  if (filter.value === 'ungraded') {
    return submissions.value.filter(s => s.grade === null)
  } else if (filter.value === 'graded') {
    return submissions.value.filter(s => s.grade !== null)
  }
  return submissions.value
})

const ungradedCount = computed(() =>
  submissions.value.filter(s => s.grade === null).length
)

const gradedCount = computed(() =>
  submissions.value.filter(s => s.grade !== null).length
)

const loadSubmissions = async () => {
  loading.value = true
  error.value = ''

  try {
    const response = await instructorAPI.getSubmissionsForExam(
      examId.value,
      authStore.userId
    )

    if (response.data.success) {
      submissions.value = response.data.data

      const questionResponse = await instructorAPI.getExamQuestionDetails(
        examId.value,
        authStore.userId
      )

      if (questionResponse.data.success) {
        examQuestionDetails.value = questionResponse.data.data || []
      }
    } else {
      error.value = response.data.message
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to load submissions'
    console.error('Error loading submissions:', err)
  } finally {
    loading.value = false
  }
}

const startGrading = (submission) => {
  if (examQuestionDetails.value.length === 0) {
    error.value = 'No exam questions found for grading'
    return
  }

  gradingMode.value[submission.submissionId] = true
  gradeForm.value[submission.submissionId] = {
    questionGrades: examQuestionDetails.value.map((_, index) => ({
      questionNumber: index + 1,
      grade: '',
      feedback: '',
      justification: ''
    }))
  }
}

const cancelGrading = (submissionId) => {
  gradingMode.value[submissionId] = false
}

const submitGrade = async (submission) => {
  const form = gradeForm.value[submission.submissionId]

  const invalidQuestion = form.questionGrades.find(q =>
    q.grade === '' || q.grade === null || !q.feedback?.trim() || !q.justification?.trim()
  )

  if (invalidQuestion) {
    error.value = 'Please provide grade, feedback, and justification for every question'
    return
  }

  const exceedsQuestionMax = form.questionGrades.find((q, index) => {
    const maxGrade = getQuestionMaxGrade(index)
    return Number(q.grade) > maxGrade
  })

  if (exceedsQuestionMax) {
    error.value = 'One or more question grades exceed the CSV maxGrade limit'
    return
  }

  if (getTotalGrade(submission) > submission.maxScore) {
    error.value = `Total grade cannot exceed ${submission.maxScore}`
    return
  }

  submitting.value = true
  error.value = ''

  try {
    const response = await instructorAPI.gradeSubmission(
      submission.submissionId,
      authStore.userId,
      form.questionGrades.map(questionGrade => ({
        questionNumber: questionGrade.questionNumber,
        grade: parseInt(questionGrade.grade),
        feedback: questionGrade.feedback.trim(),
        gradeJustification: questionGrade.justification.trim()
      }))
    )

    if (response.data.success) {
      gradingMode.value[submission.submissionId] = false
      await loadSubmissions() // Reload submissions
    } else {
      error.value = response.data.message
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to submit grade'
    console.error('Error submitting grade:', err)
  } finally {
    submitting.value = false
  }
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString()
}

const getAnswerForQuestion = (submission, index) => {
  const answers = parseSubmissionAnswers(submission.submissionText)
  return answers[index] || 'No answer submitted'
}

const parseSubmissionAnswers = (submissionText) => {
  if (!submissionText) {
    return []
  }

  const answers = []
  const chunks = submissionText.split(/\n\nQ\d+:\n/)

  chunks.forEach((chunk, idx) => {
    if (idx === 0 && chunk.startsWith('Q1:\n')) {
      answers.push(chunk.replace(/^Q1:\n/, '').trim())
      return
    }

    if (idx > 0) {
      answers.push(chunk.trim())
    }
  })

  return answers
}

const getTotalGrade = (submission) => {
  const form = gradeForm.value[submission.submissionId]
  if (!form) {
    return 0
  }

  return form.questionGrades.reduce((sum, questionGrade) => {
    const numeric = Number(questionGrade.grade)
    return sum + (Number.isNaN(numeric) ? 0 : numeric)
  }, 0)
}

const getQuestionMaxGrade = (index) => {
  const details = examQuestionDetails.value[index]
  if (!details || details.maxGrade === null || details.maxGrade === undefined) {
    return 0
  }
  return Math.max(0, Number(details.maxGrade) || 0)
}

const onGradeInput = (submission, index) => {
  const form = gradeForm.value[submission.submissionId]
  if (!form || !form.questionGrades[index]) {
    return
  }

  const rawValue = form.questionGrades[index].grade
  const numericValue = Number(rawValue)

  if (rawValue === '' || Number.isNaN(numericValue)) {
    return
  }

  const maxAllowed = getQuestionMaxGrade(index)
  if (numericValue < 0) {
    form.questionGrades[index].grade = 0
    return
  }

  if (numericValue > maxAllowed) {
    form.questionGrades[index].grade = maxAllowed
  }
}

onMounted(() => {
  loadSubmissions()
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

.loading,
.empty-state {
  background: var(--glass-bg-strong);
  padding: 60px 20px;
  text-align: center;
  border-radius: 16px;
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(14px);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-state h3 {
  color: var(--color-primary);
  margin-bottom: 8px;
}

.empty-state p {
  color: var(--color-text-soft);
}

.submissions-container {
  background: var(--glass-bg-strong);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(14px);
}

.filters {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.45);
}

.filter-btn {
  background: rgba(255, 255, 255, 0.55);
  color: var(--color-text);
  padding: 10px 20px;
  border-radius: 999px;
  font-weight: 500;
  border: 1px solid rgba(255, 255, 255, 0.45);
}

.filter-btn.active {
  background: linear-gradient(135deg, var(--color-surface) 0%, var(--color-accent) 100%);
  color: #27423a;
}

.submissions-grid {
  display: grid;
  gap: 20px;
}

.submission-card {
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.42);
  backdrop-filter: blur(10px);
}

.submission-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 16px;
}

.submission-header h3 {
  color: var(--color-text);
  font-size: 20px;
  margin-bottom: 4px;
}

.submission-date {
  color: var(--color-text-soft);
  font-size: 14px;
}

.status-badge {
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 600;
}

.status-badge.graded {
  background: rgba(182, 223, 217, 0.48);
  color: var(--color-primary);
}

.status-badge.pending {
  background: rgba(255, 255, 255, 0.62);
  color: var(--color-muted);
}

.grade-display {
  background: rgba(246, 245, 230, 0.62);
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  border: 1px solid rgba(255, 255, 255, 0.45);
}

.grade-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-primary);
  margin-bottom: 8px;
}

.feedback-preview {
  color: var(--color-text-soft);
  font-size: 14px;
}

.feedback-preview pre {
  white-space: pre-wrap;
  margin-top: 6px;
}

.answer-preview {
  background: rgba(246, 245, 230, 0.62);
  padding: 14px;
  border-radius: 8px;
  margin-bottom: 16px;
  border: 1px solid rgba(255, 255, 255, 0.45);
}

.answer-preview strong {
  color: var(--color-primary);
}

.answer-preview p {
  margin-top: 8px;
  white-space: pre-wrap;
  color: var(--color-text-soft);
}

.student-answers {
  display: grid;
  gap: 10px;
  margin-top: 10px;
}

.student-answer-item {
  background: rgba(255, 255, 255, 0.42);
  border-radius: 8px;
  padding: 10px;
}

.question-title {
  color: var(--color-primary);
  font-weight: 600;
  margin-bottom: 6px;
}

.question-max {
  color: var(--color-text-soft);
  font-weight: 500;
}

.student-answer-inline {
  color: var(--color-text-soft);
  margin-bottom: 8px;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.grading-form {
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.45);
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 8px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 2px solid rgba(112, 113, 77, 0.3);
  border-radius: 8px;
  font-size: 14px;
}

.grade-limit-hint {
  display: block;
  margin-top: 6px;
  color: var(--color-text-soft);
  font-size: 12px;
}

.aggregate-grade {
  background: rgba(246, 245, 230, 0.62);
  border: 1px solid rgba(255, 255, 255, 0.45);
  border-radius: 8px;
  padding: 12px;
  color: var(--color-primary);
  font-weight: 700;
  margin-bottom: 14px;
}

.form-actions {
  display: flex;
  gap: 8px;
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

