import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getDashboardRoute } from '../utils/roleRedirect'

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
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('../views/AdminDashboard.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export const resolveAuthNavigation = (to, authState) => {
  if (to.name === 'Login' && authState.isAuthenticated) {
    return getDashboardRoute(authState.userType)
  }

  if (to.meta?.requiresAuth) {
    if (!authState.isAuthenticated) {
      return '/login'
    }

    if (to.meta.role && authState.userType !== to.meta.role) {
      return getDashboardRoute(authState.userType)
    }
  }

  return true
}

// Navigation guard
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const decision = resolveAuthNavigation(to, {
    isAuthenticated: authStore.isAuthenticated,
    userType: authStore.userType
  })

  if (decision === true) {
    next()
  } else {
    next(decision)
  }
})

export default router

