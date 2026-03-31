import { describe, expect, it } from 'vitest'
import { resolveAuthNavigation } from './index'

describe('resolveAuthNavigation', () => {
  it('redirects authenticated admin away from login page', () => {
    const result = resolveAuthNavigation(
      { name: 'Login', meta: {} },
      { isAuthenticated: true, userType: 'admin' }
    )

    expect(result).toBe('/admin')
  })

  it('redirects unauthenticated user to login for protected route', () => {
    const result = resolveAuthNavigation(
      { name: 'AdminDashboard', meta: { requiresAuth: true, role: 'admin' } },
      { isAuthenticated: false, userType: null }
    )

    expect(result).toBe('/login')
  })

  it('redirects role mismatch to own dashboard', () => {
    const result = resolveAuthNavigation(
      { name: 'StudentDashboard', meta: { requiresAuth: true, role: 'student' } },
      { isAuthenticated: true, userType: 'admin' }
    )

    expect(result).toBe('/admin')
  })

  it('allows matching authenticated role', () => {
    const result = resolveAuthNavigation(
      { name: 'AdminDashboard', meta: { requiresAuth: true, role: 'admin' } },
      { isAuthenticated: true, userType: 'admin' }
    )

    expect(result).toBe(true)
  })

  it('allows public route for anonymous users', () => {
    const result = resolveAuthNavigation(
      { name: 'Home', meta: {} },
      { isAuthenticated: false, userType: null }
    )

    expect(result).toBe(true)
  })
})

