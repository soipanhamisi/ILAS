import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/instructor',
    name: 'InstructorDashboard',
    component: () => import('../views/InstructorDashboard.vue'),
    meta: { requiresAuth: true, role: 'instructor' }
  },
  {
    path: '/instructor/exams/create',
    name: 'CreateExam',
    component: () => import('../views/CreateExam.vue'),
    meta: { requiresAuth: true, role: 'instructor' }
  },
  {
    path: '/instructor/exams/:examId',
    name: 'ExamSubmissions',
    component: () => import('../views/ExamSubmissions.vue'),
    meta: { requiresAuth: true, role: 'instructor' }
  },
  {
    path: '/student',
    name: 'StudentDashboard',
    component: () => import('../views/StudentDashboard.vue'),
    meta: { requiresAuth: true, role: 'student' }
  },
  {
    path: '/student/exams/:examId',
    name: 'TakeExam',
    component: () => import('../views/TakeExam.vue'),
    meta: { requiresAuth: true, role: 'student' }
  },
  {
    path: '/student/results/:examId',
    name: 'ViewResults',
    component: () => import('../views/ViewResults.vue'),
    meta: { requiresAuth: true, role: 'student' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth) {
    if (!authStore.isAuthenticated) {
      next('/login')
    } else if (to.meta.role && authStore.userType !== to.meta.role) {
      next('/')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router

