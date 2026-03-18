# 🚀 COMPLETE APPLICATION - SETUP & RUN GUIDE

## Quick Start Guide for ILAS Full-Stack Application

This guide will help you get the complete ILAS (Integrated Learning Assessment System) up and running on your machine.

---

## 📋 Prerequisites

### Required Software
- ✅ **Java JDK 17+** - For Spring Boot backend
- ✅ **Node.js 16+** - For Vue.js frontend
- ✅ **MySQL or PostgreSQL** - Database
- ✅ **Git** (optional) - For version control

### Verify Installation
```bash
# Check Java
java -version

# Check Node.js
node --version

# Check npm
npm --version

# Check MySQL
mysql --version
```

---

## 🗄️ Step 1: Database Setup

### Option A: MySQL
```sql
-- Create database
CREATE DATABASE ilas_db;

-- Create user (optional)
CREATE USER 'ilas_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON ilas_db.* TO 'ilas_user'@'localhost';
FLUSH PRIVILEGES;
```

### Option B: PostgreSQL
```sql
-- Create database
CREATE DATABASE ilas_db;

-- Create user (optional)
CREATE USER ilas_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE ilas_db TO ilas_user;
```

---

## ⚙️ Step 2: Configure Backend

### 1. Create `.env` file
Navigate to project root and create `.env`:

```bash
cd C:\Users\Admin\Documents\ILAS
```

Create file `C:\Users\Admin\Documents\ILAS\.env`:
```properties
# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/ilas_db
DB_USERNAME=root
DB_PASSWORD=your_password

# For PostgreSQL, use:
# DB_URL=jdbc:postgresql://localhost:5432/ilas_db
```

### 2. Verify application.properties
File: `src/main/resources/application.properties`

Should contain:
```properties
spring.config.import=file:.env[.properties]

spring.application.name=ILAS
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=true

# File upload settings
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
file.storage.upload-dir=storage/exam-assessments
```

---

## 🚀 Step 3: Start Backend

### Option A: Using Maven Wrapper (Recommended)
```bash
cd C:\Users\Admin\Documents\ILAS

# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### Option B: Using IDE (IntelliJ IDEA)
1. Open project in IntelliJ
2. Wait for dependencies to download
3. Right-click `IlasApplication.java`
4. Select "Run 'IlasApplication'"

### Verify Backend is Running
- Console should show: `Started IlasApplication in X seconds`
- Access: `http://localhost:8080/api`
- Tables should be auto-created in database

---

## 🎨 Step 4: Start Frontend

### 1. Install Dependencies
```bash
cd C:\Users\Admin\Documents\ILAS\frontend

# Install npm packages
npm install
```

### 2. Start Development Server
```bash
# Start Vite dev server
npm run dev
```

### Verify Frontend is Running
- Console should show: `Local: http://localhost:3000`
- Browser should open automatically
- Or manually open: `http://localhost:3000`

---

## ✅ Step 5: Test the Application

### Test Backend API
Open browser or use curl:

```bash
# Test health (optional endpoint)
curl http://localhost:8080/api

# Backend is ready if no errors
```

### Test Frontend
1. Open: `http://localhost:3000`
2. You should see the landing page
3. Click "Get Started" → Login page

---

## 👥 Step 6: Login & Test Features

### Demo Credentials

#### Instructor Login
```
User Type: Instructor
ID: 1
Name: Dr. Smith
```

#### Student Login
```
User Type: Student
ID: 5
Name: John Doe
```

**Note:** These IDs should exist in your database. If not, add them manually or through the application.

---

## 🧪 Complete Test Workflow

### Test Flow 1: Instructor Creates Exam

1. **Login as Instructor**
   - Navigate to `http://localhost:3000/login`
   - Select "Instructor"
   - ID: 1, Name: Dr. Smith
   - Click Login

2. **Create Exam**
   - Click "Create Exam" in navigation
   - Select Course: "Introduction to Java (101)"
   - Exam Title: "Midterm Exam"
   - Max Score: 100
   - Upload CSV file (exam_template.csv)
   - Click "Create Exam"

3. **Verify Creation**
   - Should see success message
   - Redirected to dashboard
   - Exam appears in list

### Test Flow 2: Student Takes Exam

1. **Logout** (click Logout button)

2. **Login as Student**
   - Select "Student"
   - ID: 5, Name: John Doe
   - Click Login

3. **View Available Exams**
   - Dashboard shows available exams
   - Click "Take Exam" on created exam

4. **Submit Exam**
   - Upload completed CSV file
   - Click "Submit Exam"
   - Should see success message

### Test Flow 3: Instructor Grades

1. **Logout and Login as Instructor** (ID: 1)

2. **View Submissions**
   - Click "View Submissions" on exam
   - See student submission

3. **Grade Submission**
   - Click "Grade" button
   - Enter grade: 85
   - Feedback: "Excellent work!"
   - Justification: "Strong understanding"
   - Click "Submit Grade"

4. **Verify Grade**
   - Grade appears in submission card
   - Status changes to "Graded"

### Test Flow 4: Student Views Results

1. **Logout and Login as Student** (ID: 5)

2. **View Results**
   - Click "View Results" on graded submission
   - See grade: 85/100 (85%)
   - Read feedback and justification
   - Performance indicator shows

---

## 📁 Sample CSV Files

### exam_template.csv (Instructor Creates)
```csv
question,learnerResponses,maxGrade,grade
What is 2+2?,,[10],
Define OOP?,,[20],
Explain inheritance?,,[30],
What is polymorphism?,,[20],
List SOLID principles?,,[20],
```

### completed_exam.csv (Student Submits)
```csv
question,learnerResponses,maxGrade,grade
What is 2+2?,4,[10],
Define OOP?,Object-oriented programming is a programming paradigm,[20],
Explain inheritance?,Inheritance allows classes to inherit properties,[30],
What is polymorphism?,Polymorphism allows objects to take many forms,[20],
List SOLID principles?,Single Responsibility Open/Closed Liskov,[20],
```

Save these files to test file upload functionality.

---

## 🐛 Troubleshooting

### Backend Issues

#### Issue: Port 8080 already in use
```bash
# Find and kill process on port 8080 (Windows)
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

#### Issue: Database connection failed
- Verify database is running
- Check credentials in `.env` file
- Verify database exists
- Check `DB_URL` format

#### Issue: Tables not created
- Check `spring.jpa.hibernate.ddl-auto=update` in properties
- Verify database user has CREATE privileges
- Check console for errors

### Frontend Issues

#### Issue: Port 3000 already in use
```bash
# Kill process on port 3000 (Windows)
netstat -ano | findstr :3000
taskkill /PID <PID> /F
```

#### Issue: npm install fails
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -rf node_modules
npm install
```

#### Issue: API calls fail (CORS)
- Verify backend is running on port 8080
- Check `@CrossOrigin` annotations in controllers
- Verify proxy configuration in `vite.config.js`

#### Issue: File upload fails
- Check file size (max 10MB)
- Verify file is CSV format
- Check backend console for errors
- Verify storage directory exists

---

## 📊 System Health Check

### Backend Health
```bash
# Check if backend is running
curl http://localhost:8080/api/instructor/exams/courses/101?instructorId=1

# Should return JSON response (even if empty data)
```

### Frontend Health
```bash
# Check if frontend is running
curl http://localhost:3000

# Should return HTML
```

### Database Health
```sql
-- Check tables exist
SHOW TABLES;

-- Check data
SELECT * FROM students_tbl;
SELECT * FROM instructors_tbl;
SELECT * FROM courses_tbl;
SELECT * FROM exams_tbl;
```

---

## 🔧 Development Tips

### Hot Reload
- **Frontend:** Changes auto-reload (Vite HMR)
- **Backend:** Restart required (or use Spring DevTools)

### Debugging

#### Backend Debugging
1. Add breakpoints in IntelliJ
2. Run in Debug mode
3. Check console logs

#### Frontend Debugging
1. Open browser DevTools (F12)
2. Check Console tab for errors
3. Check Network tab for API calls
4. Use Vue DevTools extension

### Logs
- **Backend logs:** Console output
- **Frontend logs:** Browser console
- **Network logs:** Browser DevTools Network tab

---

## 📦 Building for Production

### Build Backend
```bash
cd C:\Users\Admin\Documents\ILAS

# Create JAR file
.\mvnw.cmd clean package

# JAR file location: target/ILAS-0.0.1-SNAPSHOT.jar
```

### Build Frontend
```bash
cd C:\Users\Admin\Documents\ILAS\frontend

# Create production build
npm run build

# Output: dist/ folder
```

### Deploy
1. Deploy JAR to server
2. Upload dist/ folder to web server
3. Configure environment variables
4. Set up production database
5. Configure CORS for production domain

---

## 🎯 Quick Commands Reference

### Start Application
```bash
# Terminal 1: Backend
cd C:\Users\Admin\Documents\ILAS
.\mvnw.cmd spring-boot:run

# Terminal 2: Frontend
cd C:\Users\Admin\Documents\ILAS\frontend
npm run dev
```

### Stop Application
```bash
# Stop backend: Ctrl+C in terminal
# Stop frontend: Ctrl+C in terminal
```

### Rebuild
```bash
# Backend: mvnw clean install
# Frontend: npm run build
```

---

## 📚 Documentation Reference

- **API Documentation:** `API_DOCUMENTATION.md`
- **Frontend Guide:** `frontend/README.md`
- **Service Layer:** `SERVICE_LAYER_DOCUMENTATION.md`
- **Controller Guide:** `CONTROLLER_IMPLEMENTATION_GUIDE.md`
- **Complete Summary:** `FRONTEND_COMPLETE_SUMMARY.md`

---

## ✅ Final Checklist

Before testing, ensure:
- [ ] Database created and running
- [ ] `.env` file configured
- [ ] Backend dependencies installed
- [ ] Frontend dependencies installed (`npm install`)
- [ ] Backend running on port 8080
- [ ] Frontend running on port 3000
- [ ] Sample CSV files ready
- [ ] Demo users exist in database

---

## 🎉 Success!

If everything is working:
- ✅ Backend API responds
- ✅ Frontend loads
- ✅ Login works
- ✅ Can create exams
- ✅ Can submit exams
- ✅ Can grade exams
- ✅ Can view results

**Congratulations! Your full-stack LMS is running!** 🚀

---

## 🆘 Need Help?

1. Check console errors (backend and frontend)
2. Verify all services are running
3. Check database connection
4. Review documentation files
5. Check network tab in browser DevTools

---

**Last Updated:** March 10, 2026  
**Version:** 1.0.0  
**Status:** ✅ Production Ready

