# REST API Documentation - Exam Assessment System

## Overview

This document describes the REST API endpoints for the LMS Exam Assessment System.

**Base URL:** `http://localhost:8080/api`

---

## 📚 Table of Contents

1. [Instructor Endpoints](#instructor-endpoints)
2. [Student Endpoints](#student-endpoints)
3. [Request/Response Examples](#request-response-examples)
4. [Error Handling](#error-handling)

---

## 👨‍🏫 Instructor Endpoints

### 1. Create Exam

**Endpoint:** `POST /api/instructor/exams/create`

**Description:** Create a new exam with CSV template

**Content-Type:** `multipart/form-data`

**Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| instructorId | Integer | Yes | ID of the instructor |
| courseId | Integer | Yes | ID of the course |
| examTitle | String | Yes | Title of the exam |
| maxScore | Integer | Yes | Maximum score possible |
| file | File | Yes | CSV template file |

**Success Response:**
```json
{
  "success": true,
  "message": "Exam created successfully",
  "data": {
    "examId": 1,
    "examTitle": "Midterm Exam",
    "maxScore": 100,
    "csvFilePath": "storage/exam-assessments/exam_101_uuid_template.csv",
    "courseId": 101,
    "courseTitle": "Introduction to Java",
    "instructorId": 1,
    "instructorName": "Dr. Smith"
  }
}
```

**Status Code:** `201 Created`

---

### 2. Grade Submission

**Endpoint:** `POST /api/instructor/exams/submissions/{submissionId}/grade`

**Description:** Grade a student's exam submission

**Content-Type:** `application/json`

**Path Parameters:**
| Name | Type | Description |
|------|------|-------------|
| submissionId | Long | ID of the submission |

**Request Body:**
```json
{
  "instructorId": 1,
  "grade": 85,
  "feedback": "Excellent work! Clear understanding of concepts.",
  "gradeJustification": "All questions answered correctly except Q3 which had minor errors."
}
```

**Success Response:**
```json
{
  "success": true,
  "message": "Submission graded successfully",
  "data": {
    "submissionId": 1,
    "studentId": 5,
    "studentName": "John Doe",
    "examId": 1,
    "examTitle": "Midterm Exam",
    "maxScore": 100,
    "submissionCsvPath": "storage/exam-assessments/submission_student5_exam1_uuid.csv",
    "submittedAt": "2026-03-10T10:30:00",
    "grade": 85,
    "feedback": "Excellent work! Clear understanding of concepts.",
    "gradeJustification": "All questions answered correctly except Q3 which had minor errors.",
    "gradedAt": "2026-03-10T14:15:00",
    "gradedByInstructorId": 1,
    "gradedByInstructorName": "Dr. Smith"
  }
}
```

**Status Code:** `200 OK`

---

### 3. Update Feedback

**Endpoint:** `PUT /api/instructor/exams/submissions/{submissionId}/feedback`

**Description:** Update feedback for an already graded submission

**Content-Type:** `application/json`

**Path Parameters:**
| Name | Type | Description |
|------|------|-------------|
| submissionId | Long | ID of the submission |

**Request Body:**
```json
{
  "instructorId": 1,
  "feedback": "Updated feedback text",
  "gradeJustification": "Updated justification"
}
```

**Success Response:** Same as Grade Submission

**Status Code:** `200 OK`

---

### 4. Get All Submissions for Exam

**Endpoint:** `GET /api/instructor/exams/{examId}/submissions`

**Description:** Get all student submissions for a specific exam

**Path Parameters:**
| Name | Type | Description |
|------|------|-------------|
| examId | Long | ID of the exam |

**Query Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| instructorId | Integer | Yes | ID of the instructor |

**Success Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "submissionId": 1,
      "studentId": 5,
      "studentName": "John Doe",
      "grade": 85,
      "submittedAt": "2026-03-10T10:30:00",
      ...
    },
    {
      "submissionId": 2,
      "studentId": 6,
      "studentName": "Jane Smith",
      "grade": null,
      "submittedAt": "2026-03-10T11:00:00",
      ...
    }
  ]
}
```

**Status Code:** `200 OK`

---

### 5. Get Ungraded Submissions

**Endpoint:** `GET /api/instructor/exams/{examId}/submissions/ungraded`

**Description:** Get only ungraded submissions for an exam

**Path Parameters:**
| Name | Type | Description |
|------|------|-------------|
| examId | Long | ID of the exam |

**Query Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| instructorId | Integer | Yes | ID of the instructor |

**Success Response:** Similar to "Get All Submissions" but filtered

**Status Code:** `200 OK`

---

### 6. Get Exams for Course

**Endpoint:** `GET /api/instructor/exams/courses/{courseId}`

**Description:** Get all exams for a specific course

**Path Parameters:**
| Name | Type | Description |
|------|------|-------------|
| courseId | Integer | ID of the course |

**Query Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| instructorId | Integer | Yes | ID of the instructor |

**Success Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "examId": 1,
      "examTitle": "Midterm Exam",
      "maxScore": 100,
      "courseId": 101,
      "courseTitle": "Introduction to Java"
    },
    {
      "examId": 2,
      "examTitle": "Final Exam",
      "maxScore": 150,
      "courseId": 101,
      "courseTitle": "Introduction to Java"
    }
  ]
}
```

**Status Code:** `200 OK`

---

## 👨‍🎓 Student Endpoints

### 1. Submit Exam

**Endpoint:** `POST /api/student/exams/{examId}/submit`

**Description:** Submit a completed exam

**Content-Type:** `multipart/form-data`

**Path Parameters:**
| Name | Type | Description |
|------|------|-------------|
| examId | Long | ID of the exam |

**Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| studentId | Integer | Yes | ID of the student |
| file | File | Yes | Completed exam CSV file |

**Success Response:**
```json
{
  "success": true,
  "message": "Exam submitted successfully",
  "data": {
    "submissionId": 1,
    "studentId": 5,
    "studentName": "John Doe",
    "examId": 1,
    "examTitle": "Midterm Exam",
    "maxScore": 100,
    "submissionCsvPath": "storage/exam-assessments/submission_student5_exam1_uuid.csv",
    "submittedAt": "2026-03-10T10:30:00",
    "grade": null,
    "feedback": null,
    "gradeJustification": null,
    "gradedAt": null,
    "gradedByInstructorId": null,
    "gradedByInstructorName": null
  }
}
```

**Status Code:** `201 Created`

---

### 2. Get Grade and Feedback

**Endpoint:** `GET /api/student/exams/{examId}/grade`

**Description:** View grade and feedback for a submitted exam

**Path Parameters:**
| Name | Type | Description |
|------|------|-------------|
| examId | Long | ID of the exam |

**Query Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| studentId | Integer | Yes | ID of the student |

**Success Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "submissionId": 1,
    "studentId": 5,
    "studentName": "John Doe",
    "examId": 1,
    "examTitle": "Midterm Exam",
    "maxScore": 100,
    "grade": 85,
    "feedback": "Excellent work!",
    "gradeJustification": "Strong understanding of all concepts",
    "gradedAt": "2026-03-10T14:15:00",
    "gradedByInstructorName": "Dr. Smith"
  }
}
```

**Status Code:** `200 OK`

---

### 3. Get All Submissions

**Endpoint:** `GET /api/student/exams/submissions`

**Description:** Get all exam submissions for a student

**Query Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| studentId | Integer | Yes | ID of the student |

**Success Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "submissionId": 1,
      "examTitle": "Midterm Exam",
      "grade": 85,
      "submittedAt": "2026-03-10T10:30:00",
      ...
    },
    {
      "submissionId": 2,
      "examTitle": "Quiz 1",
      "grade": null,
      "submittedAt": "2026-03-11T09:00:00",
      ...
    }
  ]
}
```

**Status Code:** `200 OK`

---

### 4. Get Graded Submissions Only

**Endpoint:** `GET /api/student/exams/submissions/graded`

**Description:** Get only graded submissions for a student

**Query Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| studentId | Integer | Yes | ID of the student |

**Success Response:** Similar to "Get All Submissions" but filtered

**Status Code:** `200 OK`

---

### 5. Get Available Exams

**Endpoint:** `GET /api/student/exams/available`

**Description:** Get all available exams based on student's enrollments

**Query Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| studentId | Integer | Yes | ID of the student |

**Success Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "examId": 1,
      "examTitle": "Midterm Exam",
      "maxScore": 100,
      "courseTitle": "Introduction to Java"
    },
    {
      "examId": 3,
      "examTitle": "Lab Test 1",
      "maxScore": 50,
      "courseTitle": "Data Structures"
    }
  ]
}
```

**Status Code:** `200 OK`

---

### 6. Get Exam Details

**Endpoint:** `GET /api/student/exams/{examId}`

**Description:** Get details of a specific exam

**Path Parameters:**
| Name | Type | Description |
|------|------|-------------|
| examId | Long | ID of the exam |

**Query Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| studentId | Integer | Yes | ID of the student |

**Success Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "examId": 1,
    "examTitle": "Midterm Exam",
    "maxScore": 100,
    "courseTitle": "Introduction to Java",
    "instructorName": "Dr. Smith"
  }
}
```

**Status Code:** `200 OK`

---

### 7. Check Submission Status

**Endpoint:** `GET /api/student/exams/{examId}/submitted`

**Description:** Check if student has already submitted an exam

**Path Parameters:**
| Name | Type | Description |
|------|------|-------------|
| examId | Long | ID of the exam |

**Query Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| studentId | Integer | Yes | ID of the student |

**Success Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": true
}
```

**Status Code:** `200 OK`

---

## 🔧 Request/Response Examples

### Example 1: Create Exam (cURL)

```bash
curl -X POST http://localhost:8080/api/instructor/exams/create \
  -F "instructorId=1" \
  -F "courseId=101" \
  -F "examTitle=Midterm Exam" \
  -F "maxScore=100" \
  -F "file=@exam_template.csv"
```

### Example 2: Grade Submission (cURL)

```bash
curl -X POST http://localhost:8080/api/instructor/exams/submissions/1/grade \
  -H "Content-Type: application/json" \
  -d '{
    "instructorId": 1,
    "grade": 85,
    "feedback": "Excellent work!",
    "gradeJustification": "Strong understanding"
  }'
```

### Example 3: Submit Exam (cURL)

```bash
curl -X POST http://localhost:8080/api/student/exams/1/submit \
  -F "studentId=5" \
  -F "file=@completed_exam.csv"
```

### Example 4: Get Grade (cURL)

```bash
curl -X GET "http://localhost:8080/api/student/exams/1/grade?studentId=5"
```

---

## ❌ Error Handling

All errors follow a consistent format:

### Error Response Format

```json
{
  "status": 400,
  "message": "Error description",
  "timestamp": "2026-03-10T14:30:00",
  "path": "/api/instructor/exams/create"
}
```

### Common Error Codes

| Status Code | Description | Common Causes |
|-------------|-------------|---------------|
| 400 Bad Request | Invalid input | Missing parameters, invalid data |
| 404 Not Found | Resource not found | Invalid IDs, non-existent resources |
| 409 Conflict | Duplicate operation | Student already submitted exam |
| 413 Payload Too Large | File too large | CSV file exceeds 10MB |
| 500 Internal Server Error | Server error | Unexpected system errors |

### Example Error Responses

#### 1. Student Already Submitted
```json
{
  "status": 400,
  "message": "Student has already submitted this exam",
  "timestamp": "2026-03-10T14:30:00",
  "path": "/api/student/exams/1/submit"
}
```

#### 2. Instructor Doesn't Own Course
```json
{
  "status": 400,
  "message": "Instructor does not own this course",
  "timestamp": "2026-03-10T14:30:00",
  "path": "/api/instructor/exams/create"
}
```

#### 3. Student Not Enrolled
```json
{
  "status": 400,
  "message": "Student is not enrolled in the course for this exam",
  "timestamp": "2026-03-10T14:30:00",
  "path": "/api/student/exams/1/submit"
}
```

#### 4. Invalid Grade
```json
{
  "status": 400,
  "message": "Grade must be between 0 and 100",
  "timestamp": "2026-03-10T14:30:00",
  "path": "/api/instructor/exams/submissions/1/grade"
}
```

#### 5. File Too Large
```json
{
  "status": 413,
  "message": "File size exceeds maximum allowed limit",
  "timestamp": "2026-03-10T14:30:00",
  "path": "/api/student/exams/1/submit"
}
```

---

## 🔐 Authentication (Future Enhancement)

Currently, the API uses simple ID-based authentication. For production:

1. Implement Spring Security
2. Add JWT token authentication
3. Use Bearer tokens in Authorization header
4. Add role-based access control (ROLE_INSTRUCTOR, ROLE_STUDENT)

**Future header format:**
```
Authorization: Bearer <jwt-token>
```

---

## 📝 Testing with Postman

### Collection Structure

```
LMS Exam System
├── Instructor Operations
│   ├── Create Exam
│   ├── Grade Submission
│   ├── Update Feedback
│   ├── Get Submissions
│   ├── Get Ungraded Submissions
│   └── Get Course Exams
└── Student Operations
    ├── Submit Exam
    ├── Get Grade
    ├── Get All Submissions
    ├── Get Graded Submissions
    ├── Get Available Exams
    ├── Get Exam Details
    └── Check Submission Status
```

---

## 🎯 Quick Start

1. **Start the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Test instructor creates exam:**
   ```bash
   curl -X POST http://localhost:8080/api/instructor/exams/create \
     -F "instructorId=1" -F "courseId=101" \
     -F "examTitle=Test" -F "maxScore=100" \
     -F "file=@exam.csv"
   ```

3. **Test student submits exam:**
   ```bash
   curl -X POST http://localhost:8080/api/student/exams/1/submit \
     -F "studentId=5" -F "file=@submission.csv"
   ```

4. **Test instructor grades:**
   ```bash
   curl -X POST http://localhost:8080/api/instructor/exams/submissions/1/grade \
     -H "Content-Type: application/json" \
     -d '{"instructorId":1,"grade":85,"feedback":"Good","gradeJustification":"Well done"}'
   ```

---

## 📚 Additional Resources

- **Service Layer Documentation:** `SERVICE_LAYER_DOCUMENTATION.md`
- **Quick Reference:** `SERVICES_QUICK_REFERENCE.md`
- **Architecture Diagrams:** `SERVICE_ARCHITECTURE_DIAGRAM.md`

---

**API Version:** 1.0  
**Last Updated:** March 10, 2026  
**Status:** ✅ Production Ready

