<template>
  <main class="home-page">
    <header class="brand-header">
      <h1 class="brand-title">티끌저금통</h1>
    </header>

    <section class="hero-section">
      <div class="hero-copy">
        <h2 class="hero-title">
          우리 아이의 첫 번째
          <br />
          경제 습관, <span>티끌 저금통</span>
        </h2>
      </div>

      <div class="hero-visual" aria-hidden="true">
        <img src="/logo.svg" alt="" class="pig-image" />
      </div>

      <form class="entry-form" @submit.prevent="submit">
        <label class="sr-only" for="parentId">학부모 ID</label>
        <input
          id="parentId"
          v-model.trim="parentId"
          type="text"
          class="entry-input"
          placeholder="학부모 ID를 입력하세요"
        />
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        <button type="submit" class="entry-button" :disabled="isSubmitting">
          시작하기
        </button>
        <router-link :to="{ name: 'Register' }" class="sub-link"
          >처음이신가요? 회원가입</router-link
        >
      </form>
    </section>

    <section class="feature-section" aria-label="서비스 소개">
      <!-- <article class="feature-card">
        <div class="feature-icon">
          <span class="icon-circle">🛡</span>
        </div>
        <h2>안전한 자산 관리</h2>
      </article> -->

      <article class="feature-card">
        <div class="feature-icon">
          <span class="icon-circle">📖</span>
        </div>
        <h2>쉬운 경제 교육</h2>
      </article>

      <article class="feature-card">
        <div class="feature-icon">
          <span class="icon-circle">👨‍👩‍👧</span>
        </div>
        <h2>함께하는 우리 가족</h2>
      </article>
    </section>
  </main>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useSessionStore } from '@/stores/session';

const router = useRouter();
const session = useSessionStore();

const parentId = ref('');
const errorMessage = ref('');
const isSubmitting = ref(false);

async function submit() {
  if (!parentId.value) {
    errorMessage.value = '학부모 ID를 입력해주세요.';
    return;
  }
  errorMessage.value = '';
  isSubmitting.value = true;
  try {
    await session.login(parentId.value);
    router.push({ name: 'Family' });
  } catch (error) {
    errorMessage.value = error.message;
  } finally {
    isSubmitting.value = false;
  }
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  padding: 18px 28px 36px;
  background-color: #f8f9fc;
  color: #2d2d2d;
}

.brand-header {
  margin-bottom: 44px;
}

.brand-title {
  color: #4d72f8;
  font-size: 1.9rem;
  font-weight: 800;
  letter-spacing: -0.04em;
}

.hero-section {
  max-width: 760px;
  margin: 0 auto 88px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.hero-title {
  font-size: clamp(2.2rem, 5vw, 4.25rem);
  line-height: 1.18;
  letter-spacing: -0.05em;
  font-weight: 500;
}

.hero-title span {
  color: #4d72f8;
  font-weight: 700;
}

.hero-visual {
  margin: 42px 0 30px;
}

.pig-image {
  width: min(300px, 54vw);
  display: block;
  image-rendering: pixelated;
}

.entry-form {
  width: min(100%, 385px);
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.entry-input {
  width: 100%;
  border: none;
  border-radius: 999px;
  padding: 18px 24px;
  background-color: #e7edf3;
  font-size: 1rem;
  color: #313131;
  outline: none;
}

.entry-input::placeholder {
  color: #97a2ae;
  font-weight: 600;
}

.entry-input:focus {
  box-shadow: 0 0 0 3px rgba(77, 114, 248, 0.16);
}

.entry-button {
  width: 100%;
  border: none;
  border-radius: 999px;
  padding: 18px 24px;
  background: linear-gradient(90deg, #3f63ec 0%, #6787ff 100%);
  color: #ffffff;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  box-shadow: 0 10px 18px rgba(77, 114, 248, 0.22);
}

.feature-section {
  max-width: 640px;
  margin: 0 auto;
  padding-top: 46px;
  border-top: 1px solid #ebeef4;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.feature-card {
  text-align: center;
  padding: 0 8px;
}

.feature-icon {
  margin-bottom: 12px;
}

.icon-circle {
  width: 40px;
  height: 40px;
  margin: 0 auto;
  border-radius: 50%;
  display: grid;
  place-items: center;
  color: #4d72f8;
  background-color: #f2f5ff;
  font-size: 1rem;
}

.feature-card h2 {
  font-size: 0.95rem;
  font-weight: 600;
  color: #7d8593;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

@media (max-width: 768px) {
  .home-page {
    padding: 18px 20px 32px;
  }

  .brand-header {
    margin-bottom: 28px;
  }

  .brand-title {
    font-size: 1.65rem;
  }

  .hero-visual {
    margin: 34px 0 26px;
  }

  .entry-form {
    width: min(100%, 360px);
  }

  .feature-section {
    max-width: 360px;
    grid-template-columns: 1fr;
    gap: 28px;
    padding-top: 36px;
  }
}

.entry-button:disabled {
  opacity: 0.6;
  cursor: default;
}

.error-message {
  color: #a83836;
  font-size: 0.9rem;
}

.sub-link {
  color: #7d8593;
  font-size: 0.9rem;
  text-decoration: none;
}

.sub-link:hover {
  color: #4d72f8;
}
</style>
