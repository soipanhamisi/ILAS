<template>
  <div class="home-container">
    <div class="hero shell-card">
      <div class="hero-copy">
        <h1 class="hero-title">ILAS</h1>
        <p class="hero-subtitle">Intelligent Learning Assessment System</p>
        <p class="hero-description">
          Build, deliver, and review assessments in one workspace.
        </p>
        <router-link to="/login" class="btn-get-started">
          Enter Workspace
        </router-link>
      </div>
      <div class="hero-preview">
        <img
          :src="landingHeroImage"
          alt="Students collaborating in a digital workspace"
          class="preview-block preview-image large"
          @error="onPreviewImageError($event, fallbackPreviewImage)"
        />
        <div class="preview-row">
          <img
            :src="landingCourseImage"
            alt="Course design preview"
            class="preview-block preview-image"
            @error="onPreviewImageError($event, fallbackPreviewImage)"
          />
          <img
            :src="landingExamImage"
            alt="Exam workspace preview"
            class="preview-block preview-image"
            @error="onPreviewImageError($event, fallbackPreviewImage)"
          />
        </div>
      </div>
    </div>

    <div class="features">
      <div class="feature-card">
        <div class="feature-icon">IN</div>
        <h3>For Instructors</h3>
        <p>Create exams, grade submissions, and provide detailed feedback</p>
      </div>

      <div class="feature-card">
        <div class="feature-icon">ST</div>
        <h3>For Students</h3>
        <p>Take exams, submit answers, and receive grades with feedback</p>
      </div>

      <div class="feature-card">
        <div class="feature-icon">AN</div>
        <h3>Track Progress</h3>
        <p>Monitor submissions and view detailed performance analytics</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getDashboardRoute } from '../utils/roleRedirect'

const router = useRouter()
const authStore = useAuthStore()

const landingHeroImage = new URL('../assets/ui-images/brain-with-glasses-illustration.jpg', import.meta.url).href
const landingCourseImage = new URL('../assets/ui-images/download (4).jpg', import.meta.url).href
const landingExamImage = new URL('../assets/ui-images/Classroom Tour 2014-2015.jpg', import.meta.url).href
const fallbackPreviewImage = new URL('../assets/ui-images/course-covers/mobile-first.svg', import.meta.url).href

const onPreviewImageError = (event, fallbackSrc) => {
  const image = event.target
  if (image && image.src !== fallbackSrc) {
    image.src = fallbackSrc
  }
}

onMounted(() => {
  if (authStore.isAuthenticated) {
    router.push(getDashboardRoute(authStore.userType))
  }
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  padding: 36px 28px;
  max-width: 1200px;
  margin: 0 auto;
}

.shell-card {
  background: var(--glass-bg-strong);
  border: 1px solid var(--glass-border);
  border-radius: 28px;
  box-shadow: var(--shadow-soft);
}

.hero {
  margin-bottom: 26px;
  padding: 34px;
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  gap: 22px;
}

.eyebrow {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--color-muted);
  margin-bottom: 8px;
}

.hero-title {
  font-size: 72px;
  font-weight: 800;
  color: var(--color-primary);
  margin-bottom: 4px;
}

.hero-subtitle {
  font-size: 28px;
  color: var(--color-text);
  font-weight: 600;
  margin-bottom: 16px;
}

.hero-description {
  font-size: 20px;
  color: var(--color-text-soft);
  margin-bottom: 32px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.btn-get-started {
  display: inline-block;
  background: linear-gradient(135deg, #d8ccff 0%, #bca6ff 100%);
  color: #17181f;
  padding: 13px 26px;
  border-radius: 999px;
  font-size: 16px;
  font-weight: 700;
  text-decoration: none;
  transition: all 0.3s ease;
}

.hero-preview {
  display: grid;
  gap: 14px;
}

.preview-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.preview-block {
  border-radius: 18px;
  border: 1px solid var(--glass-border);
  background: linear-gradient(145deg, #2e3446 0%, #252a38 100%);
  min-height: 120px;
}

.preview-image {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-block.large {
  min-height: 220px;
}

.btn-get-started:hover {
  transform: translateY(-2px);
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 18px;
  width: 100%;
}

.feature-card {
  background: var(--glass-bg-strong);
  padding: 28px;
  border-radius: 20px;
  text-align: left;
  transition: all 0.3s ease;
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--glass-border);
}

.feature-card:hover {
  transform: translateY(-3px);
}

.feature-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: rgba(197, 177, 255, 0.2);
  border: 1px solid rgba(197, 177, 255, 0.32);
  color: #d9ccff;
  font-size: 12px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.feature-card h3 {
  font-size: 24px;
  margin-bottom: 12px;
  color: var(--color-primary);
}

.feature-card p {
  color: var(--color-text-soft);
  line-height: 1.6;
}

@media (max-width: 960px) {
  .hero {
    grid-template-columns: 1fr;
  }

  .hero-title {
    font-size: 54px;
  }

  .hero-subtitle {
    font-size: 22px;
  }
}
</style>
