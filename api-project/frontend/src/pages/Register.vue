<template>
  <main class="home-page">
    <header class="brand-header">
      <h1 class="brand-title">티끌저금통</h1>
    </header>

    <section class="hero-section">
      <h2 class="title">회원가입</h2>
      <p class="desc">학부모 ID와 닉네임만 입력하면 바로 시작할 수 있어요.</p>

      <form class="entry-form" @submit.prevent="submit">
        <label class="sr-only" for="parentId">학부모 ID</label>
        <input
          id="parentId"
          v-model.trim="parentId"
          type="text"
          class="entry-input"
          placeholder="사용할 학부모 ID"
        />
        <label class="sr-only" for="nickname">닉네임</label>
        <input
          id="nickname"
          v-model.trim="nickname"
          type="text"
          class="entry-input"
          placeholder="닉네임 (예: 부모님)"
        />
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        <button type="submit" class="entry-button" :disabled="isSubmitting">
          가입하고 시작하기
        </button>
        <router-link :to="{ name: 'Home' }" class="sub-link"
          >이미 계정이 있나요? 로그인</router-link
        >
      </form>
    </section>
  </main>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useSessionStore } from '@/stores/session';

// 부모 아이콘은 현재 5번(icon5.png) 하나뿐이라 선택 없이 고정한다.
const PARENT_ICON_ID = 5;

const router = useRouter();
const session = useSessionStore();

const parentId = ref('');
const nickname = ref('');
const errorMessage = ref('');
const isSubmitting = ref(false);

async function submit() {
  if (!parentId.value || !nickname.value) {
    errorMessage.value = '학부모 ID와 닉네임을 모두 입력해주세요.';
    return;
  }
  errorMessage.value = '';
  isSubmitting.value = true;
  try {
    await session.register({
      parentId: parentId.value,
      nickname: nickname.value,
      iconId: PARENT_ICON_ID,
    });
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
  margin: 60px auto 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.title {
  font-size: 2.4rem;
  font-weight: 700;
  letter-spacing: -0.04em;
}

.desc {
  margin: 14px 0 36px;
  color: #7d8593;
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
</style>
