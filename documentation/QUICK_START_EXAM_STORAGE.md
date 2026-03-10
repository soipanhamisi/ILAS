# Quick Start Guide - Exam CSV Storage

## Setup Complete! ✅

Your exam bucket storage system is now configured and ready to use.

## What Was Configured

### 1. Configuration Files
- ✅ `application.properties` - Storage settings and file upload limits
- ✅ `FileStorageProperties.java` - Configuration bean for storage properties

### 2. Services Created
- ✅ `ExamFileStorageService` - Handles file storage operations
- ✅ `ExamCsvService` - Parses and processes CSV files
- ✅ `ExamService` - High-level exam management with CSV integration

### 3. Controller
- ✅ `ExamController` - REST API endpoints for exam operations

### 4. Storage Structure
- Storage Location: `./storage/exam-assessments/`
- Organized by course: `./storage/exam-assessments/course_{courseId}/`
- Files auto-named with timestamps and unique IDs

## Testing the Setup

### 1. Start Your Application
```bash
mvn spring-boot:run
```

### 2. Create an Exam with CSV (using Postman or curl)

**Using curl:**
```bash
curl -X POST "http://localhost:8080/api/exams?courseId=1&title=Midterm%20Exam&maxScore=100" \
  -F "csvFile=@example_exam_responses.csv"
```

**Using Postman:**
- Method: POST
- URL: `http://localhost:8080/api/exams?courseId=1&title=Midterm Exam&maxScore=100`
- Body: form-data
  - Key: `csvFile`
  - Type: File
  - Value: Select your CSV file

### 3. Get Exam Responses
```bash
curl http://localhost:8080/api/exams/1/responses
```

### 4. Calculate Student Score
```bash
curl "http://localhost:8080/api/exams/1/score?studentId=101"
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/exams?courseId={id}&title={title}&maxScore={score}` | Create exam with CSV |
| PUT | `/api/exams/{examId}/csv` | Update exam CSV file |
| GET | `/api/exams/course/{courseId}` | Get all exams for a course |
| GET | `/api/exams/{examId}` | Get exam by ID |
| GET | `/api/exams/{examId}/responses` | Get exam responses from CSV |
| GET | `/api/exams/{examId}/questions` | Get exam questions from CSV |
| GET | `/api/exams/{examId}/score?studentId={id}` | Calculate student score |
| DELETE | `/api/exams/{examId}` | Delete exam and CSV file |
| GET | `/api/exams/{examId}/has-csv` | Check if exam has CSV |

## CSV File Formats

### Exam Responses CSV
```csv
studentId,questionNumber,answer,score
101,1,Paris,10.0
101,2,1789,8.5
```

### Exam Questions CSV
```csv
questionNumber,questionText,correctAnswer,maxScore
1,What is the capital of France?,Paris,10.0
2,In what year did the French Revolution begin?,1789,10.0
```

## Example Files Provided

Two example CSV files have been created in your project root:
- `example_exam_responses.csv` - Sample student responses
- `example_exam_questions.csv` - Sample exam questions

Use these to test your implementation!

## File Organization

When you upload a CSV for exam ID 1 in course ID 5, it will be stored as:
```
./storage/exam-assessments/course_5/course_5_exam_1_20260309_143022_abc12345_yourfile.csv
```

## Configuration Options

Edit `application.properties` to customize:

```properties
# Change storage location
file.storage.base-path=./storage

# Change bucket name
file.storage.exam-bucket=exam-assessments

# Adjust file size limit
file.storage.max-file-size=10MB
spring.servlet.multipart.max-file-size=10MB
```

## Next Steps

1. **Test with example files**: Use the provided CSV examples to test uploads
2. **Create more endpoints**: Add endpoints for filtering, searching, etc.
3. **Add validation**: Implement more robust CSV validation
4. **Cloud storage**: Migrate to AWS S3 or similar when ready for production
5. **Security**: Add authentication and authorization to endpoints

## Troubleshooting

### Storage directory not created?
- The directory is created automatically when the application starts
- Check file permissions for your application directory

### File upload fails?
- Check file size (max 10MB by default)
- Ensure file is CSV format
- Verify course exists in database

### Cannot read CSV?
- Verify CSV format matches expected structure
- Check for proper comma separation
- Ensure header row is included

## Need Help?

Refer to `EXAM_STORAGE_README.md` for detailed documentation.

