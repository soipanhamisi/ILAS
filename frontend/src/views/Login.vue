<template>
  <div class="login-container">
    <div class="login-card">
      <h1 class="login-title">Hey there 👋</h1>
      <p class="login-subtitle">Jump in to your learning space</p>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label>User Type</label>
          <select v-model="userType" required>
            <option value="">Select user type</option>
            <option value="instructor">Instructor</option>
            <option value="student">Student</option>
          </select>
        </div>

        <div class="form-group">
          <label>User ID</label>
          <input
            v-model="userId"
            type="number"
            placeholder="Enter your ID"
            required
          />
        </div>

        <div class="form-group">
          <label>Name</label>
          <input
            v-model="userName"
            type="text"
            placeholder="Enter your name"
            required
          />
        </div>

        <button type="submit" class="btn-login">
          Login
        </button>
      </form>

      <div v-if="error" class="error-message">
        {{ error }}
      </div>

      <div class="demo-info">
        <p><strong>Demo Credentials:</strong></p>
        <p>Instructor: ID=1, Name=Dr. Smith</p>
        <p>Student: ID=5, Name=John Doe</p>
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

const userType = ref('')
const userId = ref('')
const userName = ref('')
const error = ref('')

const handleLogin = () => {
  error.value = ''

  if (!userType.value || !userId.value || !userName.value) {
    error.value = 'Please fill in all fields'
    return
  }

  try {
    authStore.login(parseInt(userId.value), userType.value, userName.value)

    if (userType.value === 'instructor') {
      router.push('/instructor')
    } else {
      router.push('/student')
    }
  } catch (err) {
    error.value = 'Login failed. Please try again.'
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

.login-title {
  font-size: 32px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 8px;
  color: var(--color-primary);
}

.login-subtitle {
  text-align: center;
  color: var(--color-text-soft);
  margin-bottom: 32px;
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
}

.form-group input,
.form-group select {
  padding: 14px;
  border: 2px solid rgba(112, 113, 77, 0.3);
  border-radius: 10px;
  font-size: 16px;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: var(--color-accent);
}

.btn-login {
  background: linear-gradient(135deg, var(--color-surface) 0%, var(--color-accent) 100%);
  color: #224139;
  padding: 16px;
  border-radius: 999px;
  font-size: 18px;
  font-weight: 600;
  margin-top: 8px;
}

.error-message {
  background: rgba(255, 255, 255, 0.55);
  color: var(--color-primary);
  padding: 12px;
  border-radius: 8px;
  text-align: center;
  margin-top: 16px;
  border: 1px solid var(--glass-border);
}

.demo-info {
  margin-top: 24px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.48);
  border-radius: 8px;
  font-size: 14px;
  color: var(--color-text-soft);
  border: 1px solid var(--glass-border);
}

.demo-info p {
  margin: 4px 0;
}
</style>

