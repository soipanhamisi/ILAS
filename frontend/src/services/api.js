import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Instructor API Service
export const instructorAPI = {
  // Create exam
  createExam(instructorId, courseId, examTitle, maxScore, file) {
    const formData = new FormData()
    formData.append('instructorId', instructorId)
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

  // Grade submission
  gradeSubmission(submissionId, instructorId, grade, feedback, gradeJustification) {
    return apiClient.post(`/instructor/exams/submissions/${submissionId}/grade`, {
      instructorId,
      grade,
      feedback,
      gradeJustification
    })
  },

  // Update feedback
  updateFeedback(submissionId, instructorId, feedback, gradeJustification) {
    return apiClient.put(`/instructor/exams/submissions/${submissionId}/feedback`, {
      instructorId,
      feedback,
      gradeJustification
    })
  },

  // Get submissions for exam
  getSubmissionsForExam(examId, instructorId) {
    return apiClient.get(`/instructor/exams/${examId}/submissions`, {
      params: { instructorId }
    })
  },

  // Get ungraded submissions
  getUngradedSubmissions(examId, instructorId) {
    return apiClient.get(`/instructor/exams/${examId}/submissions/ungraded`, {
      params: { instructorId }
    })
  },

  // Get exams for course
  getExamsForCourse(courseId, instructorId) {
    return apiClient.get(`/instructor/exams/courses/${courseId}`, {
      params: { instructorId }
    })
  }
}

// Student API Service
export const studentAPI = {
  // Submit exam
  submitExam(examId, studentId, file) {
    const formData = new FormData()
    formData.append('studentId', studentId)
    formData.append('file', file)

    return apiClient.post(`/student/exams/${examId}/submit`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // Get grade and feedback
  getGradeAndFeedback(examId, studentId) {
    return apiClient.get(`/student/exams/${examId}/grade`, {
      params: { studentId }
    })
  },

  // Get all submissions
  getAllSubmissions(studentId) {
    return apiClient.get('/student/exams/submissions', {
      params: { studentId }
    })
  },

  // Get graded submissions
  getGradedSubmissions(studentId) {
    return apiClient.get('/student/exams/submissions/graded', {
      params: { studentId }
    })
  },

  // Get available exams
  getAvailableExams(studentId) {
    return apiClient.get('/student/exams/available', {
      params: { studentId }
    })
  },

  // Get exam details
  getExamDetails(examId, studentId) {
    return apiClient.get(`/student/exams/${examId}`, {
      params: { studentId }
    })
  },

  // Check if submitted
  hasSubmitted(examId, studentId) {
    return apiClient.get(`/student/exams/${examId}/submitted`, {
      params: { studentId }
    })
  }
}

export default apiClient

