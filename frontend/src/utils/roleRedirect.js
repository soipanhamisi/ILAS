export const getDashboardRoute = (role) => {
  if (role === 'instructor') return '/instructor'
  if (role === 'student') return '/student'
  if (role === 'admin') return '/admin'
  return '/'
}

