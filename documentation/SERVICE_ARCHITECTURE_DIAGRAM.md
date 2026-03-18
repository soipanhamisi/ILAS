# Service Layer Architecture

## Service Dependencies

```
┌─────────────────────────────────────────────────────────────┐
│                    CONTROLLER LAYER                          │
│                   (Not yet implemented)                      │
└──────────────┬──────────────────────┬────────────────────────┘
               │                      │
               │                      │
┌──────────────▼──────────────┐  ┌──▼──────────────────────────┐
│  InstructorExamService      │  │  StudentExamService          │
│  (Instructor Use Cases)     │  │  (Student Use Cases)         │
├─────────────────────────────┤  ├──────────────────────────────┤
│ • createAssessment()        │  │ • submitAssessment()         │
│ • issueGrade()              │  │ • getGradeAndFeedback()      │
│ • updateFeedback()          │  │ • getAllSubmissionsFor...()  │
│ • getSubmissionsForExam()   │  │ • getGradedSubmissions...()  │
│ • getUngradedSubmissions()  │  │ • getAvailableExams()        │
│ • getExamsForCourse()       │  │ • hasSubmitted()             │
└──────────────┬──────────────┘  └──┬───────────────────────────┘
               │                    │
               └──────────┬─────────┘
                          │
                ┌─────────▼─────────┐
                │ FileStorageService│
                ├───────────────────┤
                │ • storeFile()     │
                │ • deleteFile()    │
                │ • getLocation()   │
                └─────────┬─────────┘
                          │
        ┌─────────────────┴─────────────────┐
        │                                   │
┌───────▼────────────────────────────────┐  │
│        REPOSITORY LAYER                │  │
├────────────────────────────────────────┤  │
│ • ExamRepository                       │  │
│ • ExamSubmissionRepository             │  │
│ • StudentRepository                    │  │
│ • InstructorRepository                 │  │
│ • CourseRepository                     │  │
│ • EnrollmentRepository                 │  │
└────────────────┬───────────────────────┘  │
                 │                          │
                 │   ┌──────────────────────┘
┌────────────────▼───▼───────────────────┐
│         DATABASE (JPA/Hibernate)       │
├────────────────────────────────────────┤
│ Tables:                                │
│ • exams_tbl                            │
│ • exam_submissions_tbl (NEW)           │
│ • students_tbl                         │
│ • instructors_tbl                      │
│ • courses_tbl                          │
│ • enrollments_tbl                      │
└────────────────────────────────────────┘
```

## File System Integration

```
┌──────────────────────────────────────────────────────────┐
│              FileStorageService                          │
└──────────────────────┬───────────────────────────────────┘
                       │
                       │ stores/retrieves
                       │
┌──────────────────────▼───────────────────────────────────┐
│        File System (storage/exam-assessments/)           │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  exam_123_uuid_template.csv  ← Instructor creates       │
│  submission_student456_exam123_uuid.csv ← Student submits│
│  submission_student789_exam123_uuid.csv ← Student submits│
│                                                          │
└──────────────────────────────────────────────────────────┘
```

## Use Case Flow

### Instructor Creates Assessment
```
Instructor → InstructorExamService.createAssessment()
              ↓
         FileStorageService.storeFile(csv)
              ↓
         ExamRepository.save(exam)
              ↓
         Return Exam with CSV path
```

### Student Submits Assessment
```
Student → StudentExamService.submitAssessment()
           ↓
      Validate enrollment (EnrollmentRepository)
           ↓
      Check duplicate submission (ExamSubmissionRepository)
           ↓
      FileStorageService.storeFile(csv)
           ↓
      ExamSubmissionRepository.save(submission)
           ↓
      Return ExamSubmission
```

### Instructor Grades Submission
```
Instructor → InstructorExamService.issueGrade()
              ↓
         Find ExamSubmission (ExamSubmissionRepository)
              ↓
         Validate instructor owns course
              ↓
         Update grade, feedback, justification
              ↓
         ExamSubmissionRepository.save(submission)
              ↓
         Return updated ExamSubmission
```

### Student Views Grade and Feedback
```
Student → StudentExamService.getGradeAndFeedback()
           ↓
      ExamSubmissionRepository.findByStudent_StudentIdAndExam_ExamId()
           ↓
      Return ExamSubmission (with grade and feedback)
```

## Security & Validation

### InstructorExamService Validations
- ✓ Instructor exists
- ✓ Course exists
- ✓ Instructor owns the course
- ✓ No duplicate exam titles per course
- ✓ Grade within valid range (0 to maxScore)

### StudentExamService Validations
- ✓ Student exists
- ✓ Exam exists
- ✓ Student is enrolled in the course
- ✓ No duplicate submissions
- ✓ Submission exists before viewing grade

## Transaction Boundaries
All service methods that modify data are marked with `@Transactional`:
- createAssessment
- issueGrade
- updateFeedback
- submitAssessment

This ensures atomicity and consistency across repository operations.

