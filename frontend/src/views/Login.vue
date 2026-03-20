<template>
  <div class="login-container">
    <div class="login-card">
      <div class="auth-header">
        <h1 class="login-title">🎓 ILAS</h1>
        <p class="login-subtitle">
          {{ isSignup ? 'Join the learning platform' : 'Welcome back! 👋' }}
        </p>
      </div>

      <div class="auth-toggle">
        <button
          :class="['toggle-btn', { active: !isSignup }]"
          @click="isSignup = false"
        >
          Login
        </button>
        <button
          :class="['toggle-btn', { active: isSignup }]"
          @click="isSignup = true"
        >
          Sign Up
        </button>
      </div>

      <!-- Login Form -->
      <form
        v-if="!isSignup"
        @submit.prevent="handleLogin"
        class="login-form"
      >
        <div class="form-group">
          <label>User Type</label>
          <select v-model="userType" required>
            <option value="">Select your role</option>
            <option value="instructor">Instructor</option>
            <option value="student">Student</option>
          </select>
        </div>

        <div class="form-group">
          <label>Username</label>
          <input
            v-model="loginForm.username"
            type="text"
            placeholder="Enter your username"
            required
          />
        </div>

        <div class="form-group">
          <label>Password</label>
          <input
            v-model="loginForm.password"
            type="password"
            placeholder="Enter your password"
            required
          />
        </div>

        <button
          type="submit"
          class="btn-login"
          :disabled="loading"
        >
          {{ loading ? 'Logging in...' : 'Login' }}
        </button>
      </form>

      <!-- Signup Form -->
      <form
        v-else
        @submit.prevent="handleSignup"
        class="login-form"
      >
        <div class="form-group">
          <label>User Type</label>
          <select v-model="userType" required>
            <option value="">Select your role</option>
            <option value="instructor">Instructor</option>
            <option value="student">Student</option>
          </select>
        </div>

        <div class="form-group">
          <label>Full Name</label>
          <input
            v-model="signupForm.name"
            type="text"
            placeholder="Enter your full name"
            required
          />
        </div>

        <div class="form-group">
          <label>Email</label>
          <input
            v-model="signupForm.email"
            type="email"
            placeholder="Enter your email"
            required
          />
        </div>

        <div class="form-group">
          <label>Username</label>
          <input
            v-model="signupForm.username"
            type="text"
            placeholder="Choose a username"
            required
          />
        </div>

        <div class="form-group">
          <label>Password</label>
          <input
            v-model="signupForm.password"
            type="password"
            placeholder="Create a password"
            required
          />
        </div>

        <button
          type="submit"
          class="btn-login"
          :disabled="loading"
        >
          {{ loading ? 'Creating account...' : 'Sign Up' }}
        </button>
      </form>

      <div v-if="error" class="error-message">
        {{ error }}
      </div>

      <div v-if="success" class="success-message">
        ✓ {{ successMessage }}
      </div>

      <!-- Demo Credentials -->
      <div class="demo-info">
        <p><strong>Demo Credentials:</strong></p>
        <p><strong>Instructors:</strong></p>
        <p>📧 smith_instructor / password123</p>
        <p>📧 johnson_instructor / password123</p>
        <p><strong>Students:</strong></p>
        <p>👤 john_student / password123</p>
        <p>👤 jane_student / password123</p>
        <p>👤 alice_student / password123</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const isSignup = ref(false)
const userType = ref('')
const loading = ref(false)
const error = ref('')
const success = ref(false)
const successMessage = ref('')

const loginForm = ref({
  username: '',
  password: ''
})

const signupForm = ref({
  name: '',
  email: '',
  username: '',
  password: ''
})

const handleLogin = async () => {
  error.value = ''
  success.value = false

  if (!userType.value || !loginForm.value.username || !loginForm.value.password) {
    error.value = 'Please fill in all fields'
    return
  }

  loading.value = true

  try {
    await authStore.login(loginForm.value.username, loginForm.value.password, userType.value)
    success.value = true
    successMessage.value = 'Login successful! Redirecting...'


    // Redirect after 1 second
    setTimeout(() => {
      if (userType.value === 'instructor') {
        router.push('/instructor')
      } else {
        router.push('/student')
      }
    }, 1000)
  } catch (err) {
    error.value = err.response?.data?.message || err.message || 'Login failed'
    console.error('Login error:', err)
  } finally {
    loading.value = false
  }
}

const handleSignup = async () => {
  error.value = ''
  success.value = false

  if (!userType.value || !signupForm.value.name || !signupForm.value.email ||
      !signupForm.value.username || !signupForm.value.password) {
    error.value = 'Please fill in all fields'
    return
  }

  loading.value = true

  try {
    await authStore.signup(
      signupForm.value.name,
      signupForm.value.email,
      signupForm.value.username,
      signupForm.value.password,
      userType.value
    )
    success.value = true
    successMessage.value = 'Account created successfully! Redirecting...'


    // Reset form
    signupForm.value = {
      name: '',
      email: '',
      username: '',
      password: ''
    }

    // Redirect after 1 second
    setTimeout(() => {
      if (userType.value === 'instructor') {
        router.push('/instructor')
      } else {
        router.push('/student')
      }
    }, 1000)
  } catch (err) {
    error.value = err.response?.data?.message || err.message || 'Signup failed'
    console.error('Signup error:', err)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-card {
  background: var(--glass-bg-strong);
  border-radius: 24px;
  padding: 48px;
  box-shadow: var(--shadow-strong);
  max-width: 450px;
  width: 100%;
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(16px);
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-title {
  font-size: 40px;
  font-weight: 800;
  color: var(--color-primary);
  margin-bottom: 8px;
}

.login-subtitle {
  color: var(--color-text-soft);
  font-size: 16px;
}

.auth-toggle {
  display: flex;
  gap: 12px;
  margin-bottom: 32px;
  background: rgba(255, 255, 255, 0.42);
  padding: 6px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.45);
}

.toggle-btn {
  flex: 1;
  padding: 10px 16px;
  border: none;
  border-radius: 10px;
  background: transparent;
  color: var(--color-text-soft);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.toggle-btn.active {
  background: rgba(255, 255, 255, 0.7);
  color: var(--color-primary);
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 600;
  color: var(--color-text);
  font-size: 14px;
}

.form-group input,
.form-group select {
  padding: 14px;
  border: 1px solid rgba(255, 255, 255, 0.55);
  border-radius: 12px;
  font-size: 16px;
  background: rgba(255, 255, 255, 0.66);
  backdrop-filter: blur(10px);
  color: var(--color-text);
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: var(--color-accent);
  box-shadow: 0 0 0 4px rgba(182, 223, 217, 0.35);
}

.btn-login {
  background: linear-gradient(135deg, var(--color-surface) 0%, var(--color-accent) 100%);
  color: #224139;
  padding: 16px;
  border-radius: 999px;
  font-size: 18px;
  font-weight: 600;
  margin-top: 8px;
  cursor: pointer;
  border: none;
  transition: all 0.3s ease;
}

.btn-login:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-soft);
}

.btn-login:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.error-message {
  background: rgba(255, 255, 255, 0.55);
  color: var(--color-primary);
  padding: 12px;
  border-radius: 8px;
  text-align: center;
  margin-top: 16px;
  border: 1px solid var(--glass-border);
  font-size: 14px;
}

.success-message {
  background: rgba(255, 255, 255, 0.56);
  color: var(--color-primary);
  padding: 12px;
  border-radius: 8px;
  text-align: center;
  margin-top: 16px;
  border: 1px solid var(--glass-border);
  font-size: 14px;
  font-weight: 600;
}

.demo-info {
  margin-top: 24px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.48);
  border-radius: 8px;
  font-size: 13px;
  color: var(--color-text-soft);
  border: 1px solid var(--glass-border);
}

.demo-info p {
  margin: 4px 0;
  line-height: 1.4;
}

.demo-info strong {
  color: var(--color-text);
  display: block;
  margin-top: 8px;
  margin-bottom: 4px;
}
</style>

