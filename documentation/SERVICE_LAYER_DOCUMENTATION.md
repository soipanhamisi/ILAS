# Services Layer Implementation Summary

## Overview
The services layer has been implemented to handle the specific use cases for instructors and students in the LMS exam/assessment system.

## Architecture

### Entities Created
1. **ExamSubmission** (`ExamSubmission.java`)
   - Tracks student submissions for exams
   - Stores grades, feedback, and grade justifications
   - Links students, exams, and instructors (who graded)
   - Stores CSV file paths for submitted assessments

### Repositories Created
1. **ExamSubmissionRepository** (`ExamSubmissionRepository.java`)
   - Full CRUD operations for exam submissions
   - Query methods for finding submissions by student, exam, grade status, etc.

### Configuration
1. **FileStorageProperties** (`FileStorageProperties.java`)
   - Configures file upload directory: `storage/exam-assessments`
   - Can be overridden in `application.properties`

### Services

#### 1. FileStorageService (`FileStorageService.java`)
**Purpose**: Handle CSV file storage operations

**Methods**:
- `storeFile(MultipartFile file, String prefix)` - Store uploaded CSV files with unique names
- `deleteFile(String filePath)` - Delete stored files
- `getFileStorageLocation()` - Get storage directory path

**Features**:
- Validates CSV files only
- Generates unique filenames using UUID
- Creates storage directory automatically

---

#### 2. InstructorExamService (`InstructorExamService.java`)
**Purpose**: Handle instructor-specific exam operations

**Use Cases Implemented**:

##### UC1: Create Assessment
**Method**: `createAssessment(instructorId, courseId, examTitle, maxScore, csvFile)`
- Instructor creates an exam for their course
- Uploads CSV template for the exam
- Validates instructor owns the course
- Prevents duplicate exam titles per course

##### UC2: Issue Exam Grade
**Method**: `issueGrade(instructorId, submissionId, grade, feedback, gradeJustification)`
- Grades a student's exam submission
- Validates grade is within max score range
- Records grading timestamp and instructor who graded
- Provides feedback and detailed justification

##### UC3: Issue Comments and Grade Justifications
**Method**: `updateFeedback(instructorId, submissionId, feedback, gradeJustification)`
- Updates feedback for an already graded submission
- Allows instructors to modify comments/justifications

**Additional Helper Methods**:
- `getSubmissionsForExam(instructorId, examId)` - View all submissions for an exam
- `getUngradedSubmissions(instructorId, examId)` - Filter ungraded submissions
- `getExamsForCourse(instructorId, courseId)` - List all exams for a course

**Security Features**:
- Validates instructor owns the course before operations
- Prevents unauthorized access to other instructors' courses

---

#### 3. StudentExamService (`StudentExamService.java`)
**Purpose**: Handle student-specific exam operations

**Use Cases Implemented**:

##### UC1: Take Assessment and Submit
**Method**: `submitAssessment(studentId, examId, csvFile)`
- Student submits completed exam as CSV
- Validates student is enrolled in the course
- Prevents duplicate submissions
- Records submission timestamp

##### UC2: Receive Grade and Feedback
**Method**: `getGradeAndFeedback(studentId, examId)`
- Student views their graded submission
- Returns grade, feedback, and grade justification
- Shows who graded the exam and when

**Additional Helper Methods**:
- `getAllSubmissionsForStudent(studentId)` - View all submissions across all exams
- `getGradedSubmissionsForStudent(studentId)` - View only graded submissions
- `getAvailableExams(studentId)` - List exams from enrolled courses
- `hasSubmitted(studentId, examId)` - Check submission status
- `getExamDetails(studentId, examId)` - View exam information before taking

**Security Features**:
- Validates student is enrolled before allowing exam access
- Prevents submission to exams from non-enrolled courses

---

## Data Flow

### Instructor Workflow
1. Instructor creates assessment with CSV template → `InstructorExamService.createAssessment()`
2. Students submit their responses → `StudentExamService.submitAssessment()`
3. Instructor views submissions → `InstructorExamService.getSubmissionsForExam()`
4. Instructor grades submissions → `InstructorExamService.issueGrade()`
5. Instructor updates feedback (optional) → `InstructorExamService.updateFeedback()`

### Student Workflow
1. Student views available exams → `StudentExamService.getAvailableExams()`
2. Student views exam details → `StudentExamService.getExamDetails()`
3. Student submits assessment → `StudentExamService.submitAssessment()`
4. Student checks grade and feedback → `StudentExamService.getGradeAndFeedback()`

---

## CSV File Format

### Exam Template (Created by Instructor)
```csv
question,learnerResponses,maxGrade,grade
What is 2+2?,,[10],
Define OOP,,[20],
```

### Student Submission (Completed Exam)
```csv
question,learnerResponses,maxGrade,grade
What is 2+2?,4,[10],
Define OOP?,Object-oriented programming is...,[20],
```

**Note**: The `grade` column is filled by the instructor during manual grading via the system.

---

## Transaction Management
- All write operations are annotated with `@Transactional`
- Ensures data consistency across multiple repository operations
- Automatic rollback on exceptions

---

## Error Handling
All service methods throw `IllegalArgumentException` with descriptive messages for:
- Entity not found (student, instructor, exam, course, submission)
- Authorization failures (instructor doesn't own course)
- Business rule violations (duplicate submissions, invalid grades)
- Enrollment issues (student not enrolled in course)

---

## Next Steps
To complete the application, you should:
1. Create REST controllers to expose these services via HTTP endpoints
2. Add security layer (Spring Security) for authentication/authorization
3. Create DTOs (Data Transfer Objects) for API responses
4. Add exception handler for consistent error responses
5. Implement CSV parsing/validation logic if needed
6. Add pagination for list operations
7. Create integration tests

---

## Dependencies Required
Ensure these are in your `pom.xml`:
- Spring Data JPA
- Spring Boot Starter Web (for MultipartFile)
- Lombok
- Jakarta Persistence API

---

## Configuration
Add to `application.properties`:
```properties
# File upload settings
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# File storage location (optional override)
file.storage.upload-dir=storage/exam-assessments
```

