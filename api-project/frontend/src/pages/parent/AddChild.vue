<template>
  <main class="add-child-page">
    <section class="card">
      <div class="hero-icon" aria-hidden="true">
        <img src="@/assets/images/plus2.png" alt="" />
      </div>

      <div class="title-wrap">
        <h1>새로운 자녀 등록하기</h1>
        <p>우리 아이의 첫 경제 습관, 티끌 저금으로 시작해요!</p>
      </div>

      <form class="form" @submit.prevent="submitChild">
        <label class="field-label" for="childName">아이 이름</label>
        <input
          id="childName"
          v-model.trim="childName"
          type="text"
          class="name-input"
          maxlength="12"
          placeholder="아이의 이름을 입력해주세요"
        />

        <div class="field-group">
          <span class="field-label">아바타 선택</span>
          <div class="icon-list">
            <button
              v-for="icon in childIcons"
              :key="icon.id"
              type="button"
              class="icon-button"
              :class="{ selected: selectedIconId === icon.id }"
              @click="selectedIconId = icon.id"
            >
              <img :src="icon.src" :alt="`아이콘 ${icon.id}`" />
            </button>
          </div>
        </div>

        <p v-if="errorMessage" class="message error">{{ errorMessage }}</p>

        <div class="action-row">
          <button type="button" class="cancel-btn" @click="goBack">취소</button>
          <button type="submit" class="submit-btn" :disabled="isSubmitting">
            {{ isSubmitting ? '등록 중...' : '등록하기' }}
          </button>
        </div>
      </form>
    </section>
  </main>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { createChild } from '@/api/children';
import { useSessionStore } from '@/stores/session';
import { iconPath } from '@/utils/format';

const router = useRouter();
const session = useSessionStore();

const childName = ref('');
const selectedIconId = ref(1);
const isSubmitting = ref(false);
const errorMessage = ref('');

// 자녀 아이콘은 1~4번 (서버 검증 범위와 같음). 선택용 이미지는 icons2 폴더
const childIcons = [1, 2, 3, 4].map((id) => ({
  id,
  src: iconPath(id, 'icons2'),
}));

const goBack = () => router.push({ name: 'Family' });

const submitChild = async () => {
  if (!childName.value) {
    errorMessage.value = '아이 이름을 입력해주세요.';
    return;
  }

  errorMessage.value = '';
  isSubmitting.value = true;
  try {
    await createChild(session.parent.parentId, {
      nickname: childName.value,
      iconId: selectedIconId.value,
    });
    router.push({ name: 'Family' });
  } catch (error) {
    errorMessage.value = error.message;
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style scoped>
.add-child-page {
  min-height: 100vh;
  padding: 40px 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f7f9fb;
}

.card {
  width: min(100%, 560px);
  padding: 42px 32px 36px;
  border-radius: 36px;
  background-color: rgba(255, 255, 255, 0.96);
  box-shadow: 0 24px 50px rgba(101, 123, 151, 0.18);
}

.hero-icon {
  width: 58px;
  height: 58px;
  margin: 0 auto 20px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: linear-gradient(180deg, #edf5ff 0%, #d7e8ff 100%);
}

.hero-icon img {
  width: 28px;
  height: 28px;
}

.title-wrap {
  text-align: center;
  margin-bottom: 30px;
}

.title-wrap h1 {
  margin: 0 0 10px;
  color: #303949;
  font-size: clamp(1.7rem, 4vw, 2.1rem);
  font-weight: 800;
  letter-spacing: -0.04em;
}

.title-wrap p {
  margin: 0;
  color: #8a93a3;
  font-size: 0.96rem;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.field-label {
  color: #5f9ee9;
  font-size: 0.92rem;
  font-weight: 700;
}

.name-input {
  width: 100%;
  border: none;
  border-radius: 999px;
  padding: 18px 22px;
  background-color: #eef2f6;
  color: #2f3644;
  font-size: 1rem;
  outline: none;
}

.name-input::placeholder {
  color: #b0b8c5;
}

.name-input:focus {
  box-shadow: 0 0 0 3px rgba(85, 148, 255, 0.18);
}

.icon-list {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.icon-button {
  width: 100%;
  aspect-ratio: 1 / 1;
  border: none;
  border-radius: 50%;
  background-color: #f1f4f8;
  cursor: pointer;
}

.icon-button img {
  width: 62%;
  height: 62%;
  object-fit: contain;
}

.icon-button.selected {
  background-color: #ffffff;
  box-shadow:
    0 0 0 5px #e9f1ff,
    0 0 0 10px rgba(94, 156, 255, 0.35);
}

.message {
  margin: -6px 0 0;
  font-size: 0.92rem;
}

.error {
  color: #df5a5a;
}

.action-row {
  display: flex;
  gap: 14px;
  margin-top: 4px;
}

.action-row button {
  flex: 1;
  border: none;
  border-radius: 999px;
  padding: 16px 18px;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
}

.cancel-btn {
  background-color: #dfe5ec;
  color: #596271;
}

.submit-btn {
  color: #ffffff;
  background: linear-gradient(90deg, #3b7fe5 0%, #69a6ff 100%);
  box-shadow: 0 12px 24px rgba(65, 132, 233, 0.28);
}

.submit-btn:disabled {
  cursor: wait;
  opacity: 0.72;
}

@media (max-width: 640px) {
  .add-child-page {
    padding: 20px 14px;
  }

  .card {
    padding: 34px 20px 24px;
    border-radius: 28px;
  }

  .icon-list {
    gap: 10px;
  }

  .action-row {
    flex-direction: column;
  }
}
</style>
