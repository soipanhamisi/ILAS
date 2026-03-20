# ILAS Authentication - Quick Start Guide

## Prerequisites
- Java 17+ installed (Java 25 recommended)
- Node.js and npm installed
- Maven installed (or use ./mvnw)

## Quick Start

### 1. Start the Backend Server

```bash
# Navigate to project root
cd C:\Users\Admin\Documents\ILAS

# Set JAVA_HOME (if not already set)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.2"

# Start the Spring Boot application
.\mvnw spring-boot:run
```

The backend will start on **http://localhost:8080**

On startup, the DataSeeder will automatically:
- Create 2 instructor accounts
- Create 3 student accounts  
- Create 3 courses
- Create enrollments

**Console output will show:**
```
Starting database seeding...
Created 2 instructors: Dr. Smith, Prof. Johnson
Created 3 students: John Doe, Jane Smith, Alice Johnson
Database seeding completed successfully!
```

### 2. Start the Frontend Server

```bash
# In a new terminal, navigate to frontend
cd C:\Users\Admin\Documents\ILAS\frontend

# Install dependencies (if not done already)
npm install

# Start development server
npm run dev
```

The frontend will start on **http://localhost:5173**

### 3. Access the Application

1. Open your browser to **http://localhost:5173**
2. You'll see the ILAS login page with:
   - Toggle between **Login** and **Sign Up**
   - Demo credentials displayed

### 4. Test with Demo Credentials

#### Login as Instructor
- Username: `smith_instructor`
- Password: `password123`
- Role: **Instructor**

Then you can:
- View instructor dashboard
- Create new exams
- Grade student submissions

#### Login as Student
- Username: `john_student`
- Password: `password123`
- Role: **Student**

Then you can:
- View student dashboard
- Take available exams
- View grades and feedback

### 5. Create New Account

1. Click **Sign Up** tab
2. Fill in:
   - User Type: **Instructor** or **Student**
   - Full Name: Your name
   - Email: Your email address
   - Username: Choose a username
   - Password: Choose a password
3. Click **Sign Up**
4. You'll be logged in and redirected to your dashboard

## API Endpoints

### Authentication Endpoints
```
POST /api/auth/login
{
  "username": "smith_instructor",
  "password": "password123",
  "userType": "instructor"
}

POST /api/auth/signup
{
  "name": "Dr. New Professor",
  "email": "newprof@university.edu",
  "username": "newprof_instructor",
  "password": "password123",
  "userType": "instructor"
}
```

### Response Format
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": 1,
    "name": "Dr. Smith",
    "username": "smith_instructor",
    "email": "dr.smith@university.edu",
    "userType": "instructor",
    "token": "uuid-token"
  }
}
```

## Database

The application uses an in-memory or file-based database (configured in `application.properties`).

### Sample Data Created on Startup

**Instructors Table:**
| ID | Name | Email | Username | Password |
|---|---|---|---|---|
| 1 | Dr. Smith | dr.smith@university.edu | smith_instructor | password123 |
| 2 | Prof. Johnson | prof.johnson@university.edu | johnson_instructor | password123 |

**Students Table:**
| ID | Name | Email | Username | Password |
|---|---|---|---|---|
| 1 | John Doe | john.doe@student.edu | john_student | password123 |
| 2 | Jane Smith | jane.smith@student.edu | jane_student | password123 |
| 3 | Alice Johnson | alice.johnson@student.edu | alice_student | password123 |

## Troubleshooting

### Backend won't start
- Check JAVA_HOME is set correctly
- Make sure port 8080 is not in use
- Run `.\mvnw clean install` first

### Frontend won't start
- Make sure Node.js is installed: `node --version`
- Run `npm install` in the frontend directory
- Make sure port 5173 is not in use

### Can't login
- Make sure backend is running on http://localhost:8080
- Check the browser console (F12) for error messages
- Verify you're using correct username and password
- Make sure you selected the correct User Type (Instructor/Student)

### CORS Errors
- Backend has CORS enabled for all origins in AuthController
- Check that API calls are going to `http://localhost:8080/api`

## File Structure

```
ILAS/
├── src/main/java/org/soipan/ilas/
│   ├── controllers/
│   │   ├── AuthController.java          (NEW)
│   │   ├── InstructorExamController.java
│   │   └── StudentExamController.java
│   ├── services/
│   │   ├── AuthService.java             (NEW)
│   │   ├── InstructorExamService.java
│   │   └── StudentExamService.java
│   ├── config/
│   │   └── DataSeeder.java              (NEW)
│   ├── dto/
│   │   ├── AuthRequest.java             (NEW)
│   │   ├── AuthResponse.java            (NEW)
│   │   ├── SignupRequest.java           (NEW)
│   │   └── [other DTOs]
│   ├── models/
│   │   ├── Student.java                 (MODIFIED)
│   │   ├── Instructor.java              (MODIFIED)
│   │   └── [other models]
│   └── repository/
│       ├── StudentRepository.java       (MODIFIED)
│       ├── InstructorRepository.java    (MODIFIED)
│       └── [other repositories]
│
├── frontend/src/
│   ├── stores/
│   │   └── auth.js                      (MODIFIED)
│   ├── services/
│   │   └── api.js                       (MODIFIED)
│   ├── views/
│   │   └── Login.vue                    (MODIFIED)
│   └── [other frontend files]
│
└── pom.xml
    └── application.properties
```

## Next Steps

1. ✅ **Authentication is implemented** - Users can login and signup
2. ✅ **Database is seeded** - Demo users are created automatically
3. ✅ **Frontend is updated** - New login UI with signup support
4. 🔄 **Security considerations**:
   - Implement password hashing (bcrypt) instead of plain text
   - Use JWT tokens instead of UUIDs
   - Add HTTPS/TLS for production
   - Implement refresh tokens
   - Add rate limiting on auth endpoints

## Support

For issues or questions, check:
1. The backend console for error messages
2. The browser console (F12) for frontend errors
3. `AUTH_IMPLEMENTATION_SUMMARY.md` for more details

