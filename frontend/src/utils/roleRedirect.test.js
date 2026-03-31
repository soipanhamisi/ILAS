import { describe, expect, it } from 'vitest'
import { getDashboardRoute } from './roleRedirect'

describe('getDashboardRoute', () => {
  it('returns instructor dashboard path', () => {
    expect(getDashboardRoute('instructor')).toBe('/instructor')
  })

  it('returns student dashboard path', () => {
    expect(getDashboardRoute('student')).toBe('/student')
  })

  it('returns admin dashboard path', () => {
    expect(getDashboardRoute('admin')).toBe('/admin')
  })

  it('returns home path for unknown role', () => {
    expect(getDashboardRoute('guest')).toBe('/')
  })

  it('returns home path for missing role', () => {
    expect(getDashboardRoute()).toBe('/')
  })
})

