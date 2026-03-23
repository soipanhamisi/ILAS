<template>
  <div id="app">
    <NavBar v-if="authStore.isAuthenticated" />
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from './stores/auth'
import NavBar from './components/NavBar.vue'

const authStore = useAuthStore()

onMounted(() => {
  authStore.checkAuth()
})
</script>

<style>
:root {
  --color-primary: #7d9a57;
  --color-surface: #D0E4A1;
  --color-surface-strong: #EBEEB9;
  --color-muted: #6f7e68;
  --color-accent: #B6DFD9;
  --color-bg: #F6F5E6;
  --color-text: #384634;
  --color-text-soft: #5a6a54;
  --color-white: #ffffff;
  --glass-bg: rgba(246, 245, 230, 0.52);
  --glass-bg-strong: rgba(255, 255, 255, 0.64);
  --glass-border: rgba(255, 255, 255, 0.5);
  --shadow-soft: 0 8px 24px rgba(79, 106, 72, 0.12);
  --shadow-strong: 0 18px 44px rgba(79, 106, 72, 0.2);
}

#app {
  width: 100%;
  min-height: 100vh;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
  background:
    radial-gradient(circle at 15% 20%, rgba(182, 223, 217, 0.65), transparent 35%),
    radial-gradient(circle at 85% 15%, rgba(208, 228, 161, 0.6), transparent 35%),
    linear-gradient(140deg, var(--color-bg) 0%, var(--color-surface-strong) 52%, var(--color-accent) 100%);
  color: var(--color-text);
  line-height: 1.55;
}

button {
  cursor: pointer;
  border: none;
  border-radius: 12px;
  padding: 10px 20px;
  font-size: 16px;
  transition: all 0.3s ease;
}

button:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-soft);
}

button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-primary {
  background: linear-gradient(135deg, var(--color-primary) 0%, #94b878 100%);
  color: #f9fff7;
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.58);
  color: var(--color-text);
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(10px);
}

.btn-success {
  background: linear-gradient(135deg, #8bc5bc 0%, var(--color-accent) 100%);
  color: #1f3b39;
}

.btn-danger {
  background: linear-gradient(135deg, #d0b487 0%, #c9987c 100%);
  color: #3e2b1d;
}

input, textarea, select {
  width: 100%;
  padding: 12px;
  border: 1px solid rgba(255, 255, 255, 0.55);
  border-radius: 12px;
  font-size: 16px;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  background: rgba(255, 255, 255, 0.66);
  backdrop-filter: blur(10px);
  color: var(--color-text);
}

input:focus, textarea:focus, select:focus {
  outline: none;
  border-color: var(--color-accent);
  box-shadow: 0 0 0 4px rgba(182, 223, 217, 0.35);
}

.card {
  background: var(--glass-bg-strong);
  border-radius: 20px;
  padding: 24px;
  box-shadow: var(--shadow-soft);
  margin-bottom: 20px;
  border: 1px solid var(--glass-border);
  backdrop-filter: blur(14px);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.glass-panel {
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: 20px;
  box-shadow: var(--shadow-soft);
  backdrop-filter: blur(16px);
}
</style>
