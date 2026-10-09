<template>
  <div class="home-container">
    <header class="public-nav">
      <div class="brand">
        <img src="/markrr-logo.svg" alt="" class="brand-logo" />
        <strong>Markrr</strong>
      </div>
      <nav class="nav-links">
        <a href="#how-it-works" @click.prevent="scrollToSection('how-it-works')">How it works</a>
        <a href="#features" @click.prevent="scrollToSection('features')">Features</a>
        <a href="#for-students" @click.prevent="scrollToSection('for-students')">For students</a>
      </nav>
      <div class="nav-actions">
        <router-link to="/login" class="btn-link">Log in</router-link>
        <router-link to="/login" class="btn-primary cta">
          <span>Start for free</span>
          <span aria-hidden="true" class="cta-arrow">→</span>
        </router-link>
      </div>
    </header>

    <section class="hero text-enter">
      <div class="hero-glow" aria-hidden="true"></div>
      <div class="hero-content">
        <div class="hero-pill">
          <span>NEW</span>
          Thoughtful feedback, not just a score
        </div>

        <h1 class="hero-heading">
          <span>Mark less.</span>
          <span class="hero-heading-secondary">Teach more.</span>
        </h1>

        <p>
          Markrr uses AI to assess every answer against your rubric and gives each student clear, personal feedback in seconds.
        </p>

        <div class="hero-actions">
          <router-link to="/login" class="btn-primary hero-btn">Mark your first exam <span aria-hidden="true">→</span></router-link>
          <a href="#how-it-works" class="btn-secondary hero-btn" @click.prevent="scrollToSection('how-it-works')">See how it works</a>
        </div>

        <div class="hero-meta">
          <span>Free to try</span>
          <span>No credit card</span>
          <span>Built for educators</span>
        </div>
      </div>

      <div class="hero-card" aria-label="Mock assessment result preview">
        <img
          class="hero-preview-image"
          :src="resultPreviewUrl"
          alt="Markrr assessment result preview with Biology paper marks, AI feedback, and result badges"
          width="812"
          height="447"
          fetchpriority="high"
          decoding="async"
        />
      </div>
    </section>

    <section id="features" class="section-block">
      <p class="section-eyebrow">Made for meaningful marking</p>
      <h2>Every student deserves feedback they can use.</h2>
      <div class="feature-grid">
        <article class="feature-card">
          <span class="feature-icon" aria-hidden="true">✓</span>
          <h3>Rubric-aligned marks</h3>
          <p>Set your criteria once. Responses are assessed consistently against what matters.</p>
        </article>
        <article class="feature-card feature-card-dark">
          <span class="feature-icon" aria-hidden="true">✦</span>
          <h3>Feedback per question</h3>
          <p>Students see what they did well, what they missed, and one clear way to improve.</p>
        </article>
        <article class="feature-card">
          <span class="feature-icon" aria-hidden="true">↗</span>
          <h3>Hours back each week</h3>
          <p>Upload completed papers and receive a structured, editable first pass in moments.</p>
        </article>
      </div>
    </section>

    <section id="how-it-works" class="section-block section-alt">
      <p class="section-eyebrow">Simple by design</p>
      <h2>From paper to progress in three steps.</h2>
      <div class="steps-grid">
        <article>
          <span>01</span>
          <h3>Upload your exam</h3>
          <p>Add the question paper, marking rubric, and student responses.</p>
        </article>
        <article>
          <span>02</span>
          <h3>Review AI marking</h3>
          <p>Check scores and edit any feedback before sharing it.</p>
        </article>
        <article id="for-students">
          <span>03</span>
          <h3>Help students grow</h3>
          <p>Students get clear next steps for every question.</p>
        </article>
      </div>
    </section>

    <section class="cta-strip">
      <div class="cta-strip-inner">
        <div>
          <h2>Ready to make feedback feel useful?</h2>
          <p>Give your students more than a grade. Your first exam is on us.</p>
        </div>
        <router-link to="/login" class="btn-primary cta">Get started free</router-link>
      </div>
    </section>

    <footer class="home-footer">
      <div class="brand">
        <img src="/markrr-logo.svg" alt="" class="brand-logo" />
        <strong>Markrr</strong>
      </div>
      <p>AI-assisted marking for better learning.</p>
      <p>© 2026 Markrr</p>
    </footer>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import resultPreviewUrl from '../../ResultPreview.svg'
import { useAuthStore } from '../stores/auth'
import { getDashboardRoute } from '../utils/roleRedirect'

const router = useRouter()
const authStore = useAuthStore()

const scrollToSection = (id) => {
  document.getElementById(id)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

onMounted(() => {
  if (authStore.isAuthenticated) {
    router.push(getDashboardRoute(authStore.userType))
  }
})
</script>

<style scoped>
.home-container {
  width: 100%;
  min-height: 100vh;
  background: #f7f0e1;
  color: #3b1212;
  font-family: 'Manrope', 'Segoe UI', sans-serif;
  overflow-x: clip;
}

.public-nav {
  position: relative;
  z-index: 50;
  max-width: 1178.4px;
  height: 88px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 20px 32px;
  background: rgba(247, 240, 225, 0.7);
  backdrop-filter: blur(8px);
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #3b1212;
  font-size: 20px;
  font-weight: 800;
  letter-spacing: -0.56px;
}

.brand-logo {
  width: 36px;
  height: 36px;
  display: block;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 32px;
}

.nav-links a,
.btn-link {
  color: #755e55;
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
  line-height: 20px;
}

.nav-links a:hover,
.btn-link:hover {
  color: #3b1212;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-link {
  width: 81px;
  height: 48px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 12px;
  background: transparent;
  color: #755e55;
  font-weight: 700;
}

.cta {
  text-decoration: none;
  border-radius: 12px;
  min-height: 48px;
  padding: 0 20px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 700;
  line-height: 20px;
  background: #3b1212;
  color: #ffffff;
  box-shadow: 0 6px 0 #b85b0a;
  border: none;
}

.nav-actions .cta {
  width: 152px;
  white-space: nowrap;
}

.cta-arrow {
  width: 16px;
  height: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  line-height: 1;
  color: #ffffff;
}

.hero {
  position: relative;
  max-width: 1178.4px;
  min-height: 885px;
  margin: 0 auto;
  padding-top: 0;
  text-align: center;
  overflow: hidden;
}

.hero-glow {
  position: absolute;
  left: 50%;
  top: 0;
  width: 384px;
  height: 384px;
  transform: translateX(-50%);
  border-radius: 9999px;
  background: rgba(222, 202, 160, 0.5);
  filter: blur(64px);
  opacity: 0.9;
  pointer-events: none;
}

.hero-content {
  position: relative;
  z-index: 1;
  width: min(896px, calc(100% - 24px));
  margin: 0 auto;
  padding-top: 96px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.hero-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: min(100%, 298.6px);
  height: 41.6px;
  padding: 8px 12px;
  gap: 8px;
  border: 0.8px solid #ddccb0;
  border-radius: 9999px;
  background: #ffffff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1), 0 1px 2px -1px rgba(0, 0, 0, 0.1);
  color: #3b1212;
  font-size: 12px;
  font-weight: 800;
  line-height: 16px;
}

.hero-pill span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 24px;
  padding: 4px 8px;
  border-radius: 9999px;
  background: #deca9f;
  color: #3b1212;
  font-size: 12px;
  font-weight: 800;
  line-height: 16px;
}

.hero h1 {
  margin-top: 18px;
  width: 896px;
  max-width: 100%;
  font-size: clamp(56px, 6vw, 86px);
  font-weight: 800;
  line-height: 0.96;
  letter-spacing: -4.4px;
  color: #3b1212;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 0;
}

.hero h1 span {
  display: inline-block;
}

.hero-heading-secondary {
  position: relative;
  display: inline-block;
  margin-left: 0.12em;
}

.hero-heading-secondary::after {
  content: '';
  position: absolute;
  left: 3%;
  right: 1%;
  bottom: -8px;
  height: 8px;
  border-radius: 999px;
  background: #b85b0a;
  transform: rotate(-1deg);
}

.hero p {
  margin: 22px auto 0;
  width: 720px;
  max-width: calc(100% - 40px);
  font-size: 20px;
  line-height: 32.5px;
  color: #755e55;
  font-weight: 400;
  text-align: center;
}

.hero-actions {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 12px;
  flex-wrap: wrap;
}

.hero-btn {
  min-height: 56px;
  padding: 0 28px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 700;
  line-height: 20px;
  letter-spacing: 0;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.hero-actions .btn-primary {
  width: 221px;
  background: #3b1212;
  color: #ffffff;
  box-shadow: 0 6px 0 #b85b0a;
}

.hero-actions .btn-secondary {
  width: 170.6px;
  background: #ffffff;
  border: 0.8px solid #ddccb0;
  color: #3b1212;
  box-shadow: none;
}

.hero-meta {
  margin-top: 24px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  width: 100%;
  color: #755e55;
  font-size: 12px;
  line-height: 16px;
  font-weight: 600;
  text-align: center;
}

.hero-meta span {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.hero-meta span::before {
  content: '';
  display: block;
  width: 16px;
  height: 16px;
  border-radius: 9999px;
  border: 1.2px solid #164541;
  background: rgba(22, 69, 65, 0.08);
}

.hero-card {
  position: relative;
  z-index: 2;
  width: min(812px, calc(100% - 24px));
  margin: 18px auto 0;
  padding: 0;
}

.hero-preview-image {
  display: block;
  width: 100%;
  height: auto;
}

.section-block {
  width: 100%;
  padding: 72px max(32px, calc((100% - 1114px) / 2)) 72px;
  background: #ffffff;
}

.section-block > * {
  width: 100%;
  max-width: 1114px;
  margin-right: auto;
  margin-left: auto;
}

.section-alt {
  background: #f7f0e1;
}

.section-eyebrow {
  margin: 0 0 16px;
  color: #b85b0a;
  font-size: 14px;
  line-height: 20px;
  font-weight: 800;
  letter-spacing: 2.24px;
  text-transform: uppercase;
}

.section-block h2 {
  margin: 0;
  max-width: 672px;
  color: #3b1212;
  font-size: 42px;
  line-height: 48px;
  letter-spacing: -2px;
  font-weight: 800;
}

.feature-grid,
.steps-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 24px;
  margin-top: 32px;
}

.feature-card,
.steps-grid article {
  min-width: 0;
}

.feature-card {
  min-height: 239px;
  padding: 24px;
  border: 1px solid #ddccb0;
  border-radius: 24px;
  background: #f7f0e1;
  color: #3b1212;
}

.feature-card-dark {
  border-color: #164541;
  background: #164541;
  color: #ffffff;
}

.feature-icon {
  width: 32px;
  height: 32px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 9px;
  background: #ffffff;
  color: #b85b0a;
  font-size: 16px;
  font-weight: 800;
}

.feature-card-dark .feature-icon {
  background: #deca9f;
  color: #164541;
}

.feature-card h3,
.steps-grid article h3 {
  margin: 16px 0 0;
  font-size: 16px;
  line-height: 24px;
  font-weight: 800;
}

.feature-card p,
.steps-grid article p {
  margin: 8px 0 0;
  color: #755e55;
  font-size: 14px;
  line-height: 22px;
}

.feature-card-dark p {
  color: rgba(255, 255, 255, 0.82);
}

.steps-grid article {
  min-height: 0;
  padding: 0;
  border: 0;
  border-radius: 0;
  background: transparent;
  color: #3b1212;
}

.steps-grid article span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  min-width: 36px;
  height: 36px;
  padding: 0;
  border-radius: 10px;
  background: #deca9f;
  font-size: 12px;
  line-height: 1;
  font-weight: 800;
  color: #3b1212;
}

.steps-grid article:nth-child(2) span {
  background: #164541;
  color: #ffffff;
}

.steps-grid article:nth-child(3) span {
  background: #ffffff;
  border: 1px solid #ddccb0;
}

.cta-strip {
  width: 100%;
  padding: 0 max(32px, calc((100% - 1114px) / 2)) 52px;
}

.cta-strip-inner {
  width: 100%;
  max-width: 1114px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;
  padding: 40px 48px;
  background: #164541;
  border-radius: 32px;
}

.cta-strip h2 {
  margin: 0;
  color: #ffffff;
  font-size: 36px;
  line-height: 40px;
  letter-spacing: -1.62px;
  font-weight: 800;
}

.cta-strip p {
  margin: 12px 0 0;
  color: #decaa0;
  font-size: 16px;
  line-height: 24px;
}

.cta-strip .btn-primary {
  width: 176px;
  min-height: 48px;
  padding: 0 20px;
  background: #3b1212;
  color: #ffffff;
  box-shadow: 0 6px 0 #b85b0a;
}

.home-footer {
  max-width: 1178.4px;
  margin: 0 auto;
  padding: 30px 32px 24px;
  border-top: 0.8px solid #ddccb0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.home-footer .brand {
  font-size: 20px;
}

.home-footer p {
  margin: 0;
  color: #755e55;
  font-size: 14px;
  line-height: 20px;
}

@media (max-width: 980px) {
  .public-nav {
    padding: 12px 16px;
    height: auto;
  }

  .nav-links {
    display: none;
  }

  .hero {
    min-height: auto;
  }

  .hero-content {
    padding-top: 72px;
  }

  .hero h1 {
    letter-spacing: -2px;
  }

  .hero-meta {
    flex-wrap: wrap;
    row-gap: 10px;
  }

  .hero-actions {
    align-items: center;
  }

  .cta-strip-inner {
    padding: 32px 24px;
  }

  .hero-actions .hero-btn {
    width: min(100%, 360px);
    min-width: 0;
  }

  .hero-card {
    width: min(812px, calc(100% - 32px));
    margin-top: 28px;
  }

  .feature-grid,
  .steps-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 760px) {
  .cta-strip-inner {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 640px) {
  .public-nav {
    gap: 10px;
    padding: 10px 16px;
  }

  .brand {
    gap: 7px;
    font-size: 17px;
  }

  .brand-logo {
    width: 30px;
    height: 30px;
  }

  .nav-actions {
    gap: 4px;
  }

  .btn-link {
    width: auto;
    padding: 0 8px;
  }

  .nav-actions .cta {
    width: auto;
    min-height: 42px;
    padding: 0 12px;
    font-size: 12px;
  }

  .hero-content {
    width: calc(100% - 32px);
    padding-top: 56px;
  }

  .hero-pill {
    width: auto;
    max-width: 100%;
    height: auto;
    min-height: 38px;
    font-size: 11px;
  }

  .hero h1 {
    font-size: clamp(42px, 11vw, 56px);
    line-height: 1;
    letter-spacing: -2.4px;
  }

  .hero-heading-secondary::after {
    bottom: -5px;
    height: 5px;
  }

  .hero p {
    width: 100%;
    max-width: 440px;
    font-size: 16px;
    line-height: 25px;
  }

  .hero-actions {
    width: 100%;
    gap: 10px;
  }

  .hero-actions .hero-btn {
    width: min(100%, 340px);
    min-height: 50px;
  }

  .hero-meta {
    gap: 8px 14px;
    font-size: 11px;
  }

  .hero-card {
    width: calc(100% - 24px);
    margin-top: 24px;
  }


  .section-block {
    padding: 56px 20px;
  }

  .section-block h2 {
    font-size: 32px;
    line-height: 38px;
    letter-spacing: -1.2px;
  }

  .feature-grid,
  .steps-grid {
    gap: 16px;
    margin-top: 24px;
  }

  .feature-card {
    min-height: 0;
    padding: 20px;
  }

  .steps-grid article {
    padding: 4px 0 8px;
  }

  .cta-strip {
    padding: 0 20px 36px;
  }

  .cta-strip-inner {
    flex-direction: column;
    align-items: flex-start;
    padding: 28px 24px;
    border-radius: 24px;
  }

  .cta-strip h2 {
    font-size: 26px;
    line-height: 32px;
    letter-spacing: -1px;
  }

  .cta-strip p {
    font-size: 14px;
    line-height: 21px;
  }

  .home-footer {
    flex-wrap: wrap;
    justify-content: flex-start;
    padding: 24px 20px;
  }

  .home-footer p {
    font-size: 12px;
  }
}
</style>
