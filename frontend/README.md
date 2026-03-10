# ILAS Frontend - Vue.js Application

## 🎯 Overview

Modern, responsive Vue.js 3 frontend for the Integrated Learning Assessment System (ILAS). Built with Vite, Vue Router, Pinia, and Axios.

---

## 🚀 Quick Start

### Prerequisites
- Node.js (v16 or higher)
- npm or yarn
- Backend API running on http://localhost:8080

### Installation

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

The application will be available at `http://localhost:3000`

---

## 📦 Technology Stack

- **Vue 3** - Progressive JavaScript framework
- **Vite** - Next-generation frontend build tool
- **Vue Router** - Official router for Vue.js
- **Pinia** - State management library
- **Axios** - HTTP client for API calls

---

## 🏗️ Project Structure

```
frontend/
├── src/
│   ├── components/
│   │   └── NavBar.vue           # Navigation component
│   ├── views/
│   │   ├── Home.vue              # Landing page
│   │   ├── Login.vue             # Login page
│   │   ├── InstructorDashboard.vue
│   │   ├── CreateExam.vue
│   │   ├── ExamSubmissions.vue
│   │   ├── StudentDashboard.vue
│   │   ├── TakeExam.vue
│   │   └── ViewResults.vue
│   ├── services/
│   │   └── api.js                # API service layer
│   ├── stores/
│   │   └── auth.js               # Authentication store
│   ├── router/
│   │   └── index.js              # Router configuration
│   ├── App.vue                   # Root component
│   └── main.js                   # Application entry point
├── index.html                    # HTML entry point
├── vite.config.js               # Vite configuration
└── package.json                 # Dependencies
```

---

## 👥 User Roles & Features

### 👨‍🏫 Instructor Features

1. **Dashboard**
   - View all courses
   - See exams per course
   - Quick access to create exams

2. **Create Exam**
   - Upload CSV template
   - Set exam title and max score
   - Assign to specific course

3. **View Submissions**
   - See all student submissions
   - Filter by graded/ungraded
   - Inline grading interface

4. **Grade Submissions**
   - Assign numerical grade
   - Provide written feedback
   - Add grade justification

### 👨‍🎓 Student Features

1. **Dashboard**
   - View available exams
   - See submission history
   - Check grading status

2. **Take Exam**
   - View exam details
   - Upload completed CSV
   - Submit responses

3. **View Results**
   - See grade and percentage
   - Read instructor feedback
   - View grade justification
   - Performance analysis

---

## 🎨 UI/UX Features

### Design System
- **Color Scheme**: Purple gradient (`#667eea` to `#764ba2`)
- **Typography**: System fonts with clean hierarchy
- **Components**: Card-based design with smooth transitions
- **Responsive**: Mobile-first, works on all devices

### Interactions
- **Smooth Animations**: Hover effects, transitions
- **Loading States**: Clear feedback during API calls
- **Error Handling**: User-friendly error messages
- **Success Messages**: Confirmation feedback

### Accessibility
- Semantic HTML
- Keyboard navigation
- Clear focus states
- Readable contrast ratios

---

## 🔧 Configuration

### API Configuration
Edit `src/services/api.js` to change API base URL:

```javascript
const API_BASE_URL = 'http://localhost:8080/api'
```

### Proxy Configuration
Vite proxy is configured in `vite.config.js`:

```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

---

## 📱 Routes

### Public Routes
- `/` - Home/Landing page
- `/login` - Login page

### Instructor Routes (Protected)
- `/instructor` - Instructor dashboard
- `/instructor/exams/create` - Create new exam
- `/instructor/exams/:examId` - View submissions

### Student Routes (Protected)
- `/student` - Student dashboard
- `/student/exams/:examId` - Take exam
- `/student/results/:examId` - View results

---

## 🔐 Authentication

Simple ID-based authentication for demonstration:

**Demo Credentials:**

**Instructor:**
- User Type: Instructor
- ID: 1
- Name: Dr. Smith

**Student:**
- User Type: Student
- ID: 5
- Name: John Doe

Authentication state is managed by Pinia and persisted in localStorage.

---

## 🧪 Testing the Application

### Manual Testing Flow

1. **Start Backend**
   ```bash
   cd ILAS
   ./mvnw spring-boot:run
   ```

2. **Start Frontend**
   ```bash
   cd frontend
   npm run dev
   ```

3. **Test Instructor Flow**
   - Login as instructor (ID: 1)
   - Navigate to "Create Exam"
   - Upload CSV template
   - View submissions
   - Grade a submission

4. **Test Student Flow**
   - Login as student (ID: 5)
   - View available exams
   - Take an exam
   - Submit CSV file
   - View results after grading

---

## 📋 CSV File Format

### Exam Template (Instructor Creates)
```csv
question,learnerResponses,maxGrade,grade
What is 2+2?,,[10],
Define OOP?,,[20],
Explain inheritance?,,[30],
```

### Student Submission (Completed Exam)
```csv
question,learnerResponses,maxGrade,grade
What is 2+2?,4,[10],
Define OOP?,Object-oriented programming is...,[20],
Explain inheritance?,Inheritance allows classes to...,[30],
```

---

## 🚀 Build for Production

```bash
# Build for production
npm run build

# Preview production build
npm run preview
```

Built files will be in the `dist/` directory.

---

## 🎯 Key Features Implemented

### ✅ State Management
- Pinia store for authentication
- LocalStorage persistence
- Reactive state updates

### ✅ Routing
- Vue Router with navigation guards
- Role-based route protection
- Dynamic route parameters

### ✅ API Integration
- Axios for HTTP requests
- Centralized API service
- Error handling
- File upload support

### ✅ User Experience
- Loading states
- Error messages
- Success notifications
- Smooth transitions
- Responsive design

### ✅ Component Architecture
- Reusable components
- Single File Components (SFC)
- Composition API
- Props & Events

---

## 🔧 Common Commands

```bash
# Install dependencies
npm install

# Start dev server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Lint code (if configured)
npm run lint
```

---

## 🐛 Troubleshooting

### API Connection Issues
- Ensure backend is running on port 8080
- Check CORS configuration in backend
- Verify API endpoints match

### File Upload Issues
- Ensure CSV files only
- Check file size limits (10MB max)
- Verify multipart/form-data headers

### Authentication Issues
- Clear localStorage: `localStorage.clear()`
- Check browser console for errors
- Verify user IDs match backend data

---

## 📚 Documentation

- **API Documentation**: `../API_DOCUMENTATION.md`
- **Backend Services**: `../SERVICE_LAYER_DOCUMENTATION.md`
- **Controller Guide**: `../CONTROLLER_IMPLEMENTATION_GUIDE.md`

---

## 🎨 Customization

### Change Color Scheme
Edit gradient colors in components:

```css
background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
```

### Modify Layout
- Adjust container max-width in components
- Update grid configurations
- Change padding/margins

### Add Features
- Create new components in `src/components/`
- Add new views in `src/views/`
- Update router in `src/router/index.js`
- Add API methods in `src/services/api.js`

---

## 🌟 Future Enhancements

- [ ] Real authentication with JWT
- [ ] File preview before upload
- [ ] Drag & drop file upload
- [ ] Real-time notifications
- [ ] Dark mode support
- [ ] Export grades as PDF
- [ ] Charts and analytics
- [ ] Search and filtering
- [ ] Pagination for large lists
- [ ] Progressive Web App (PWA)

---

## 📞 Support

For issues or questions:
1. Check documentation files
2. Review console errors
3. Verify backend is running
4. Check network tab in browser DevTools

---

## ✅ Checklist

- [x] Vue 3 with Composition API
- [x] Vite for fast development
- [x] Vue Router with guards
- [x] Pinia state management
- [x] Axios API integration
- [x] Responsive design
- [x] File upload support
- [x] Error handling
- [x] Loading states
- [x] Role-based access
- [x] Beautiful UI/UX
- [x] Complete documentation

---

**Status:** ✅ Production Ready

**Version:** 1.0.0

**Last Updated:** March 10, 2026

