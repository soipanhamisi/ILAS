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
          <label>CSV Template File</label>
          <div class="file-upload">
            <input
              type="file"
              @change="handleFileChange"
              accept=".csv"
              required
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

        <div class="csv-format-info">
          <h4>Expected CSV Format:</h4>
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
import { ref } from 'vue'
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

const selectedFile = ref(null)
const fileInput = ref(null)
const loading = ref(false)
const error = ref('')
const success = ref(false)

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

  loading.value = true
  error.value = ''
  success.value = false

  try {
    const response = await instructorAPI.createExam(
      authStore.userId,
      parseInt(formData.value.courseId),
      formData.value.examTitle,
      parseInt(formData.value.maxScore),
      selectedFile.value
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
.form-group select {
  width: 100%;
  padding: 12px;
  border: 2px solid rgba(112, 113, 77, 0.3);
  border-radius: 8px;
  font-size: 16px;
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

