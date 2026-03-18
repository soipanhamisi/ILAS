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
              <strong>Feedback:</strong> {{ submission.feedback }}
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
            <div class="form-group">
              <label>Grade (out of {{ submission.maxScore }})</label>
              <input
                v-model="gradeForm[submission.submissionId].grade"
                type="number"
                :min="0"
                :max="submission.maxScore"
                required
              />
            </div>

            <div class="form-group">
              <label>Feedback</label>
              <textarea
                v-model="gradeForm[submission.submissionId].feedback"
                rows="3"
                placeholder="Enter feedback for student"
                required
              ></textarea>
            </div>

            <div class="form-group">
              <label>Grade Justification</label>
              <textarea
                v-model="gradeForm[submission.submissionId].justification"
                rows="3"
                placeholder="Detailed explanation of grade"
                required
              ></textarea>
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
  gradingMode.value[submission.submissionId] = true
  gradeForm.value[submission.submissionId] = {
    grade: submission.grade || '',
    feedback: submission.feedback || '',
    justification: submission.gradeJustification || ''
  }
}

const cancelGrading = (submissionId) => {
  gradingMode.value[submissionId] = false
}

const submitGrade = async (submission) => {
  const form = gradeForm.value[submission.submissionId]

  if (!form.grade || !form.feedback || !form.justification) {
    error.value = 'Please fill in all fields'
    return
  }

  submitting.value = true
  error.value = ''

  try {
    const response = await instructorAPI.gradeSubmission(
      submission.submissionId,
      authStore.userId,
      parseInt(form.grade),
      form.feedback,
      form.justification
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
  color: var(--color-white);
}

.loading,
.empty-state {
  background: rgba(255, 255, 255, 0.96);
  padding: 60px 20px;
  text-align: center;
  border-radius: 16px;
  box-shadow: var(--shadow-soft);
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
  background: rgba(255, 255, 255, 0.96);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--shadow-soft);
}

.filters {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid rgba(112, 113, 77, 0.22);
}

.filter-btn {
  background: rgba(207, 218, 197, 0.75);
  color: var(--color-text);
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
}

.filter-btn.active {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
  color: var(--color-white);
}

.submissions-grid {
  display: grid;
  gap: 20px;
}

.submission-card {
  border: 2px solid rgba(112, 113, 77, 0.22);
  border-radius: 12px;
  padding: 20px;
  background: rgba(207, 218, 197, 0.18);
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
  background: rgba(183, 203, 145, 0.65);
  color: var(--color-primary);
}

.status-badge.pending {
  background: rgba(207, 218, 197, 0.85);
  color: var(--color-muted);
}

.grade-display {
  background: rgba(255, 255, 255, 0.72);
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  border: 1px solid rgba(67, 96, 50, 0.08);
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

.card-actions {
  display: flex;
  gap: 8px;
}

.grading-form {
  padding-top: 16px;
  border-top: 2px solid rgba(112, 113, 77, 0.2);
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

.form-actions {
  display: flex;
  gap: 8px;
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

