<template>
  <div class="rubric-panel">
    <div class="rubric-panel-header">
      <div>
        <h2 class="section-title">{{ title }}</h2>
        <p class="rubric-help">
          {{ subtitle }}
        </p>
      </div>
      <div class="rubric-actions">
        <button
          v-if="showSkipButton"
          @click="$emit('skip')"
          class="btn-secondary"
          :disabled="saving || examQuestionDetails.length === 0"
        >
          {{ skipButtonText }}
        </button>
        <button
          @click="$emit('save', rubricForm)"
          class="btn-primary"
          :disabled="saving || examQuestionDetails.length === 0"
        >
          {{ saving ? 'Saving...' : saveButtonText }}
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="examQuestionDetails.length === 0" class="loading-state">
      <div class="loading-spinner"></div>
      <p>Loading question details...</p>
      <p class="loading-hint">If questions don't appear after a few seconds, you can still proceed to define rubrics later in the Exam Submissions page.</p>
    </div>

    <!-- Questions Display -->
    <div v-else>
      <div class="rubric-grid">
        <div
          v-for="(question, index) in examQuestionDetails"
          :key="`rubric-${index}`"
          class="rubric-card"
        >
          <h3>Q{{ index + 1 }} — Max {{ question.maxGrade }}</h3>
          <p class="question-title">{{ question.questionText }}</p>

          <label class="rubric-label">Rubric max score</label>
          <input
            v-model.number="rubricForm[index].maxScore"
            type="number"
            min="0"
            class="score-input"
          />

          <label class="rubric-label">Rubric guidance for LLM</label>
          <textarea
            v-model="rubricForm[index].rubricText"
            rows="4"
            placeholder="Describe the criteria the LLM should use when grading this question. Include key points, complexity expectations, etc."
            class="rubric-textarea"
          ></textarea>

          <div class="rubric-status">
            <span
              :class="['status-dot', rubricForm[index].rubricText ? 'filled' : 'empty']"
            ></span>
            <span v-if="rubricForm[index].rubricText" class="status-text">Rubric defined</span>
            <span v-else class="status-text empty">Rubric pending</span>
          </div>
        </div>
      </div>

      <div v-if="showProgressIndicator" class="progress-indicator">
        <p>{{ rubricForm.filter(r => r.rubricText).length }} of {{ rubricForm.length }} rubrics defined</p>
        <div class="progress-bar">
          <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, ref, computed, watch } from 'vue'

const props = defineProps({
  examQuestionDetails: {
    type: Array,
    required: true,
    default: () => []
  },
  initialRubrics: {
    type: Array,
    default: () => []
  },
  title: {
    type: String,
    default: 'Define Rubrics for Auto-Grading'
  },
  subtitle: {
    type: String,
    default: 'Define what the LLM should look for on each question before auto-grading submissions.'
  },
  saveButtonText: {
    type: String,
    default: 'Save Rubrics'
  },
  skipButtonText: {
    type: String,
    default: 'Skip & Go Back'
  },
  showSkipButton: {
    type: Boolean,
    default: false
  },
  showProgressIndicator: {
    type: Boolean,
    default: false
  },
  saving: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['save', 'skip'])

const rubricForm = ref([])

// Initialize rubric form with existing data or empty values
const initializeRubricForm = () => {
  rubricForm.value = props.examQuestionDetails.map((question, index) => {
    const existingRubric = props.initialRubrics[index]
    return {
      questionNumber: index + 1,
      maxScore: existingRubric?.maxScore || question.maxGrade || 0,
      rubricText: existingRubric?.rubricText || ''
    }
  })
}

// Watch for changes in examQuestionDetails
watch(() => props.examQuestionDetails, () => {
  initializeRubricForm()
}, { deep: true })

// Watch for initial rubrics changes
watch(() => props.initialRubrics, () => {
  initializeRubricForm()
}, { deep: true })

// Initialize on mount
initializeRubricForm()

// Computed property for progress percentage
const progressPercent = computed(() => {
  if (rubricForm.value.length === 0) return 0
  const definedCount = rubricForm.value.filter(r => r.rubricText).length
  return Math.round((definedCount / rubricForm.value.length) * 100)
})
</script>

<style scoped>
.rubric-panel {
  background: rgba(255, 255, 255, 0.48);
  border: 1px solid rgba(255, 255, 255, 0.62);
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
  backdrop-filter: blur(10px);
}

.rubric-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  gap: 16px;
}

.rubric-panel-header h2 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: var(--color-primary);
}

.rubric-help {
  margin: 0;
  font-size: 14px;
  color: var(--color-text-soft);
}

.rubric-actions {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

.rubric-actions button {
  padding: 10px 16px;
  font-size: 14px;
  white-space: nowrap;
}

.rubric-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(380px, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.rubric-card {
  background: rgba(255, 255, 255, 0.56);
  border: 1px solid rgba(255, 255, 255, 0.62);
  border-radius: 8px;
  padding: 16px;
  display: flex;
  flex-direction: column;
}

.rubric-card h3 {
  margin: 0 0 8px 0;
  color: var(--color-primary);
  font-size: 16px;
}

.question-title {
  margin: 0 0 12px 0;
  color: var(--color-text);
  font-size: 14px;
  font-style: italic;
  line-height: 1.4;
}

.rubric-label {
  display: block;
  margin: 12px 0 6px 0;
  font-weight: 600;
  color: var(--color-text);
  font-size: 13px;
}

.score-input,
.rubric-textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid rgba(112, 113, 77, 0.3);
  border-radius: 4px;
  font-family: inherit;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.72);
  color: var(--color-text);
}

.rubric-textarea {
  resize: vertical;
  min-height: 100px;
}

.rubric-textarea:focus,
.score-input:focus {
  outline: none;
  border-color: var(--color-primary);
  background: rgba(255, 255, 255, 0.9);
}

.rubric-status {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(112, 113, 77, 0.2);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  transition: background 0.3s ease;
}

.status-dot.filled {
  background: #16a34a;
}

.status-dot.empty {
  background: #cbd5e1;
}

.status-text {
  font-size: 12px;
  color: #16a34a;
  font-weight: 600;
}

.status-text.empty {
  color: #64748b;
}

.progress-indicator {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(112, 113, 77, 0.2);
}

.progress-indicator p {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: var(--color-text);
  font-weight: 600;
}

.progress-bar {
  width: 100%;
  height: 6px;
  background: rgba(112, 113, 77, 0.2);
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-primary), var(--color-accent));
  transition: width 0.3s ease;
  border-radius: 3px;
}

/* Loading State */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(112, 113, 77, 0.2);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-state p {
  margin: 8px 0;
  color: var(--color-text);
  font-size: 14px;
}

.loading-hint {
  color: var(--color-text-soft);
  font-size: 13px !important;
  margin-top: 12px !important;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .rubric-grid {
    grid-template-columns: 1fr;
  }

  .rubric-panel-header {
    flex-direction: column;
    align-items: stretch;
  }

  .rubric-actions {
    flex-direction: column;
    width: 100%;
  }

  .rubric-actions button {
    width: 100%;
  }
}
</style>

