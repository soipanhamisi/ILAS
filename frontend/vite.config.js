import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// Default to the local Spring Boot port used by the repo's .env file.
const devApiTarget = process.env.VITE_DEV_API_PROXY_TARGET || 'http://localhost:8081'

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
