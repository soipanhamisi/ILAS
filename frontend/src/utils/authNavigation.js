import { getDashboardRoute } from './roleRedirect'

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

