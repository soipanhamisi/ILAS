# 🎯 Services Layer Quick Reference

## Service Methods at a Glance

### 👨‍🏫 InstructorExamService

| Method | Purpose | Returns |
|--------|---------|---------|
| `createAssessment(instructorId, courseId, title, maxScore, csv)` | Create new exam | `Exam` |
| `issueGrade(instructorId, submissionId, grade, feedback, justification)` | Grade submission | `ExamSubmission` |
| `updateFeedback(instructorId, submissionId, feedback, justification)` | Update comments | `ExamSubmission` |
| `getSubmissionsForExam(instructorId, examId)` | Get all submissions | `List<ExamSubmission>` |
| `getUngradedSubmissions(instructorId, examId)` | Get ungraded only | `List<ExamSubmission>` |
| `getExamsForCourse(instructorId, courseId)` | Get course exams | `List<Exam>` |

### 👨‍🎓 StudentExamService

| Method | Purpose | Returns |
|--------|---------|---------|
| `submitAssessment(studentId, examId, csv)` | Submit exam | `ExamSubmission` |
| `getGradeAndFeedback(studentId, examId)` | View grade | `ExamSubmission` |
| `getAllSubmissionsForStudent(studentId)` | Get all submissions | `List<ExamSubmission>` |
| `getGradedSubmissionsForStudent(studentId)` | Get graded only | `List<ExamSubmission>` |
| `getAvailableExams(studentId)` | Get enrolled exams | `List<Exam>` |
| `hasSubmitted(studentId, examId)` | Check submission | `boolean` |
| `getExamDetails(studentId, examId)` | View exam info | `Exam` |

### 📁 FileStorageService

| Method | Purpose | Returns |
|--------|---------|---------|
| `storeFile(file, prefix)` | Store CSV file | `String` (path) |
| `deleteFile(filePath)` | Delete file | `void` |
| `getFileStorageLocation()` | Get storage path | `Path` |

---

## Object Properties

### Exam
```java
examId      : Long
course       : Course
examTitle    : String
maxScore     : int
csvFilePath  : String
```

### ExamSubmission
```java
submissionId        : Long
exam                : Exam
student             : Student
submissionCsvPath   : String
submittedAt         : LocalDateTime
grade               : Integer (nullable)
feedback            : String
gradeJustification  : String
gradedAt            : LocalDateTime
gradedBy            : Instructor
```

---

## Common Exceptions

| Exception | When Thrown |
|-----------|-------------|
| `IllegalArgumentException("Instructor not found...")` | Invalid instructor ID |
| `IllegalArgumentException("Student not found...")` | Invalid student ID |
| `IllegalArgumentException("Exam not found...")` | Invalid exam ID |
| `IllegalArgumentException("Instructor does not own this course")` | Authorization failure |
| `IllegalArgumentException("Student is not enrolled...")` | Enrollment check failed |
| `IllegalArgumentException("Student has already submitted...")` | Duplicate submission |
| `IllegalArgumentException("Grade must be between 0 and X")` | Invalid grade |
| `IllegalArgumentException("Only CSV files are allowed")` | Invalid file type |

---

## Typical Workflows

### Workflow 1: Create and Grade Exam
```
1. instructorExamService.createAssessment()
2. studentExamService.submitAssessment()
3. instructorExamService.getUngradedSubmissions()
4. instructorExamService.issueGrade()
5. studentExamService.getGradeAndFeedback()
```

### Workflow 2: Student Views Results
```
1. studentExamService.getAvailableExams()
2. studentExamService.hasSubmitted()
3. studentExamService.getGradeAndFeedback()
```

### Workflow 3: Instructor Reviews Course
```
1. instructorExamService.getExamsForCourse()
2. instructorExamService.getSubmissionsForExam()
3. instructorExamService.issueGrade() (for each)
```

---

## File Storage Paths

| Type | Pattern |
|------|---------|
| Exam Template | `exam_{courseId}_{uuid}_{filename}.csv` |
| Student Submission | `submission_student{studentId}_exam{examId}_{uuid}_{filename}.csv` |
| Storage Location | `storage/exam-assessments/` |

---

## Validation Rules

### Instructor Operations
- ✓ Must exist in database
- ✓ Must own the course
- ✓ Grade must be 0 ≤ grade ≤ maxScore

### Student Operations
- ✓ Must exist in database
- ✓ Must be enrolled in course
- ✓ Can only submit once per exam
- ✓ Only CSV files accepted

---

## Transaction Boundaries

Methods marked `@Transactional`:
- `createAssessment()` ← Atomic
- `issueGrade()` ← Atomic
- `updateFeedback()` ← Atomic
- `submitAssessment()` ← Atomic

All other methods are read-only.

---

## Dependencies

Each service autowires:

**InstructorExamService**
- ExamRepository
- ExamSubmissionRepository
- CourseRepository
- InstructorRepository
- FileStorageService

**StudentExamService**
- ExamRepository
- ExamSubmissionRepository
- StudentRepository
- EnrollmentRepository
- FileStorageService

**FileStorageService**
- FileStorageProperties

---

## Quick Start Code

```java
// In your controller or test class

@Autowired
private InstructorExamService instructorService;

@Autowired
private StudentExamService studentService;

// Instructor creates exam
Exam exam = instructorService.createAssessment(
    1,           // instructorId
    101,         // courseId
    "Quiz 1",    // title
    50,          // maxScore
    csvFile      // MultipartFile
);

// Student submits
ExamSubmission sub = studentService.submitAssessment(
    5,           // studentId
    exam.getExamId(),
    answerFile   // MultipartFile
);

// Instructor grades
ExamSubmission graded = instructorService.issueGrade(
    1,           // instructorId
    sub.getSubmissionId(),
    45,          // grade
    "Well done", // feedback
    "Minor errors in Q3" // justification
);

// Student views grade
ExamSubmission result = studentService.getGradeAndFeedback(
    5,           // studentId
    exam.getExamId()
);
```

---

## 📋 Checklist: Is Everything Working?

- [x] ExamSubmission entity created
- [x] ExamSubmissionRepository created
- [x] FileStorageService implemented
- [x] InstructorExamService implemented (3 use cases)
- [x] StudentExamService implemented (2 use cases)
- [x] File upload configuration added
- [x] All methods have proper validation
- [x] Transaction management in place
- [x] Documentation complete
- [ ] Controllers created (NEXT STEP)
- [ ] DTOs created (NEXT STEP)
- [ ] Exception handler added (NEXT STEP)
- [ ] Tests written (NEXT STEP)

---

**Status: ✅ Services Layer Complete and Ready to Use!**

