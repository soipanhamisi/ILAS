<template>
  <div class="auth-shell">
    <section class="auth-panel text-enter">
      <div class="auth-brand">
        <img src="/markrr-logo.svg" alt="" class="brand-logo" />
        <h1>Markrr</h1>
      </div>

      <div class="auth-copy">
        <p class="eyebrow">{{ isSignup ? 'Get started free' : 'Welcome back' }}</p>
        <h2>{{ isSignup ? 'Create your account' : 'Log in to Markrr' }}</h2>
        <p>{{ isSignup ? 'Join educators and students making every answer count.' : 'Continue marking smarter and helping students improve.' }}</p>
      </div>

      <div class="auth-toggle">
        <button :class="['toggle-btn', { active: !isSignup }]" @click="isSignup = false">
          Login
        </button>
        <button :class="['toggle-btn', { active: isSignup }]" @click="isSignup = true">
          Sign Up
        </button>
      </div>

      <form v-if="!isSignup" @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label>User Type</label>
          <select v-model="userType" required>
            <option value="">Select your role</option>
            <option value="instructor">Instructor</option>
            <option value="student">Student</option>
            <option value="admin">Admin</option>
          </select>
        </div>

        <div class="form-group">
          <label>Username</label>
          <input v-model="loginForm.username" type="text" placeholder="Enter your username" required />
        </div>

        <div class="form-group">
          <label>Password</label>
          <input v-model="loginForm.password" type="password" placeholder="Enter your password" required />
        </div>

        <button type="submit" class="btn-login" :disabled="loading">
          {{ loading ? 'Logging in...' : 'Log in' }}
        </button>
      </form>

      <form v-else @submit.prevent="handleSignup" class="login-form">
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
          <input v-model="signupForm.name" type="text" placeholder="Enter your full name" required />
        </div>

        <div class="form-group">
          <label>Email</label>
          <input v-model="signupForm.email" type="email" placeholder="Enter your email" required />
        </div>

        <div class="form-group">
          <label>Username</label>
          <input v-model="signupForm.username" type="text" placeholder="Choose a username" required />
        </div>

        <div class="form-group">
          <label>Password</label>
          <input v-model="signupForm.password" type="password" placeholder="Create a password" required />
        </div>

        <button type="submit" class="btn-login" :disabled="loading">
          {{ loading ? 'Creating account...' : 'Create account' }}
        </button>
      </form>

      <div v-if="error" class="error-message">{{ error }}</div>
      <div v-if="success" class="success-message">{{ successMessage }}</div>
    </section>

    <section class="promo-panel">
      <p class="promo-chip">Purpose-built for learning</p>
      <div class="promo-card">
        <img :src="resultPreviewUrl" alt="Markrr assessment result with student marks and AI feedback" class="result-preview-image" />
      </div>
      <blockquote>
        "The feedback is specific enough to act on, but simple enough that my students actually read it."
      </blockquote>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import resultPreviewUrl from '../../ResultPreview (1).svg'
import { useAuthStore } from '../stores/auth'
import { getDashboardRoute } from '../utils/roleRedirect'

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

    setTimeout(() => {
      router.push(getDashboardRoute(authStore.userType))
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

    signupForm.value = {
      name: '',
      email: '',
      username: '',
      password: ''
    }

    setTimeout(() => {
      router.push(getDashboardRoute(authStore.userType))
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
.auth-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(0, 0.9fr);
  background: #f8efdc;
}

.auth-panel {
  width: 100%;
  max-width: 680px;
  min-height: 100vh;
  margin: 0 auto;
  padding: clamp(24px, 4vw, 52px) clamp(20px, 5vw, 72px);
  background: #fffefb;
  border-right: 1px solid rgba(116, 83, 64, 0.15);
}

.auth-brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-logo {
  width: 44px;
  height: 44px;
  display: block;
  flex: 0 0 auto;
}

.auth-brand h1 {
  color: #351610;
  font-size: clamp(32px, 4vw, 44px);
  line-height: 1;
}

.auth-copy {
  margin: clamp(28px, 5vh, 54px) auto 0;
  max-width: 560px;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.14em;
  color: #c36400;
  font-weight: 700;
  font-size: 13px;
}

.auth-copy h2 {
  margin-top: 10px;
  font-size: clamp(38px, 5vw, 62px);
  line-height: 0.98;
  color: #3b1712;
  overflow-wrap: anywhere;
}

.auth-copy p {
  margin-top: 14px;
  color: #705447;
  font-size: clamp(15px, 1.6vw, 18px);
  line-height: 1.5;
}

.auth-toggle,
.login-form,
.auth-panel > .error-message,
.auth-panel > .success-message {
  width: 100%;
  max-width: 560px;
  margin-left: auto;
  margin-right: auto;
}

.auth-toggle {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  margin-top: 28px;
  background: #f4e8cf;
  padding: 5px;
  border-radius: 14px;
  border: 1px solid rgba(116, 83, 64, 0.18);
}

.toggle-btn {
  min-height: 48px;
  border: 0;
  background: transparent;
  color: #7d6052;
  font-weight: 700;
  font-size: clamp(14px, 1.5vw, 16px);
}

.login-form {
  display: grid;
  gap: 16px;
  margin-top: 20px;
}

.form-group {
  display: grid;
  gap: 8px;
  min-width: 0;
}

.form-group label {
  font-weight: 700;
  color: #3f2118;
  font-size: 15px;
}

.login-form input,
.login-form select {
  width: 100%;
  min-width: 0;
  min-height: 54px;
  padding: 13px 16px;
  font-size: 16px;
  line-height: 1.2;
}

.btn-login {
  width: 100%;
  min-height: 54px;
  margin-top: 4px;
  padding: 13px 20px;
  background: linear-gradient(180deg, #61201f 0%, #481614 100%);
  color: #fff8ef;
  border-radius: 14px;
  border: 1px solid rgba(78, 26, 24, 0.5);
  font-size: 16px;
  line-height: 1.2;
  font-weight: 700;
}

.error-message,
.success-message {
  margin-top: 14px;
  border-radius: 10px;
  padding: 12px 14px;
  text-align: center;
  font-size: 14px;
  overflow-wrap: anywhere;
}

.error-message {
  background: rgba(184, 72, 52, 0.14);
  color: #8c2316;
  border: 1px solid rgba(184, 72, 52, 0.24);
}

.success-message {
  background: rgba(30, 109, 98, 0.16);
  color: #1c6158;
  border: 1px solid rgba(30, 109, 98, 0.24);
}

.promo-panel {
  min-width: 0;
  background: radial-gradient(circle at right top, #2a6962 0%, #1b504b 42%, #164541 100%);
  color: #f6f7e8;
  padding: clamp(26px, 4vw, 56px);
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  gap: clamp(16px, 2.4vw, 28px);
}

.promo-panel::before,
.promo-panel::after {
  content: '';
  position: absolute;
  border: 1px solid rgba(208, 216, 172, 0.18);
  border-radius: 50%;
  pointer-events: none;
}

.promo-panel::before {
  width: min(360px, 42vw);
  height: min(360px, 42vw);
  top: -70px;
  right: max(-90px, -8vw);
}

.promo-panel::after {
  width: min(500px, 58vw);
  height: min(500px, 58vw);
  top: -130px;
  right: max(-170px, -14vw);
}

.promo-chip {
  position: relative;
  z-index: 1;
  align-self: flex-end;
  max-width: 100%;
  padding: 8px 16px;
  border: 1px solid rgba(208, 216, 172, 0.34);
  border-radius: 999px;
  color: #d5dda9;
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
}

.promo-card {
  position: relative;
  z-index: 1;
  width: min(100%, 649px);
  margin: 0 auto;
}

.result-preview-image {
  display: block;
  width: 100%;
  height: auto;
}

blockquote {
  position: relative;
  z-index: 1;
  width: 100%;
  margin: 10px auto 0;
  font-size: clamp(24px, 2.8vw, 40px);
  line-height: 1.14;
  overflow-wrap: anywhere;
}

@media (max-width: 1100px) {
  .auth-shell {
    grid-template-columns: 1fr;
  }

  .auth-panel {
    max-width: 720px;
    min-height: auto;
    border-right: 0;
  }

  .promo-panel {
    min-height: 520px;
  }

  blockquote {
    max-width: 720px;
  }
}

@media (max-width: 640px) {
  .auth-panel {
    padding: 22px 16px 32px;
  }

  .auth-copy {
    margin-top: 34px;
  }

  .auth-copy h2 {
    font-size: clamp(34px, 11vw, 48px);
  }

  .auth-copy p {
    font-size: 15px;
  }

  .auth-toggle {
    margin-top: 22px;
  }

  .login-form {
    gap: 14px;
  }

  .login-form input,
  .login-form select,
  .btn-login {
    min-height: 52px;
  }

  .promo-panel {
    min-height: 0;
    padding: 32px 16px 44px;
  }

  .promo-chip {
    align-self: flex-start;
    font-size: 12px;
  }

  .promo-card {
    border-width: 6px;
  }


  blockquote {
    font-size: clamp(28px, 8vw, 40px);
  }
}
</style>

