<template>
  <div class="home-container">
    <header class="public-nav">
      <div class="brand">
        <img src="/markrr-logo.svg" alt="" class="brand-logo" />
        <strong>Markrr</strong>
      </div>
      <nav class="nav-links">
        <a href="#how-it-works">How it works</a>
        <a href="#features">Features</a>
        <a href="#for-students">For students</a>
      </nav>
      <div class="nav-actions">
        <router-link to="/login" class="btn-link">Log in</router-link>
        <router-link to="/login" class="btn-primary cta">Start for free</router-link>
      </div>
    </header>

    <section class="hero text-enter">
      <div class="hero-pill">
        <span>NEW</span>
        Thoughtful feedback, not just a score
      </div>
      <h1>Mark less. <span class="hero-underline">Teach more.</span></h1>
      <p>
        Markrr uses AI to assess every answer against your rubric
        and gives each student clear, personal feedback in seconds.
      </p>
      <div class="hero-actions">
        <router-link to="/login" class="btn-primary hero-btn px-6 py-4">Mark your first exam <span aria-hidden="true">→</span></router-link>
        <a href="#how-it-works" class="btn-secondary hero-btn px-6 py-4">See how it works</a>
      </div>
      <div class="hero-meta">
        <span>Free to try</span>
        <span>No credit card</span>
        <span>Built for educators</span>
      </div>

      <div class="hero-card">
        <img
          :src="landingHeroImage"
          alt="Assessment feedback preview"
          @error="onPreviewImageError($event, fallbackPreviewImage)"
        />
        <div class="hero-card-badge">Feedback ready</div>
      </div>
    </section>

    <section id="features" class="section-block">
      <p class="section-eyebrow">Made for meaningful marking</p>
      <h2>Every student deserves feedback they can use.</h2>
      <div class="feature-grid">
        <article class="feature-card">
          <h3>Rubric-aligned marks</h3>
          <p>Set your criteria once. Responses are assessed consistently against what matters.</p>
        </article>
        <article class="feature-card feature-card-dark">
          <h3>Feedback per question</h3>
          <p>Students see what they did well, what they missed, and one clear way to improve.</p>
        </article>
        <article class="feature-card">
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
      <div>
        <h2>Ready to make feedback feel useful?</h2>
        <p>Give your students more than a grade. Your first exam is on us.</p>
      </div>
      <router-link to="/login" class="btn-primary cta">Get started free</router-link>
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
import { useAuthStore } from '../stores/auth'
import { getDashboardRoute } from '../utils/roleRedirect'

const router = useRouter()
const authStore = useAuthStore()

const landingHeroImage = new URL('../assets/ui-images/download (7).jpg', import.meta.url).href
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
  padding: 20px clamp(18px, 4vw, 42px) 64px;
  max-width: 1280px;
  margin: 0 auto;
}

.public-nav {
  position: sticky;
  top: 14px;
  z-index: 20;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  background: rgba(255, 250, 240, 0.88);
  border: 1px solid rgba(116, 83, 64, 0.16);
  border-radius: 16px;
  padding: 12px 16px;
  backdrop-filter: blur(8px);
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #2f1711;
  font-size: 22px;
}

.brand-logo {
  width: 32px;
  height: 32px;
  display: block;
}

.nav-links {
  display: flex;
  gap: 24px;
}

.nav-links a,
.btn-link {
  color: #684d3e;
  text-decoration: none;
  font-weight: 500;
}

.nav-links a:hover,
.btn-link:hover {
  color: #c36400;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 14px;
}

.cta {
  text-decoration: none;
  border-radius: 12px;
  min-height: 44px;
  padding: 11px 18px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.nav-actions .cta {
  width: 124px;
  min-height: 44px;
  padding: 10px 14px;
  white-space: nowrap;
}

.hero {
  margin-top: 74px;
  text-align: center;
}

.hero-pill {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  border: 1px solid rgba(116, 83, 64, 0.22);
  padding: 7px 14px;
  border-radius: 999px;
  background: #fffef9;
  color: #4f3024;
  font-size: 14px;
}

.hero-pill span {
  background: #f0d69b;
  color: #5c351f;
  border-radius: 999px;
  padding: 2px 8px;
  font-size: 12px;
  font-weight: 700;
}

.hero h1 {
  margin-top: 22px;
  font-size: clamp(48px, 8vw, 88px);
  font-weight: 800;
  line-height: 0.96;
  color: #471815;
}

.hero-underline {
  position: relative;
  display: inline-block;
}

.hero-underline::after {
  content: '';
  position: absolute;
  left: 2%;
  right: -1%;
  bottom: -8px;
  height: 7px;
  border-radius: 999px;
  background: #c36400;
  transform: rotate(-1deg);
}

.hero p {
  margin: 22px auto 0;
  max-width: 620px;
  font-size: clamp(18px, 2vw, 25px);
  color: #6a4f41;
}

.hero-actions {
  margin-top: 28px;
  display: flex;
  justify-content: center;
  gap: 10px;
}

.hero-btn {
  min-width: 0;
  min-height: 50px;
  padding: 12px 18px;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
}

.hero-actions .btn-primary {
  width: auto;
  min-width: 238px;
}

.hero-actions .btn-secondary {
  width: auto;
  min-width: 198px;
}

.hero-meta {
  margin-top: 16px;
  display: flex;
  justify-content: center;
  gap: 18px;
  color: #7c5f51;
  font-size: 14px;
}

.hero-card {
  margin: 46px auto 0;
  max-width: 620px;
  border: 6px solid #4c1a18;
  border-radius: 28px;
  background: #f9edd4;
  padding: 10px;
  box-shadow: 0 36px 64px rgba(75, 29, 18, 0.16);
  position: relative;
}

.hero-card img {
  width: 100%;
  height: auto;
  object-fit: contain;
  display: block;
  border-radius: 16px;
}

.hero-card-badge {
  position: absolute;
  right: -26px;
  bottom: -20px;
  background: #e6c98c;
  color: #492116;
  border-radius: 14px;
  padding: 12px 16px;
  font-weight: 700;
  border: 1px solid rgba(116, 83, 64, 0.28);
}

.section-block {
  margin-top: 72px;
  background: #fffefb;
  border: 1px solid rgba(116, 83, 64, 0.14);
  border-radius: 30px;
  padding: clamp(28px, 5vw, 56px);
}

.section-eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.18em;
  color: #bd680a;
  font-size: 13px;
  font-weight: 700;
}

.section-block h2 {
  margin-top: 14px;
  font-size: clamp(34px, 5vw, 58px);
  color: #3a1713;
  line-height: 1.04;
}

.feature-grid {
  margin-top: 24px;
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}

.feature-card {
  background: #f7ecd3;
  border: 1px solid rgba(116, 83, 64, 0.14);
  border-radius: 20px;
  padding: 22px;
}

.feature-card h3 {
  font-size: 22px;
  color: #351610;
}

.feature-card p {
  margin-top: 10px;
  color: #6a4e40;
}

.feature-card-dark {
  background: #1f5a54;
}

.feature-card-dark h3,
.feature-card-dark p {
  color: #f6f8ea;
}

.section-alt {
  background: #f8edd6;
}

.steps-grid {
  margin-top: 26px;
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}

.steps-grid article {
  border-top: 1px solid rgba(116, 83, 64, 0.24);
  padding-top: 16px;
}

.steps-grid span {
  background: rgba(224, 194, 133, 0.55);
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  padding: 4px 10px;
  color: #503126;
}

.steps-grid h3 {
  margin-top: 12px;
  color: #3a1913;
}

.steps-grid p {
  margin-top: 8px;
  color: #715548;
}

.cta-strip {
  margin-top: 54px;
  border-radius: 24px;
  padding: clamp(22px, 4vw, 38px);
  background: #1c5a54;
  color: #f2f4e7;
  display: flex;
  justify-content: space-between;
  gap: 18px;
  align-items: center;
}

.cta-strip h2 {
  font-size: clamp(28px, 4vw, 42px);
}

.cta-strip p {
  color: #d6decf;
  margin-top: 8px;
}

.home-footer {
  margin-top: 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  color: #715548;
  font-size: 14px;
  border-top: 1px solid rgba(116, 83, 64, 0.16);
  padding: 18px 6px 4px;
}

.home-footer .brand {
  font-size: 20px;
}

@media (max-width: 980px) {
  .nav-links {
    display: none;
  }

  .cta-strip,
  .hero-actions,
  .hero-meta,
  .home-footer {
    flex-direction: column;
    align-items: stretch;
  }

  .hero-actions .hero-btn {
    width: 100%;
  }

  .hero-card-badge {
    position: static;
    margin: 12px auto 0;
    width: fit-content;
  }
}
</style>
