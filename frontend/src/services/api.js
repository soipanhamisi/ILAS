import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
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

  // Grade submission using per-question grades
  gradeSubmission(submissionId, instructorId, questionGrades) {
    return apiClient.post(`/instructor/exams/submissions/${submissionId}/grade`, {
      instructorId,
      questionGrades
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
  },

  // Get exam questions
  getExamQuestions(examId, instructorId) {
    return apiClient.get(`/instructor/exams/${examId}/questions`, {
      params: { instructorId }
    })
  },

  // Get exam question details (question text + max grade)
  getExamQuestionDetails(examId, instructorId) {
    return apiClient.get(`/instructor/exams/${examId}/questions/details`, {
      params: { instructorId }
    })
  }
}

// Student API Service
export const studentAPI = {
  // Submit exam
  submitExam(examId, studentId, questionAnswers) {
    return apiClient.post(`/student/exams/${examId}/submit`, {
      studentId,
      questionAnswers
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

  // Get exam questions
  getExamQuestions(examId, studentId) {
    return apiClient.get(`/student/exams/${examId}/questions`, {
      params: { studentId }
    })
  },

  // Get all courses available for enrollment
  getAllCourses(studentId) {
    return apiClient.get('/student/courses', {
      params: { studentId }
    })
  },

  // Get enrolled courses
  getEnrolledCourses(studentId) {
    return apiClient.get('/student/courses/enrolled', {
      params: { studentId }
    })
  },

  // Enroll student in a course
  enrollInCourse(studentId, courseId) {
    return apiClient.post(`/student/courses/${courseId}/enroll`, null, {
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

