<template>
  <div class="container">
    <div class="page-header">
      <h1 class="page-title">➕ Create New Exam</h1>
      <router-link to="/instructor" class="btn-secondary">← Back</router-link>
    </div>

    <div class="card form-card">
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label>Course</label>
          <select v-model="formData.courseId" required>
            <option value="">Select course</option>
            <option value="101">Introduction to Java (101)</option>
            <option value="102">Data Structures (102)</option>
            <option value="103">Web Development (103)</option>
          </select>
        </div>

        <div class="form-group">
          <label>Exam Title</label>
          <input
            v-model="formData.examTitle"
            type="text"
            placeholder="e.g., Midterm Exam"
            required
          />
        </div>

        <div class="form-group">
          <label>Maximum Score</label>
          <input
            v-model="formData.maxScore"
            type="number"
            placeholder="e.g., 100"
            required
            min="1"
          />
        </div>

        <div class="form-group">
          <label>Question Input Method</label>
          <div class="creation-mode-toggle">
            <label>
              <input v-model="creationMode" type="radio" value="upload" />
              Upload CSV
            </label>
            <label>
              <input v-model="creationMode" type="radio" value="manual" />
              Build Questions Manually
            </label>
          </div>
        </div>

        <div v-if="creationMode === 'upload'" class="form-group">
          <label>CSV Template File</label>
          <div class="file-upload">
            <input
              type="file"
              @change="handleFileChange"
              accept=".csv"
              :required="creationMode === 'upload'"
              ref="fileInput"
              id="csvFile"
            />
            <label for="csvFile" class="file-label">
              <span v-if="!selectedFile">📄 Choose CSV file</span>
              <span v-else>✓ {{ selectedFile.name }}</span>
            </label>
          </div>
          <small class="help-text">
            Upload exam template with questions in CSV format
          </small>
        </div>

        <div v-else class="manual-builder">
          <div
            v-for="(question, index) in manualQuestions"
            :key="index"
            class="manual-question-card"
          >
            <div class="manual-question-header">
              <h4>Question {{ index + 1 }}</h4>
              <button
                v-if="manualQuestions.length > 1"
                type="button"
                class="btn-danger"
                @click="removeQuestion(index)"
              >
                Remove
              </button>
            </div>
            <div class="form-group">
              <label>Question Text</label>
              <textarea
                v-model="question.text"
                rows="4"
                placeholder="Type the question prompt here"
              />
            </div>
            <div class="form-group">
              <label>Question Max Grade</label>
              <input
                v-model="question.maxGrade"
                type="number"
                min="1"
                placeholder="e.g., 10"
              />
            </div>
          </div>
          <button type="button" class="btn-secondary add-question-btn" @click="addQuestion">
            + Add Another Question
          </button>
          <small class="help-text">
            Manual entries are converted to CSV and stored the same way as uploaded templates.
          </small>
        </div>

        <div v-if="creationMode === 'manual'" class="csv-preview-info">
          <h4>Preview Generated CSV:</h4>
          <pre v-if="!manualPreviewError">{{ manualCsvPreview }}</pre>
          <div v-else class="preview-error-message">{{ manualPreviewError }}</div>
        </div>

        <div class="csv-format-info">
          <h4>CSV Format Used for Storage:</h4>
          <pre>question,learnerResponses,maxGrade,grade
What is 2+2?,,[10],
Define OOP?,,[20],</pre>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn-primary" :disabled="loading">
            {{ loading ? 'Creating...' : 'Create Exam' }}
          </button>
        </div>
      </form>

      <div v-if="success" class="success-message">
        ✓ Exam created successfully!
      </div>

      <div v-if="error" class="error-message">
        {{ error }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { instructorAPI } from '../services/api'

const router = useRouter()
const authStore = useAuthStore()

const formData = ref({
  courseId: '',
  examTitle: '',
  maxScore: ''
})

const creationMode = ref('upload')
const selectedFile = ref(null)
const manualQuestions = ref([{ text: '', maxGrade: '' }])
const fileInput = ref(null)
const loading = ref(false)
const error = ref('')
const success = ref(false)

const addQuestion = () => {
  manualQuestions.value.push({ text: '', maxGrade: '' })
}

const removeQuestion = (index) => {
  manualQuestions.value.splice(index, 1)
}

const escapeCsvValue = (value) => {
  const normalized = String(value ?? '').replace(/\r?\n/g, ' ')
  const escaped = normalized.replace(/"/g, '""')
  return `"${escaped}"`
}

const getCleanManualQuestions = () => {
  return manualQuestions.value
    .map((question) => ({
      text: question.text.trim(),
      maxGrade: Number(question.maxGrade)
    }))
    .filter((question) => question.text)
}

const buildManualCsvContent = () => {
  const cleanedQuestions = getCleanManualQuestions()

  if (!cleanedQuestions.length) {
    throw new Error('Please add at least one question with text')
  }

  if (cleanedQuestions.some((question) => !Number.isInteger(question.maxGrade) || question.maxGrade <= 0)) {
    throw new Error('Each manual question must have a valid max grade greater than 0')
  }

  const manualTotal = cleanedQuestions.reduce((sum, question) => sum + question.maxGrade, 0)
  const examMaxScore = parseInt(formData.value.maxScore, 10)
  if (manualTotal !== examMaxScore) {
    throw new Error(`Sum of manual question grades (${manualTotal}) must equal Maximum Score (${examMaxScore})`)
  }

  const rows = cleanedQuestions.map((question) => `${escapeCsvValue(question.text)},,${question.maxGrade},`)
  return ['question,learnerResponses,maxGrade,grade', ...rows].join('\n')
}

const buildManualCsvFile = () => {
  const csvContent = buildManualCsvContent()
  const safeExamTitle = (formData.value.examTitle || 'exam').trim() || 'exam'
  const fileName = `${safeExamTitle.replace(/\s+/g, '_')}_manual.csv`

  return new File([csvContent], fileName, {
    type: 'text/csv'
  })
}

const manualPreviewError = computed(() => {
  if (creationMode.value !== 'manual') {
    return ''
  }

  try {
    buildManualCsvContent()
    return ''
  } catch (previewError) {
    return previewError.message
  }
})

const manualCsvPreview = computed(() => {
  if (creationMode.value !== 'manual') {
    return ''
  }

  try {
    return buildManualCsvContent()
  } catch {
    return 'question,learnerResponses,maxGrade,grade'
  }
})

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
  let csvFile

  if (creationMode.value === 'upload') {
    if (!selectedFile.value) {
      error.value = 'Please select a CSV file'
      return
    }
    csvFile = selectedFile.value
  } else {
    try {
      csvFile = buildManualCsvFile()
    } catch (buildError) {
      error.value = buildError.message
      return
    }
  }

  loading.value = true
  error.value = ''
  success.value = false

  try {
    const response = await instructorAPI.createExam(
      authStore.userId,
      parseInt(formData.value.courseId),
      formData.value.examTitle,
      parseInt(formData.value.maxScore),
      csvFile
    )

    if (response.data.success) {
      success.value = true

      // Reset form
      formData.value = {
        courseId: '',
        examTitle: '',
        maxScore: ''
      }
      selectedFile.value = null
      creationMode.value = 'upload'
      manualQuestions.value = [{ text: '', maxGrade: '' }]
      if (fileInput.value) {
        fileInput.value.value = ''
      }

      // Redirect after 2 seconds
      setTimeout(() => {
        router.push('/instructor')
      }, 2000)
    } else {
      error.value = response.data.message
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to create exam'
    console.error('Error creating exam:', err)
  } finally {
    loading.value = false
  }
}
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

.form-card {
  max-width: 700px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.97);
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

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid rgba(112, 113, 77, 0.3);
  border-radius: 8px;
  font-size: 16px;
}

.form-group textarea {
  resize: vertical;
  min-height: 96px;
}

.creation-mode-toggle {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.creation-mode-toggle label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.manual-builder {
  margin-bottom: 24px;
}

.manual-question-card {
  background: rgba(207, 218, 197, 0.38);
  border: 1px solid rgba(67, 96, 50, 0.16);
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.manual-question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.manual-question-header h4 {
  margin: 0;
  color: var(--color-primary);
}

.btn-danger {
  border: 1px solid rgba(112, 113, 77, 0.4);
  background: rgba(112, 113, 77, 0.12);
  color: var(--color-primary);
  border-radius: 8px;
  padding: 8px 12px;
  cursor: pointer;
}

.add-question-btn {
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
  padding: 16px;
  border: 2px dashed rgba(112, 113, 77, 0.35);
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(207, 218, 197, 0.42);
  color: var(--color-text);
}

.file-label:hover {
  border-color: var(--color-accent);
  background: rgba(183, 203, 145, 0.45);
}

.help-text {
  display: block;
  margin-top: 8px;
  color: var(--color-text-soft);
  font-size: 14px;
}

.csv-format-info {
  background: rgba(207, 218, 197, 0.52);
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 24px;
  border: 1px solid rgba(67, 96, 50, 0.08);
}

.csv-format-info h4 {
  margin-bottom: 8px;
  color: var(--color-primary);
}

.csv-format-info pre {
  background: rgba(255, 255, 255, 0.88);
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 14px;
  color: var(--color-text);
}

.csv-preview-info {
  background: rgba(207, 218, 197, 0.52);
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 24px;
  border: 1px solid rgba(67, 96, 50, 0.08);
}

.csv-preview-info h4 {
  margin-bottom: 8px;
  color: var(--color-primary);
}

.csv-preview-info pre {
  background: rgba(255, 255, 255, 0.88);
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 14px;
  color: var(--color-text);
  white-space: pre-wrap;
}

.preview-error-message {
  background: rgba(112, 113, 77, 0.16);
  color: var(--color-primary);
  padding: 12px;
  border-radius: 6px;
  border: 1px solid rgba(67, 96, 50, 0.18);
}

.form-actions {
  display: flex;
  gap: 12px;
}

.form-actions button {
  flex: 1;
  padding: 14px;
  font-size: 16px;
}

.success-message {
  background: rgba(183, 203, 145, 0.48);
  color: var(--color-primary);
  padding: 16px;
  border-radius: 8px;
  text-align: center;
  margin-top: 20px;
  font-weight: 600;
  border: 1px solid rgba(67, 96, 50, 0.14);
}

.error-message {
  background: rgba(112, 113, 77, 0.16);
  color: var(--color-primary);
  padding: 16px;
  border-radius: 8px;
  text-align: center;
  margin-top: 20px;
  border: 1px solid rgba(67, 96, 50, 0.18);
}
</style>

