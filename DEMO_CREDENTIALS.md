# 🎓 ILAS - Demo User Credentials

## Quick Reference Card

### Instructors (2)

#### Instructor 1: Dr. Smith
```
Username: smith_instructor
Password: password123
Email: dr.smith@university.edu
Name: Dr. Smith
Type: Instructor
```

#### Instructor 2: Prof. Johnson
```
Username: johnson_instructor
Password: password123
Email: prof.johnson@university.edu
Name: Prof. Johnson
Type: Instructor
```

---

### Students (3)

#### Student 1: John Doe
```
Username: john_student
Password: password123
Email: john.doe@student.edu
Name: John Doe
Type: Student
```

#### Student 2: Jane Smith
```
Username: jane_student
Password: password123
Email: jane.smith@student.edu
Name: Jane Smith
Type: Student
```

#### Student 3: Alice Johnson
```
Username: alice_student
Password: password123
Email: alice.johnson@student.edu
Name: Alice Johnson
Type: Student
```

---

## How to Test

1. **Start Backend**
   ```bash
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.2"
   cd C:\Users\Admin\Documents\ILAS
   .\mvnw spring-boot:run
   ```

2. **Start Frontend**
   ```bash
   cd C:\Users\Admin\Documents\ILAS\frontend
   npm run dev
   ```

3. **Open Browser**
   - Navigate to: http://localhost:5173

4. **Login**
   - Select user type (Instructor or Student)
   - Enter username and password from above
   - Click "Login"

5. **Explore**
   - Access the dashboard corresponding to your role
   - Create exams (as Instructor)
   - Take exams (as Student)
   - Grade submissions (as Instructor)

---

## Courses Automatically Enrolled

### Course 1: Introduction to Java (101)
- **Enrolled Students:** John Doe, Jane Smith
- **Instructor:** Dr. Smith

### Course 2: Data Structures (102)
- **Enrolled Students:** Alice Johnson
- **Instructor:** Prof. Johnson

### Course 3: Web Development (103)
- **Enrolled Students:** John Doe
- **Instructor:** Dr. Smith

---

## Testing Workflows

### As Instructor (smith_instructor)
1. ✅ Login to dashboard
2. ✅ Create a new exam
3. ✅ View course exams
4. ✅ See student submissions
5. ✅ Grade submissions
6. ✅ Provide feedback

### As Student (john_student)
1. ✅ Login to dashboard
2. ✅ See available exams
3. ✅ Take an exam (upload CSV response)
4. ✅ View submitted exams
5. ✅ Check grades and feedback
6. ✅ Monitor grades

---

## Key Features to Try

### Login Page
- ✅ Toggle between Login and Sign Up
- ✅ See demo credentials displayed
- ✅ Login with existing accounts
- ✅ Create new accounts via Sign Up

### Dashboard (Role-Based)
- **Instructor:** Create exams, manage submissions, grade papers
- **Student:** Take exams, view grades, receive feedback

### UI Design
- ✅ Glassmorphic design with warm palette
- ✅ Responsive layout
- ✅ Smooth animations
- ✅ Clear error messages
- ✅ Loading indicators

---

## API Test URLs

### Login Endpoint
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "smith_instructor",
  "password": "password123",
  "userType": "instructor"
}
```

### Signup Endpoint
```
POST http://localhost:8080/api/auth/signup
Content-Type: application/json

{
  "name": "New Teacher",
  "email": "newteacher@university.edu",
  "username": "newteacher_instructor",
  "password": "password123",
  "userType": "instructor"
}
```

---

## Troubleshooting

### "Invalid username or password"
- Check that you've entered the username correctly (case-sensitive)
- Verify you selected the correct User Type (Instructor/Student)
- Use one of the demo credentials above

### "Username already exists"
- The username is taken, create a new account with a different username

### "Network error" / Can't login
- Make sure backend is running on http://localhost:8080
- Check that frontend can reach http://localhost:8080/api/auth/login

### Database Issues
- The database is auto-seeded on first startup
- If issues persist, delete the database file and restart

---

## Support Resources

📖 **Documentation:**
- `AUTH_IMPLEMENTATION_SUMMARY.md` - Technical overview
- `QUICK_START_AUTH.md` - Step-by-step guide
- `IMPLEMENTATION_COMPLETE.md` - Completion report

💾 **Code Location:**
- Backend: `/src/main/java/org/soipan/ilas/`
- Frontend: `/frontend/src/`

🔗 **API Docs:**
- Backend API runs on: http://localhost:8080
- Frontend runs on: http://localhost:5173

---

## Password Policy

⚠️ **Development Only:**
- All demo users use `password123`
- Passwords are stored in plain text (development)

✅ **Production Recommendation:**
- Implement bcrypt password hashing
- Enforce strong password requirements
- Add password reset functionality
- Implement two-factor authentication

---

**Ready to get started? Follow the instructions in QUICK_START_AUTH.md! 🚀**

