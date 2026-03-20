# 🚀 How to Run ILAS with Authentication

## Prerequisites Check

Before starting, ensure you have:
- ✅ Java installed (Java 17+) - Type: `java -version`
- ✅ Node.js installed - Type: `node --version`
- ✅ npm installed - Type: `npm --version`

---

## Step 1: Start the Backend Server

Open a **PowerShell terminal** and run:

```powershell
# Navigate to project root
cd C:\Users\Admin\Documents\ILAS

# Set Java environment
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.2"

# Start the Spring Boot application
.\mvnw spring-boot:run
```

### Expected Output:
```
[INFO] Building ILAS 0.0.1-SNAPSHOT
[INFO] Spring Boot Application started...
[INFO] Application 'ILAS' is running on port 8080

Starting database seeding...
Created 2 instructors: Dr. Smith, Prof. Johnson
Created 3 students: John Doe, Jane Smith, Alice Johnson
Database seeding completed successfully!
```

### ✅ Backend is Ready When:
- You see: `Application ready to serve requests`
- No errors in console
- Port 8080 is listening
- Database is seeded

**Don't close this terminal! Keep it running.**

---

## Step 2: Start the Frontend Server

Open a **NEW PowerShell terminal** and run:

```powershell
# Navigate to frontend directory
cd C:\Users\Admin\Documents\ILAS\frontend

# Install dependencies (first time only)
npm install

# Start development server
npm run dev
```

### Expected Output:
```
  VITE v5.4.21  ready in 234 ms

  ➜  Local:   http://localhost:5173/
  ➜  press h to show help
```

### ✅ Frontend is Ready When:
- You see the local URL
- No build errors
- Vite server is running

**Don't close this terminal either!**

---

## Step 3: Open the Application

1. Open your web browser (Chrome, Edge, Firefox, Safari)
2. Navigate to: **http://localhost:5173**
3. You should see the ILAS login page

---

## Step 4: Login or Signup

### Option A: Login with Demo Account

1. Click the **Login** tab (should be default)
2. Select User Type: **Instructor** or **Student**
3. Enter credentials (examples below)
4. Click **Login**

**Example - Login as Instructor:**
```
User Type: Instructor
Username: smith_instructor
Password: password123
```

### Option B: Create New Account

1. Click the **Sign Up** tab
2. Select User Type: **Instructor** or **Student**
3. Fill in:
   - Full Name: Your name
   - Email: your.email@example.com
   - Username: yourname_student (or _instructor)
   - Password: Your password
4. Click **Sign Up**

---

## Step 5: Use the Platform

Once logged in, you'll see your role-specific dashboard:

### As Instructor (smith_instructor):
- 📊 View instructor dashboard
- ➕ Create new exams
- 📝 Upload exam templates (CSV)
- 📋 View student submissions
- ✏️ Grade submissions
- 💬 Provide feedback

### As Student (john_student):
- 📊 View student dashboard
- 📚 See available exams
- 📝 Take exams (upload CSV responses)
- 📊 View grades
- 💬 Read instructor feedback

---

## Demo Credentials Quick Reference

| Role | Username | Password | Name |
|------|----------|----------|------|
| 👨‍🏫 Instructor | smith_instructor | password123 | Dr. Smith |
| 👨‍🏫 Instructor | johnson_instructor | password123 | Prof. Johnson |
| 👨‍🎓 Student | john_student | password123 | John Doe |
| 👩‍🎓 Student | jane_student | password123 | Jane Smith |
| 👩‍🎓 Student | alice_student | password123 | Alice Johnson |

---

## Stopping the Servers

### Stop Backend:
- In the backend terminal, press: **Ctrl + C**
- Confirm: **Y** (if prompted)

### Stop Frontend:
- In the frontend terminal, press: **Ctrl + C**
- Confirm: **Y** (if prompted)

---

## Restarting

To run again, simply repeat Steps 1 and 2 above.

The database will persist, so your data will still be there.

---

## Troubleshooting

### "Backend won't start"
```
Solution:
1. Make sure port 8080 is not in use
2. Check JAVA_HOME is set correctly
3. Try: .\mvnw clean install
```

### "Frontend won't start"
```
Solution:
1. Make sure port 5173 is not in use
2. Delete node_modules folder
3. Run: npm install
4. Run: npm run dev
```

### "Can't login / Network error"
```
Solution:
1. Check backend is running (look for 8080 in console)
2. Check frontend is running (look for 5173 in console)
3. Try: http://localhost:5173 (not 127.0.0.1)
4. Check browser console (F12) for errors
```

### "Wrong password error"
```
Solution:
Check credentials carefully:
- Username is case-sensitive
- Use: smith_instructor (underscore between name and role)
- Password: password123 (exactly)
- Select correct User Type (Instructor or Student)
```

### "Database issues"
```
Solution:
If seeding didn't work:
1. Stop backend (Ctrl + C)
2. Look for database file and delete it
3. Restart backend
4. Check console for seeding messages
```

---

## Browser Console Debugging

If you encounter issues, check browser console for errors:

1. Press **F12** to open Developer Tools
2. Click **Console** tab
3. Look for red error messages
4. Screenshot the error
5. Check QUICK_START_AUTH.md for that specific error

---

## API Testing (Optional)

If you want to test the API directly:

### Test Login Endpoint:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "smith_instructor",
    "password": "password123",
    "userType": "instructor"
  }'
```

### Test Signup Endpoint:
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Teacher",
    "email": "newteacher@example.edu",
    "username": "newteacher_inst",
    "password": "password123",
    "userType": "instructor"
  }'
```

---

## What to Try

### Instructor Workflow:
1. ✅ Login as smith_instructor
2. ✅ Go to Instructor Dashboard
3. ✅ Create a new exam
4. ✅ Upload exam questions (CSV template)
5. ✅ View created exams
6. ✅ Wait for student submissions
7. ✅ Grade submissions (use other users)

### Student Workflow:
1. ✅ Signup as a new student
2. ✅ Go to Student Dashboard
3. ✅ View available exams
4. ✅ Take an exam (upload CSV response)
5. ✅ View submitted exams
6. ✅ Check grades (once graded by instructor)

---

## Files Reference

### Important Locations:
- **Backend**: `C:\Users\Admin\Documents\ILAS`
- **Frontend**: `C:\Users\Admin\Documents\ILAS\frontend`
- **Backend running on**: http://localhost:8080
- **Frontend running on**: http://localhost:5173
- **Database auto-seeded**: On first startup

### Documentation Files:
- `DEMO_CREDENTIALS.md` - All demo accounts
- `QUICK_START_AUTH.md` - Detailed guide
- `AUTH_IMPLEMENTATION_SUMMARY.md` - Technical details

---

## Success Indicators ✅

You'll know everything is working when:

1. ✅ Backend console shows "seeding completed"
2. ✅ Frontend loads with login page
3. ✅ Login page shows demo credentials
4. ✅ Can toggle between Login and Sign Up
5. ✅ Can login with demo credentials
6. ✅ Redirected to appropriate dashboard
7. ✅ Can create new accounts
8. ✅ Can access role-specific features

---

## Need Help?

1. **Check Console Output**
   - Backend terminal: Look for errors
   - Browser Console (F12): Check for JS errors

2. **Read Documentation**
   - QUICK_START_AUTH.md has troubleshooting
   - DEMO_CREDENTIALS.md has all credentials
   - AUTH_IMPLEMENTATION_SUMMARY.md has technical details

3. **Common Issues**
   - Port already in use: Kill process using port 8080/5173
   - Java not found: Set JAVA_HOME correctly
   - npm not found: Install Node.js
   - Backend not responding: Check if running on port 8080

---

## Summary

```
BACKEND:
  cd C:\Users\Admin\Documents\ILAS
  $env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.2"
  .\mvnw spring-boot:run

FRONTEND:
  cd C:\Users\Admin\Documents\ILAS\frontend
  npm run dev

OPEN:
  http://localhost:5173

LOGIN:
  username: smith_instructor
  password: password123
  userType: Instructor
```

**That's it! Enjoy ILAS! 🎓**

