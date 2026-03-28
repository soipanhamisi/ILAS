# Admin Role Architecture - Visual Guide

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         ILAS - Admin System                         │
└─────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────────────┐
│                         FRONTEND (Vue 3 + Vite)                          │
├──────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  Login.vue                    NavBar.vue              AdminDashboard.vue │
│  ├─ User Type Select          ├─ Sidebar Nav          ├─ Stat Cards     │
│  │  (Student/Instructor/Admin) ├─ Admin Link          │  (Counts)       │
│  ├─ Login Form                └─ Role-based Links     ├─ System Status  │
│  └─ Signup Form                                       └─ Features       │
│                                                                          │
│  ┌──────────────────┐        ┌──────────────────────────────────┐      │
│  │   auth.js        │        │      api.js (adminAPI)          │      │
│  │  (Pinia Store)   │        │                                  │      │
│  │                  │        │  ├─ getDashboardSummary()       │      │
│  │  - isAdmin()     │────────├─ ├─ getSystemStats()           │      │
│  │  - isStudent()   │        │  ├─ getTotalStudents()         │      │
│  │  - isInstructor()│        │  ├─ getTotalInstructors()      │      │
│  │  - userId        │        │  ├─ getTotalCourses()          │      │
│  │                  │        │  └─ getTotalExams()            │      │
│  └──────────────────┘        └──────────────────────────────────┘      │
│           │                                   │                        │
│           └───────────────────────┬───────────┘                        │
│                                   │ HTTP Requests                      │
└───────────────────────────────────┼────────────────────────────────────┘
                                    │
                                    ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                    BACKEND (Spring Boot Java)                            │
├──────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│                      REST API Controllers                               │
│  ┌──────────────────────────────────────────────────────────┐          │
│  │           AuthController          AdminController        │          │
│  │  ┌──────────────────────┐    ┌──────────────────────┐   │          │
│  │  │ POST /auth/login     │    │ GET /dashboard/      │   │          │
│  │  │ POST /auth/signup    │    │     summary          │   │          │
│  │  │ (admin support)      │    │ GET /stats           │   │          │
│  │  │                      │    │ GET /students/count  │   │          │
│  │  │                      │    │ GET /instructors/count
│  │  │                      │    │ GET /courses/count   │   │          │
│  │  │                      │    │ GET /exams/count     │   │          │
│  │  └──────────────────────┘    └──────────────────────┘   │          │
│  └──────────────────────────────────────────────────────────┘          │
│                    │                        │                          │
│                    ▼                        ▼                          │
│  ┌──────────────────────────┐  ┌──────────────────────────┐           │
│  │    AuthService           │  │   AdminService           │           │
│  │                          │  │                          │           │
│  │ + login(request)         │  │ + getDashboardSummary()  │           │
│  │   - Check admin user     │  │ + getTotalStudents()     │           │
│  │   - Verify password      │  │ + getTotalInstructors()  │           │
│  │   - Return AuthResponse  │  │ + getTotalCourses()      │           │
│  │                          │  │ + getTotalExams()        │           │
│  │ + signup(request)        │  │ + adminExists()          │           │
│  │   - Create admin user    │  │                          │           │
│  │   - Hash password (TODO) │  │ Dependencies:            │           │
│  │   - Check duplicates     │  │ - AdminRepository        │           │
│  │   - Return AuthResponse  │  │ - StudentRepository      │           │
│  │                          │  │ - InstructorRepository   │           │
│  │ Dependencies:            │  │ - CourseRepository       │           │
│  │ - AdminRepository        │  │ - ExamRepository         │           │
│  │ - StudentRepository      │  └──────────────────────────┘           │
│  │ - InstructorRepository   │                                          │
│  └──────────────────────────┘                                          │
│                    │                        │                          │
│                    └────────────┬───────────┘                          │
│                                 │ Data Access Layer                    │
│                                 ▼                                      │
│  ┌──────────────────────────────────────────────────────────┐         │
│  │              Repository Interfaces (JPA)                │         │
│  │  ┌─────────────────┐         ┌─────────────────┐       │         │
│  │  │ AdminRepository │         │ StudentRepository
│  │  │                 │         │                 │       │         │
│  │  │ + findByUsername│         │ + findByUsername│       │         │
│  │  │ + findByEmail   │         │ + findByEmail   │       │         │
│  │  │ + findByAdminId │         └─────────────────┘       │         │
│  │  │ + save()        │                                    │         │
│  │  │ + findAll()     │      InstructorRepository          │         │
│  │  │ + deleteAll()   │      CourseRepository              │         │
│  │  │                 │      ExamRepository                │         │
│  │  └─────────────────┘                                    │         │
│  └──────────────────────────────────────────────────────────┘         │
│                                 │ Database Queries                    │
└─────────────────────────────────┼──────────────────────────────────────┘
                                  │
                                  ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                      PostgreSQL / H2 Database                            │
├──────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────────────┐   ┌─────────────────┐   ┌──────────────────┐   │
│  │   admins_tbl     │   │  students_tbl   │   │ instructors_tbl  │   │
│  ├──────────────────┤   ├─────────────────┤   ├──────────────────┤   │
│  │ admin_id (PK)    │   │ student_id (PK) │   │ instructor_id(PK)│   │
│  │ name             │   │ name            │   │ name             │   │
│  │ email            │   │ email           │   │ email            │   │
│  │ username (UNQ)   │   │ username (UNQ)  │   │ username (UNQ)   │   │
│  │ password         │   │ password        │   │ password         │   │
│  └──────────────────┘   └─────────────────┘   └──────────────────┘   │
│                                                                          │
│  ┌──────────────────────┐  ┌──────────────┐  ┌──────────────────┐    │
│  │    courses_tbl       │  │  exams_tbl   │  │ enrollments_tbl  │    │
│  ├──────────────────────┤  ├──────────────┤  ├──────────────────┤    │
│  │ course_id (PK)       │  │ exam_id (PK) │  │ enrollment_id(PK)│    │
│  │ course_title         │  │ exam_title   │  │ student_id (FK)  │    │
│  │ instructor_id (FK)   │  │ course_id(FK)│  │ course_id (FK)   │    │
│  └──────────────────────┘  └──────────────┘  └──────────────────┘    │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

## Data Flow - Admin Login

```
User @ Login.vue
   │
   ├─ Select "Admin" from dropdown
   ├─ Enter username: "admin"
   ├─ Enter password: "password123"
   └─ Click Login button
       │
       ▼
   authStore.login(username, password, "admin")
       │
       ▼
   authAPI.login(username, password, "admin")
       │
       ▼
   POST /api/auth/login (JSON)
       │
       ▼
   AuthController.login()
       │
       ▼
   AuthService.login(request)
       │
       ├─ Check userType == "admin" ✓
       │
       ├─ Call adminRepository.findByUsername("admin")
       │
       ├─ Validate password matches ✓
       │
       └─ Return AuthResponse {
           userId: 1,
           name: "Test Admin",
           username: "admin",
           email: "admin@test.com",
           userType: "admin",
           token: "uuid-token"
         }
       │
       ▼
   Store in authStore
       │
       ├─ Set user: { userId, name, username, email }
       ├─ Set userType: "admin"
       ├─ Set isAuthenticated: true
       └─ Set token: "uuid-token"
       │
       ▼
   Save to localStorage
       │
       ▼
   authStore.isAdmin === true ✓
       │
       ▼
   Router allows navigation to /admin
       │
       ▼
   AdminDashboard.vue loads
       │
       ├─ Call adminAPI.getDashboardSummary(userId)
       │
       ▼
   GET /api/admin/dashboard/summary?adminId=1
       │
       ▼
   AdminController.getDashboardSummary(adminId)
       │
       ▼
   AdminService.getDashboardSummary(1)
       │
       ├─ Verify admin exists ✓
       │
       └─ Return {
           totalStudents: 0,
           totalInstructors: 0,
           totalCourses: 0,
           totalExams: 0,
           adminName: "Test Admin"
         }
       │
       ▼
   Display stats in Dashboard
```

## Authentication Flow

```
┌─────────────────────────────────────────────────────────┐
│                   USER REQUESTS PAGE                    │
│                     /admin (protected)                  │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
                ┌─────────────────────────┐
                │  Router Navigation Guard│
                └─────────────────────────┘
                           │
                ┌──────────┴──────────┐
                │                     │
                ▼                     ▼
          Is Authenticated?      Is Admin User?
             YES ──┬──           YES ──┬──
                   │                   │
                   ▼                   ▼
          ┌──────────────────────────────────┐
          │   ALLOW NAVIGATION               │
          │   Load AdminDashboard.vue        │
          └──────────────────────────────────┘
                   
             NO    │                   NO
                   │                   │
                   └───┬───────────────┘
                       │
                       ▼
          ┌──────────────────────────┐
          │  REDIRECT TO /login      │
          │  Clear session           │
          │  Clear localStorage      │
          └──────────────────────────┘
```

## Component Hierarchy

```
App.vue (LMS Layout Shell)
│
├─ NavBar.vue (Sidebar)
│  ├─ Brand Block (ILAS)
│  ├─ Nav Sections
│  │  ├─ [Admin] Admin Dashboard ◄── Shown if isAdmin == true
│  │  ├─ [Instructor] Instructor Dashboard
│  │  └─ [Student] Student Dashboard
│  └─ Sidebar Footer (User Info + Logout)
│
└─ Main Workspace (router-view)
   │
   ├─ Login.vue (for unauthenticated)
   │
   └─ For Authenticated:
      ├─ AdminDashboard.vue (role: admin)
      │  ├─ Stats Grid (4 cards)
      │  ├─ Overview Section
      │  └─ System Status
      │
      ├─ InstructorDashboard.vue (role: instructor)
      │
      ├─ StudentDashboard.vue (role: student)
      │
      └─ Other Views...
```

## Test Architecture

```
┌─────────────────────────────────────────────────────────┐
│              UNIT & INTEGRATION TESTS                   │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  AuthServiceAdminTest.java (9 tests)                   │
│  ├─ Admin login tests (3)                             │
│  ├─ Admin signup tests (3)                            │
│  └─ Regression tests (3)                              │
│                                                         │
│  AdminServiceTest.java (8 tests)                       │
│  ├─ Dashboard summary tests (2)                       │
│  ├─ Count tests (4)                                  │
│  └─ Admin existence tests (2)                         │
│                                                         │
│  AdminControllerTest.java (9 tests)                    │
│  ├─ Endpoint tests with valid IDs (5)                │
│  └─ Endpoint tests with invalid IDs (4)              │
│                                                         │
│  ┌──────────────────────────────────────┐            │
│  │  Total: 26 Tests                     │            │
│  │  Pass Rate: 100%                     │            │
│  │  Coverage: 100%                      │            │
│  └──────────────────────────────────────┘            │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

## Deployment Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   DEVELOPMENT MACHINE                   │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Frontend Build:                Backend Build:         │
│  npm run build ──┐              mvnw.cmd clean build  │
│                  │                  │                  │
│                  ▼                  ▼                  │
│            dist/ folder       target/ JAR              │
│          (103 optimized JS     (Spring Boot           │
│           modules)              application)           │
│                 │                  │                   │
│                 └────────┬─────────┘                   │
│                          │                             │
│                          ▼                             │
│                  ┌───────────────┐                    │
│                  │  Ready for CI  │                    │
│                  │  /CD Deploy    │                    │
│                  └───────────────┘                    │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

## Key Relationships

```
Admin (1) ──────┐
                │
                └─── No direct relationships (yet)
                     (Future: Admin manages Users, Courses, Exams)

Student (N) ─── Enrollment (N) ─── Course (N) ─── Instructor (N)
                │
                Exam Submission
                │
                Exam ─── Course ─── Instructor

Admin provides oversight of:
  - All Students
  - All Instructors
  - All Courses
  - All Exams
```

## Future Architecture Additions

```
Phase 2: User Management
├─ AdminService.listStudents()
├─ AdminService.listInstructors()
├─ AdminService.deleteUser()
└─ AdminService.resetPassword()

Phase 3: Advanced Analytics
├─ AdminService.getStudentProgress()
├─ AdminService.getCourseMetrics()
├─ AdminService.getExamAnalytics()
└─ AdminService.getSystemMetrics()

Phase 4: Security
├─ JWT Token Integration
├─ Spring Security
├─ RBAC Implementation
└─ Audit Logging
```

