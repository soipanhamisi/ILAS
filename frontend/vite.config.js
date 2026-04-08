import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

const devApiTarget = process.env.VITE_DEV_API_PROXY_TARGET || 'http://localhost:8080'

export default defineConfig({
  plugins: [vue()],
  test: {
    environment: 'node',
    include: ['src/**/*.test.js']
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: devApiTarget,
        changeOrigin: true
      }
    }
  }
})
