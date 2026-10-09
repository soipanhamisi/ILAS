<template>
  <div class="container create-exam-page">
    <div v-if="showRubricPanel" class="rubric-section">
      <RubricEditorPanel
        :examQuestionDetails="newExamQuestions"
        :initialRubrics="[]"
        :title="`Define Rubrics for ${formData.examTitle}`"
        :subtitle="`Optionally define grading rubrics now for quick reference during auto-grading. You can also skip this and define rubrics later in the Exam Submissions page.`"
        :saveButtonText="`Save Rubrics & Continue`"
        :skipButtonText="`Skip & Go to Dashboard`"
        :showSkipButton="true"
        :showProgressIndicator="true"
        :saving="rubricSaving"
        @save="saveRubricsAndContinue"
        @skip="skipRubricsAndContinue"
      />
    </div>

    <template v-else>
      <header class="create-exam-heading">
        <div>
          <p class="eyebrow">ASSESSMENT BUILDER</p>
          <h1>Create new exam</h1>
          <p class="heading-copy">Set up the assessment and choose how you want to add questions.</p>
        </div>
        <span class="draft-status"><span class="status-dot" /> Setup in progress</span>
      </header>

      <form class="setup-grid" @submit.prevent="handleSubmit">
        <div class="setup-main">
          <section class="setup-card">
            <div class="section-heading">
              <span class="step-number">1</span>
              <div><h2>Exam details</h2><p>The basics students will see.</p></div>
            </div>
            <div class="details-fields">
              <div class="form-group">
                <label for="courseId">Course</label>
                <select id="courseId" v-model="formData.courseId" required>
                  <option value="">Select a course</option>
                  <option value="101">Introduction to Java (101)</option>
                  <option value="102">Data Structures (102)</option>
                  <option value="103">Web Development (103)</option>
                </select>
              </div>
              <div class="form-group">
                <label for="examTitle">Exam title</label>
                <input id="examTitle" v-model="formData.examTitle" type="text" placeholder="e.g. Cell Biology Midterm" required />
              </div>
              <div class="form-group">
                <label for="maxScore">Maximum score</label>
                <input id="maxScore" v-model="formData.maxScore" type="number" placeholder="e.g. 100" required min="1" />
              </div>
              <div class="form-group">
                <label for="dueDate">Due date</label>
                <input id="dueDate" type="date" disabled aria-describedby="dueDateNote" />
                <small id="dueDateNote" class="field-note">Scheduling will be available soon.</small>
              </div>
            </div>
          </section>

          <section class="setup-card">
            <div class="section-heading">
              <span class="step-number">2</span>
              <div><h2>Add questions</h2><p>Upload in bulk or build each question manually.</p></div>
            </div>
            <div class="creation-mode-toggle" role="radiogroup" aria-label="Question input method">
              <label class="mode-option" :class="{ active: creationMode === 'upload' }">
                <input v-model="creationMode" type="radio" value="upload" />
                <span class="mode-icon" aria-hidden="true">⇧</span>
                <span class="mode-copy"><strong>Upload CSV</strong><small>Best for existing question sets</small></span>
              </label>
              <label class="mode-option" :class="{ active: creationMode === 'manual' }">
                <input v-model="creationMode" type="radio" value="manual" />
                <span class="mode-icon" aria-hidden="true">＋</span>
                <span class="mode-copy"><strong>Build manually</strong><small>Create questions one by one</small></span>
              </label>
            </div>

            <div v-if="creationMode === 'upload'" class="upload-content">
              <label class="field-label" for="csvFile">CSV template file</label>
              <label class="upload-dropzone" @dragover.prevent @drop.prevent="handleFileDrop">
                <input id="csvFile" ref="fileInput" class="file-input" type="file" accept=".csv,text/csv" @change="handleFileChange" />
                <span class="upload-icon" aria-hidden="true">↑</span>
                <strong>{{ selectedFile ? selectedFile.name : 'Choose CSV file' }}</strong>
                <span class="upload-help">or drag and drop your file here</span>
              </label>
              <div class="csv-template">
                <div class="template-heading">
                  <div><strong>Expected CSV columns</strong><span>Use the platform’s question and score format.</span></div>
                  <button type="button" class="template-link" @click="downloadCsvTemplate">Download template</button>
                </div>
                <pre>question,learnerResponses,maxGrade,grade
"What is 2+2?",,10,</pre>
              </div>
            </div>

            <div v-else class="manual-builder">
              <div v-for="(question, index) in manualQuestions" :key="index" class="manual-question-card">
                <div class="manual-question-header">
                  <h3>Question {{ index + 1 }}</h3>
                  <button v-if="manualQuestions.length > 1" type="button" class="btn-danger remove-question-btn" @click="removeQuestion(index)">Remove</button>
                </div>
                <div class="form-group">
                  <label :for="`question-${index}`">Question text</label>
                  <textarea :id="`question-${index}`" v-model="question.text" rows="4" placeholder="Type the question prompt here" />
                </div>
                <div class="form-group">
                  <label :for="`key-points-${index}`">Rubric: key points that should be covered</label>
                  <textarea :id="`key-points-${index}`" v-model="question.rubricKeyPoints" rows="3" placeholder="List the important concepts or points expected in a strong answer" />
                </div>
                <div class="form-group">
                  <label :for="`common-mistakes-${index}`">Rubric: common mistakes to watch for</label>
                  <textarea :id="`common-mistakes-${index}`" v-model="question.rubricCommonMistakes" rows="3" placeholder="Describe typical mistakes, misconceptions, or missing elements" />
                </div>
                <div class="form-group">
                  <label :for="`scoring-criteria-${index}`">Rubric: scoring criteria</label>
                  <textarea :id="`scoring-criteria-${index}`" v-model="question.rubricScoringCriteria" rows="3" placeholder="Define how points should be awarded and deducted" />
                </div>
                <div class="form-group question-score-field">
                  <label :for="`max-grade-${index}`">Question max grade</label>
                  <input :id="`max-grade-${index}`" v-model="question.maxGrade" type="number" min="1" placeholder="e.g. 10" />
                </div>
              </div>
              <button type="button" class="add-question-btn" @click="addQuestion">＋ Add another question</button>
              <small class="help-text">Manual entries are converted to CSV, and rubric details entered here are saved for auto-grading.</small>
              <div class="csv-preview-info">
                <h3>Preview generated CSV</h3>
                <pre v-if="!manualPreviewError">{{ manualCsvPreview }}</pre>
                <div v-else class="preview-error-message">{{ manualPreviewError }}</div>
              </div>
            </div>
          </section>
          <div v-if="success" class="success-message" role="status">✓ Exam created successfully!</div>
          <div v-if="error" class="error-message" role="alert">{{ error }}</div>
        </div>

        <aside class="guidance-card">
          <p class="eyebrow">WHAT HAPPENS NEXT</p>
          <h2>Review your rubric</h2>
          <p class="guidance-copy">Your questions will be imported first. You can then review grading criteria before working with student responses.</p>
          <ol class="workflow-steps">
            <li><span>1</span><strong>Import questions</strong></li>
            <li><span>2</span><strong>Review marking criteria</strong></li>
            <li><span>3</span><strong>Add student responses</strong></li>
          </ol>
          <div class="guidance-actions">
            <button type="submit" class="btn-primary" :disabled="loading">{{ loading ? 'Creating…' : 'Create exam' }} <span aria-hidden="true">→</span></button>
            <router-link to="/instructor" class="cancel-link">Cancel</router-link>
          </div>
          <p class="privacy-note"><span aria-hidden="true">◈</span> Your exam stays private until you publish it.</p>
        </aside>
      </form>
    </template>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { instructorAPI } from '../services/api'
import RubricEditorPanel from '../components/RubricEditorPanel.vue'

const router = useRouter()
const authStore = useAuthStore()

const formData = ref({ courseId: '', examTitle: '', maxScore: '' })
const creationMode = ref('upload')
const selectedFile = ref(null)
const createEmptyManualQuestion = () => ({
  text: '',
  maxGrade: '',
  rubricKeyPoints: '',
  rubricCommonMistakes: '',
  rubricScoringCriteria: ''
})
const manualQuestions = ref([createEmptyManualQuestion()])
const fileInput = ref(null)
const loading = ref(false)
const error = ref('')
const success = ref(false)
const showRubricPanel = ref(false)
const rubricSaving = ref(false)
const newExamId = ref(null)
const newExamQuestions = ref([])

const addQuestion = () => manualQuestions.value.push(createEmptyManualQuestion())
const removeQuestion = (index) => manualQuestions.value.splice(index, 1)

const escapeCsvValue = (value) => {
  const normalized = String(value ?? '').replace(/\r?\n/g, ' ')
  return `"${normalized.replace(/"/g, '""')}"`
}

const getCleanManualQuestions = () => manualQuestions.value
  .map((question) => ({
    text: question.text.trim(),
    maxGrade: Number(question.maxGrade),
    rubricKeyPoints: (question.rubricKeyPoints || '').trim(),
    rubricCommonMistakes: (question.rubricCommonMistakes || '').trim(),
    rubricScoringCriteria: (question.rubricScoringCriteria || '').trim()
  }))
  .filter((question) => question.text)

const buildManualRubricText = (question) => {
  const parts = []
  if (question.rubricKeyPoints) parts.push(`Key points that should be covered:\n${question.rubricKeyPoints}`)
  if (question.rubricCommonMistakes) parts.push(`Common mistakes to watch for:\n${question.rubricCommonMistakes}`)
  if (question.rubricScoringCriteria) parts.push(`Scoring criteria:\n${question.rubricScoringCriteria}`)
  return parts.join('\n\n').trim()
}

const buildManualRubricsPayload = () => getCleanManualQuestions().map((question, index) => ({
  questionNumber: index + 1,
  maxScore: question.maxGrade,
  rubricText: buildManualRubricText(question)
}))

const buildManualCsvContent = () => {
  const cleanedQuestions = getCleanManualQuestions()
  if (!cleanedQuestions.length) throw new Error('Please add at least one question with text')
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
  const fileName = `${(formData.value.examTitle || 'exam').trim().replace(/\s+/g, '_')}_manual.csv`
  return new File([buildManualCsvContent()], fileName, { type: 'text/csv' })
}

const manualPreviewError = computed(() => {
  if (creationMode.value !== 'manual') return ''
  try {
    buildManualCsvContent()
    return ''
  } catch (previewError) {
    return previewError.message
  }
})

const manualCsvPreview = computed(() => {
  if (creationMode.value !== 'manual') return ''
  try {
    return buildManualCsvContent()
  } catch {
    return 'question,learnerResponses,maxGrade,grade'
  }
})

const saveRubricsAndContinue = async (rubricForm) => {
  rubricSaving.value = true
  error.value = ''
  try {
    const rubrics = rubricForm.map((rubric, index) => ({
      questionNumber: index + 1,
      maxScore: rubric.maxScore,
      rubricText: rubric.rubricText
    }))
    const response = await instructorAPI.saveExamRubrics(newExamId.value, authStore.userId, rubrics)
    if (response.data.success) {
      setTimeout(() => router.push('/instructor'), 1000)
    } else {
      error.value = response.data.message || 'Failed to save rubrics'
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to save rubrics'
    console.error('Error saving rubrics:', err)
  } finally {
    rubricSaving.value = false
  }
}

const skipRubricsAndContinue = () => router.push('/instructor')

const selectCsvFile = (file) => {
  if (!file) return
  if (!file.name.toLowerCase().endsWith('.csv')) {
    selectedFile.value = null
    error.value = 'Please select a CSV file'
    return
  }
  selectedFile.value = file
  error.value = ''
}

const handleFileChange = (event) => {
  const file = event.target.files[0]
  selectCsvFile(file)
  if (file && !file.name.toLowerCase().endsWith('.csv')) event.target.value = ''
}

const handleFileDrop = (event) => selectCsvFile(event.dataTransfer?.files?.[0])

const downloadCsvTemplate = () => {
  const content = 'question,learnerResponses,maxGrade,grade\n"What is 2+2?",,10,\n'
  const url = URL.createObjectURL(new Blob([content], { type: 'text/csv;charset=utf-8' }))
  const link = document.createElement('a')
  link.href = url
  link.download = 'exam-questions-template.csv'
  document.body.appendChild(link)
  link.click()
  link.remove()
  URL.revokeObjectURL(url)
}

const handleSubmit = async () => {
  let csvFile
  let manualRubrics = []
  const isManualMode = creationMode.value === 'manual'

  if (!isManualMode) {
    if (!selectedFile.value) {
      error.value = 'Please select a CSV file'
      return
    }
    csvFile = selectedFile.value
  } else {
    try {
      csvFile = buildManualCsvFile()
      manualRubrics = buildManualRubricsPayload()
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

    if (!response.data.success) {
      error.value = response.data.message
      return
    }

    success.value = true
    const examData = response.data.data
    newExamId.value = examData.examId

    if (isManualMode) {
      const hasManualRubrics = manualRubrics.some((rubric) => rubric.rubricText)
      if (hasManualRubrics) {
        try {
          await instructorAPI.saveExamRubrics(examData.examId, authStore.userId, manualRubrics)
        } catch (rubricErr) {
          console.warn('Exam created but manual rubrics could not be saved:', rubricErr)
          error.value = 'Exam created, but rubric definitions could not be saved. You can update rubrics in the Exam Submissions page.'
        }
      }
    } else {
      try {
        let retries = 0
        while (retries < 3) {
          try {
            const detailsResponse = await instructorAPI.getExamQuestionDetails(examData.examId, authStore.userId)
            if (detailsResponse.data.success && detailsResponse.data.data?.length) {
              newExamQuestions.value = detailsResponse.data.data
              break
            }
          } catch {
            // Continue retrying, then show the warning below if details remain unavailable.
          }
          retries++
          if (retries < 3) await new Promise((resolve) => setTimeout(resolve, 500))
        }
        if (!newExamQuestions.value.length) {
          error.value = 'Warning: Could not load question details. The rubric panel may not display properly.'
        }
      } catch (detailErr) {
        console.error('Could not fetch exam details for rubric panel:', detailErr)
        error.value = 'Warning: Could not load question details. The rubric panel may not display properly. Please try again or define rubrics later in the Exam Submissions page.'
      }
    }

    formData.value = { courseId: '', examTitle: '', maxScore: '' }
    selectedFile.value = null
    creationMode.value = 'upload'
    manualQuestions.value = [createEmptyManualQuestion()]
    if (fileInput.value) fileInput.value.value = ''
    if (isManualMode) {
      router.push('/instructor')
    } else {
      showRubricPanel.value = true
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
.create-exam-page {
  width: min(100%, 1160px);
  margin: 0 auto;
  padding: 22px 8px 48px;
  color: #3b2119;
}

.create-exam-heading {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 24px;
  margin: 0 auto 28px;
}

.eyebrow {
  margin-bottom: 8px;
  color: #846e5d;
  font-size: 10px;
  font-weight: 800;
  letter-spacing: 0.15em;
}

.create-exam-heading h1 {
  color: #3b1713;
  font-size: clamp(30px, 3.1vw, 40px);
  font-weight: 800;
  letter-spacing: -0.045em;
  line-height: 1.15;
}

.heading-copy {
  margin-top: 8px;
  color: #755e55;
  font-size: 14px;
}

.draft-status {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border: 1px solid #e0d2bb;
  border-radius: 999px;
  background: rgba(255, 254, 251, 0.72);
  color: #655044;
  font-size: 11px;
  font-weight: 700;
  white-space: nowrap;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #b85b0a;
}

.setup-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 280px;
  align-items: start;
  gap: 22px;
}

.setup-main {
  display: grid;
  gap: 18px;
  min-width: 0;
}

.setup-card,
.guidance-card {
  border: 1px solid rgba(116, 83, 64, 0.17);
  border-radius: 18px;
  background: #fffefb;
  box-shadow: 0 10px 28px rgba(63, 28, 18, 0.055);
}

.setup-card {
  padding: 24px;
}

.section-heading {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  margin-bottom: 22px;
}

.step-number {
  display: grid;
  width: 30px;
  height: 30px;
  flex: 0 0 30px;
  place-items: center;
  border: 1px solid #e4d3b4;
  border-radius: 50%;
  background: #f8f0e1;
  color: #885023;
  font-size: 13px;
  font-weight: 800;
}

.section-heading h2,
.guidance-card h2 {
  color: #3b1713;
  font-size: 18px;
  font-weight: 800;
  line-height: 1.35;
}

.section-heading p {
  margin-top: 3px;
  color: #806d5e;
  font-size: 12px;
}

.details-fields {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px 16px;
}

.form-group {
  min-width: 0;
  margin: 0;
}

.form-group label,
.field-label {
  display: block;
  margin-bottom: 7px;
  color: #4d352a;
  font-size: 12px;
  font-weight: 700;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  min-height: 44px;
  padding: 10px 12px;
  border: 1px solid #e0d6c6;
  border-radius: 10px;
  background: #fffdfa;
  color: #3b2119;
  font: inherit;
  font-size: 13px;
  box-shadow: none;
}

.form-group textarea {
  min-height: 92px;
  resize: vertical;
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: #a79584;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #bd6a26;
  box-shadow: 0 0 0 3px rgba(184, 91, 10, 0.12);
}

.form-group input:disabled {
  background: #f6f1e8;
  color: #938374;
  cursor: not-allowed;
}

.field-note {
  display: block;
  margin-top: 5px;
  color: #968574;
  font-size: 10px;
}

.creation-mode-toggle {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 22px;
}

.mode-option {
  position: relative;
  display: flex;
  min-height: 70px;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border: 1px solid #e6ddcf;
  border-radius: 12px;
  background: #fffefa;
  cursor: pointer;
  transition: border-color 0.2s ease, background 0.2s ease, box-shadow 0.2s ease;
}

.mode-option.active {
  border-color: #8a4b27;
  background: #fffaf1;
  box-shadow: 0 0 0 2px rgba(138, 75, 39, 0.08);
}

.mode-option:focus-within {
  outline: 3px solid rgba(184, 91, 10, 0.2);
  outline-offset: 2px;
}

.mode-option input {
  position: absolute;
  width: 1px;
  height: 1px;
  opacity: 0;
}

.mode-icon {
  display: grid;
  width: 34px;
  height: 34px;
  flex: 0 0 34px;
  place-items: center;
  border-radius: 10px;
  background: #f7f0e1;
  color: #774427;
  font-size: 20px;
  font-weight: 700;
}

.mode-copy {
  display: grid;
  gap: 3px;
}

.mode-copy strong {
  color: #39231a;
  font-size: 12px;
}

.mode-copy small {
  color: #877667;
  font-size: 10px;
}

.upload-content,
.manual-builder {
  display: grid;
  gap: 12px;
}

.upload-dropzone {
  display: flex;
  min-height: 156px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 5px;
  padding: 20px;
  border: 1px dashed #c9b69a;
  border-radius: 12px;
  background: #fdfaf4;
  color: #533729;
  cursor: pointer;
  text-align: center;
  transition: border-color 0.2s ease, background 0.2s ease;
}

.upload-dropzone:hover,
.upload-dropzone:focus-within {
  border-color: #aa5c21;
  background: #fff8ea;
}

.file-input {
  position: absolute;
  width: 1px;
  height: 1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  clip-path: inset(50%);
}

.upload-icon {
  display: grid;
  width: 34px;
  height: 34px;
  margin-bottom: 2px;
  place-items: center;
  border: 1px solid #eadcc6;
  border-radius: 10px;
  background: #fff;
  color: #99501e;
  font-size: 21px;
}

.upload-dropzone strong {
  max-width: 100%;
  overflow: hidden;
  color: #40271d;
  font-size: 12px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.upload-help {
  color: #8b7969;
  font-size: 10px;
}

.csv-template,
.manual-question-card {
  padding: 14px;
  border: 1px solid #eee5d8;
  border-radius: 12px;
  background: #fffdfa;
}

.template-heading {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.template-heading div {
  display: grid;
  gap: 3px;
}

.template-heading strong {
  color: #4c3428;
  font-size: 11px;
}

.template-heading span {
  color: #917f6f;
  font-size: 10px;
}

.template-link {
  flex-shrink: 0;
  padding: 4px 0;
  border: 0;
  background: transparent;
  color: #8f481e;
  font-size: 10px;
  font-weight: 800;
}

.template-link:hover,
.add-question-btn:hover {
  transform: none;
  box-shadow: none;
  text-decoration: underline;
}

.csv-template pre,
.csv-preview-info pre {
  overflow-x: auto;
  padding: 11px 12px;
  border-radius: 8px;
  background: #f7f2e9;
  color: #5d4b3c;
  font: 11px/1.6 ui-monospace, SFMono-Regular, Consolas, monospace;
  white-space: pre-wrap;
}

.manual-question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 15px;
}

.manual-question-header h3,
.csv-preview-info h3 {
  color: #442b20;
  font-size: 13px;
  font-weight: 800;
}

.manual-question-card .form-group + .form-group {
  margin-top: 13px;
}

.question-score-field {
  max-width: 180px;
  margin-top: 13px;
}

.remove-question-btn {
  padding: 6px 10px;
  font-size: 10px;
}

.add-question-btn {
  justify-self: start;
  padding: 9px 12px;
  border: 1px solid #d8c7ad;
  border-radius: 9px;
  background: #fffaf1;
  color: #72411f;
  font-size: 11px;
  font-weight: 800;
}

.help-text {
  color: #827363;
  font-size: 11px;
}

.csv-preview-info {
  display: grid;
  gap: 8px;
  margin-top: 3px;
}

.preview-error-message {
  padding: 11px 12px;
  border: 1px solid #edc7bd;
  border-radius: 8px;
  background: #fff2ed;
  color: #8c3322;
  font-size: 11px;
}

.guidance-card {
  position: sticky;
  top: 18px;
  padding: 22px 20px 18px;
}

.guidance-card .eyebrow {
  margin-bottom: 9px;
  font-size: 9px;
}

.guidance-card h2 {
  color: #3b1713;
  font-size: 17px;
}

.guidance-copy {
  margin-top: 9px;
  color: #786658;
  font-size: 11px;
  line-height: 1.7;
}

.workflow-steps {
  display: grid;
  gap: 17px;
  margin: 22px 0;
  padding: 0;
  list-style: none;
}

.workflow-steps li {
  display: flex;
  align-items: center;
  gap: 11px;
  color: #523b2f;
  font-size: 11px;
}

.workflow-steps li span {
  display: grid;
  width: 25px;
  height: 25px;
  flex: 0 0 25px;
  place-items: center;
  border: 1px solid #e4d6c2;
  border-radius: 50%;
  background: #fbf7ef;
  color: #76563e;
  font-size: 10px;
  font-weight: 800;
}

.workflow-steps li strong {
  font-weight: 700;
}

.guidance-actions {
  display: grid;
  gap: 12px;
}

.guidance-actions .btn-primary {
  display: flex;
  width: 100%;
  min-height: 46px;
  justify-content: space-between;
  padding: 0 15px;
  border-radius: 10px;
  background: #3b1713;
  color: #fffaf1;
  box-shadow: 0 4px 0 #b85b0a;
  font-size: 12px;
}

.guidance-actions .btn-primary span {
  font-size: 17px;
}

.cancel-link {
  justify-self: center;
  color: #806d5e;
  font-size: 11px;
  font-weight: 700;
  text-decoration: none;
}

.cancel-link:hover {
  color: #3b1713;
  text-decoration: underline;
}

.privacy-note {
  display: flex;
  align-items: flex-start;
  gap: 7px;
  margin-top: 19px;
  padding-top: 14px;
  border-top: 1px solid #eee5d8;
  color: #897969;
  font-size: 9px;
  line-height: 1.5;
}

.privacy-note span {
  color: #9c5a26;
}

.success-message,
.error-message {
  padding: 13px 15px;
  border: 1px solid var(--glass-border);
  border-radius: 10px;
  font-size: 12px;
  font-weight: 700;
  text-align: center;
}

.success-message {
  background: #eef7ef;
  color: #286246;
}

.error-message {
  background: #fff2ed;
  color: #8c3322;
}

.rubric-section {
  max-width: 1000px;
  margin: 0 auto 24px;
}

@media (max-width: 900px) {
  .setup-grid {
    grid-template-columns: minmax(0, 1fr) 250px;
    gap: 15px;
  }

  .setup-card {
    padding: 20px;
  }
}

@media (max-width: 720px) {
  .create-exam-page {
    padding: 14px 0 32px;
  }

  .create-exam-heading {
    align-items: flex-start;
    flex-direction: column;
    gap: 14px;
    margin-bottom: 18px;
  }

  .setup-grid {
    grid-template-columns: 1fr;
  }

  .guidance-card {
    position: static;
    grid-row: 1;
  }

  .workflow-steps {
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 8px;
  }

  .workflow-steps li {
    align-items: flex-start;
    flex-direction: column;
    gap: 7px;
    font-size: 10px;
  }
}

@media (max-width: 520px) {
  .setup-card,
  .guidance-card {
    padding: 17px 15px;
  }

  .details-fields,
  .creation-mode-toggle,
  .workflow-steps {
    grid-template-columns: 1fr;
  }

  .template-heading {
    align-items: flex-start;
    flex-direction: column;
    gap: 5px;
  }

  .workflow-steps li {
    align-items: center;
    flex-direction: row;
  }
}
</style>




