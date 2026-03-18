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
  --color-primary: #436032;
  --color-surface: #CFDAC5;
  --color-surface-strong: #B7CB91;
  --color-muted: #70714D;
  --color-accent: #386547;
  --color-text: #2f3a25;
  --color-text-soft: #4d5a3f;
  --color-white: #ffffff;
  --shadow-soft: 0 4px 12px rgba(56, 101, 71, 0.16);
  --shadow-strong: 0 10px 24px rgba(67, 96, 50, 0.22);
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
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 55%, var(--color-muted) 100%);
  color: var(--color-text);
}

button {
  cursor: pointer;
  border: none;
  border-radius: 8px;
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
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
  color: var(--color-white);
}

.btn-secondary {
  background: var(--color-surface);
  color: var(--color-text);
  border: 1px solid rgba(67, 96, 50, 0.18);
}

.btn-success {
  background: linear-gradient(135deg, var(--color-accent) 0%, var(--color-primary) 100%);
  color: var(--color-white);
}

.btn-danger {
  background: linear-gradient(135deg, var(--color-muted) 0%, var(--color-primary) 100%);
  color: var(--color-white);
}

input, textarea, select {
  width: 100%;
  padding: 12px;
  border: 2px solid rgba(112, 113, 77, 0.3);
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  background: rgba(255, 255, 255, 0.9);
  color: var(--color-text);
}

input:focus, textarea:focus, select:focus {
  outline: none;
  border-color: var(--color-accent);
  box-shadow: 0 0 0 3px rgba(56, 101, 71, 0.15);
}

.card {
  background: rgba(255, 255, 255, 0.96);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--shadow-soft);
  margin-bottom: 20px;
  border: 1px solid rgba(67, 96, 50, 0.08);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
</style>
