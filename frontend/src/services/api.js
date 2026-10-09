import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

apiClient.interceptors.request.use((config) => {
  const token = typeof localStorage === 'undefined' ? null : localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Auth API Service
export const authAPI = {
  // Login with username and password
  login(username, password, userType) {
    return apiClient.post('/auth/login', {
      username,
      password,
      userType
    })
  },

  // Signup new user
  signup(name, email, username, password, userType) {
    return apiClient.post('/auth/signup', {
      name,
      email,
      username,
      password,
      userType
    })
  }
}

// Instructor API Service
export const instructorAPI = {
  // Create exam
  createExam(_instructorId, courseId, examTitle, maxScore, file) {
    const formData = new FormData()
    formData.append('courseId', courseId)
    formData.append('examTitle', examTitle)
    formData.append('maxScore', maxScore)
    formData.append('file', file)

    return apiClient.post('/instructor/exams/create', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // Get a single exam and its rubric metadata
  getExamDetails(examId) {
    return apiClient.get(`/instructor/exams/${examId}`)
  },

  // Save per-question grading rubrics
  saveExamRubrics(examId, _instructorId, rubrics) {
    return apiClient.put(`/instructor/exams/${examId}/rubrics`, {
      rubrics
    })
  },

  // Grade submission using per-question grades
  gradeSubmission(submissionId, _instructorId, questionGrades) {
    return apiClient.post(`/instructor/exams/submissions/${submissionId}/grade`, {
      questionGrades
    })
  },

  // Update feedback
  updateFeedback(submissionId, _instructorId, feedback, gradeJustification) {
    return apiClient.put(`/instructor/exams/submissions/${submissionId}/feedback`, {
      feedback,
      gradeJustification
    })
  },

  // Trigger LLM-based grading for a submission
  autoGradeSubmission(submissionId, _instructorId) {
    return apiClient.post(`/instructor/exams/submissions/${submissionId}/auto-grade`, {})
  },

  // Get submissions for exam
  getSubmissionsForExam(examId) {
    return apiClient.get(`/instructor/exams/${examId}/submissions`)
  },

  // Get ungraded submissions
  getUngradedSubmissions(examId) {
    return apiClient.get(`/instructor/exams/${examId}/submissions/ungraded`)
  },

  // Get exams for course
  getExamsForCourse(courseId) {
    return apiClient.get(`/instructor/exams/courses/${courseId}`)
  },

  // Get exam questions
  getExamQuestions(examId) {
    return apiClient.get(`/instructor/exams/${examId}/questions`)
  },

  // Get exam question details (question text + max grade)
  getExamQuestionDetails(examId) {
    return apiClient.get(`/instructor/exams/${examId}/questions/details`)
  },

  // Get instructor dashboard summary metrics
  getDashboardSummary() {
    return apiClient.get('/instructor/exams/dashboard/summary')
  }
}

// Student API Service
export const studentAPI = {
  // Submit exam
  submitExam(examId, _studentId, questionAnswers) {
    return apiClient.post(`/student/exams/${examId}/submit`, {
      questionAnswers
    })
  },

  // Get grade and feedback
  getGradeAndFeedback(examId) {
    return apiClient.get(`/student/exams/${examId}/grade`)
  },

  // Get all submissions
  getAllSubmissions() {
    return apiClient.get('/student/exams/submissions')
  },

  // Get graded submissions
  getGradedSubmissions() {
    return apiClient.get('/student/exams/submissions/graded')
  },

  // Get available exams
  getAvailableExams() {
    return apiClient.get('/student/exams/available')
  },

  // Get exam details
  getExamDetails(examId) {
    return apiClient.get(`/student/exams/${examId}`)
  },

  // Get exam questions
  getExamQuestions(examId) {
    return apiClient.get(`/student/exams/${examId}/questions`)
  },

  // Get all courses available for enrollment
  getAllCourses() {
    return apiClient.get('/student/courses')
  },

  // Get enrolled courses
  getEnrolledCourses() {
    return apiClient.get('/student/courses/enrolled')
  },

  // Enroll student in a course
  enrollInCourse(courseId) {
    return apiClient.post(`/student/courses/${courseId}/enroll`)
  },

  // Check if submitted
  hasSubmitted(examId) {
    return apiClient.get(`/student/exams/${examId}/submitted`)
  }
}

// Admin API Service
export const adminAPI = {
  // Get dashboard summary
  getDashboardSummary() {
    return apiClient.get('/admin/dashboard/summary')
  },

  // Get system statistics
  getSystemStats() {
    return apiClient.get('/admin/stats')
  },

  // Get total student count
  getTotalStudents() {
    return apiClient.get('/admin/students/count')
  },

  // Get total instructor count
  getTotalInstructors() {
    return apiClient.get('/admin/instructors/count')
  },

  // Get total course count
  getTotalCourses() {
    return apiClient.get('/admin/courses/count')
  },

  // Get total exam count
  getTotalExams() {
    return apiClient.get('/admin/exams/count')
  },

  // Get real-time monitoring metrics for admin dashboard
  getMonitoringSummary() {
    return apiClient.get('/admin/dashboard/monitoring')
  },

  // Heartbeat for active-user tracking
  sendHeartbeat() {
    return apiClient.post('/monitoring/heartbeat')
  }
}

export default apiClient
