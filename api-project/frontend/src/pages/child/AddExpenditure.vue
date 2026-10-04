<template>
  <main class="expenditure-page">
    <AppNav />
    <section class="hero">
      <p class="hero-label">NEW TRANSACTION</p>
      <h1 class="hero-title">얼마를 썼나요?</h1>
    </section>

    <section class="content-grid">
      <article class="amount-card">
        <p class="amount-caption">CURRENT AMOUNT</p>
        <div class="amount-display">
          <input
            v-if="isEditingAmount"
            ref="amountEditRef"
            :value="amountInput"
            type="text"
            inputmode="numeric"
            class="amount-edit-input"
            @input="amountInput = $event.target.value.replace(/\D/g, '')"
            @blur="finishAmountEdit"
            @keyup.enter="finishAmountEdit"
          />
          <strong v-else class="amount-number" @click="startAmountEdit">
            {{ formatMoney(amount) }}
          </strong>
        </div>

        <div class="quick-amount-panel">
          <p class="quick-amount-title">↩ 빠른 금액 선택</p>
          <div class="quick-amount-grid">
            <button
              v-for="value in QUICK_AMOUNTS"
              :key="value"
              type="button"
              class="pill-button"
              @click="amount += value"
            >
              + {{ formatMoney(value) }}
            </button>
            <button
              type="button"
              class="pill-button primary"
              @click="amount = 0"
            >
              초기화
            </button>
          </div>
        </div>

        <div class="input-row">
          <div class="field">
            <span class="field-label">DATE</span>
            <div class="field-box date-box">
              <span class="field-icon">◦</span>
              <span>{{ today }}</span>
            </div>
          </div>
          <div class="field">
            <span class="field-label">MEMO</span>
            <textarea
              v-model="memo"
              class="memo-input"
              rows="1"
              maxlength="100"
              placeholder="메모를 입력하세요"
            />
          </div>
        </div>
      </article>

      <aside class="category-card">
        <section>
          <p class="panel-title">⌂ 카테고리 선택</p>
          <div class="category-grid">
            <button
              v-for="category in EXPENSE_CATEGORIES"
              :key="category.value"
              type="button"
              class="category-button"
              :class="{ active: selectedCategory === category.value }"
              @click="selectedCategory = category.value"
            >
              <span class="category-icon" :class="category.color">
                {{ category.icon }}
              </span>
              <span class="category-name">{{ category.label }}</span>
            </button>
          </div>
        </section>

        <section class="need-section">
          <p class="question-title">꼭 필요한가요?</p>
          <p class="question-subtitle">소비의 성격을 선택해주세요</p>
          <div class="toggle-wrap">
            <button
              type="button"
              class="toggle-button"
              :class="{ active: selectedNeedType === 'NEED' }"
              @click="selectedNeedType = 'NEED'"
            >
              필요해요
            </button>
            <button
              type="button"
              class="toggle-button"
              :class="{ active: selectedNeedType === 'WANT' }"
              @click="selectedNeedType = 'WANT'"
            >
              원해요
            </button>
          </div>
        </section>

        <div class="action-area">
          <p v-if="errorMessage" class="status-text error">
            {{ errorMessage }}
          </p>
          <button
            type="button"
            class="submit-button"
            :disabled="isSubmitting"
            @click="submit"
          >
            지출 기록하기
          </button>
        </div>
      </aside>
    </section>
  </main>
</template>

<script setup>
import { nextTick, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import AppNav from '@/components/common/AppNav.vue';
import { createExpense } from '@/api/transactions';
import { EXPENSE_CATEGORIES } from '@/constants/categories';
import { formatDateTime, formatMoney } from '@/utils/format';

const QUICK_AMOUNTS = [100, 1000, 2000, 5000, 10000];

const route = useRoute();
const router = useRouter();

const amount = ref(0);
const memo = ref('');
const selectedCategory = ref('MEAL');
const selectedNeedType = ref('NEED');
const errorMessage = ref('');
const isSubmitting = ref(false);
const today = formatDateTime(new Date());

// 금액 숫자를 클릭하면 직접 입력할 수 있다
const isEditingAmount = ref(false);
const amountInput = ref('');
const amountEditRef = ref(null);

const startAmountEdit = async () => {
  amountInput.value = String(amount.value);
  isEditingAmount.value = true;
  await nextTick();
  amountEditRef.value?.select();
};

const finishAmountEdit = () => {
  amount.value = Number(amountInput.value) || 0;
  isEditingAmount.value = false;
};

const submit = async () => {
  if (amount.value <= 0) {
    errorMessage.value = '지출 금액을 먼저 입력해주세요.';
    return;
  }

  errorMessage.value = '';
  isSubmitting.value = true;
  try {
    await createExpense(route.params.id, {
      needType: selectedNeedType.value,
      expenseCategory: selectedCategory.value,
      amount: amount.value,
      memo: memo.value.trim(),
    });
    router.push({ name: 'ChildTransactions', params: { id: route.params.id } });
  } catch (error) {
    // 잔액 부족 등 서버가 내려준 메시지를 그대로 보여준다
    errorMessage.value = error.message;
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style scoped>
.expenditure-page {
  min-height: 100vh;
  padding-bottom: 30px;
  background-color: #f7f9fd;
  color: #24324f;
}

.hero {
  padding: 106px 20px 30px;
  text-align: center;
}

.hero-label {
  margin-bottom: 14px;
  color: #4f87ff;
  font-size: 0.68rem;
  font-weight: 700;
  letter-spacing: 0.18em;
}

.hero-title {
  margin-bottom: 12px;
  font-size: clamp(2rem, 3.4vw, 3rem);
  font-weight: 700;
  letter-spacing: -0.04em;
}

.hero-subtitle {
  color: #a4b1c8;
  font-size: 0.92rem;
}

.content-grid {
  width: min(1180px, calc(100% - 48px));
  margin: 0 auto;
  display: grid;
  grid-template-columns: minmax(0, 2.2fr) minmax(300px, 1fr);
  gap: 18px;
}

.amount-card,
.category-card,
.footer-quote {
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 22px 45px rgba(90, 116, 173, 0.08);
}

.amount-card {
  padding: 58px 30px 34px;
}

.amount-caption {
  text-align: center;
  color: #b0bdd3;
  font-size: 0.7rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  margin-bottom: 28px;
}

.direct-input-section {
  margin-bottom: 28px;
}

.direct-input {
  width: 100%;
  height: 48px;
  border: 2px solid #e3e9f3;
  border-radius: 18px;
  padding: 0 18px;
  font-size: 1rem;
  font-weight: 700;
  color: #345ea7;
  text-align: center;
  background: #f4f7fc;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}

.direct-input:focus {
  outline: none;
  border-color: #4b85f7;
  box-shadow: 0 0 0 3px rgba(75, 133, 247, 0.16);
}

.direct-input::placeholder {
  color: #b1bdd2;
}

.amount-number {
  display: block;
  text-align: center;
  color: #345ea7;
  font-size: clamp(3rem, 5vw, 4.4rem);
  font-weight: 800;
  letter-spacing: -0.04em;
  cursor: pointer;
  transition: opacity 0.2s ease;
}

.amount-edit-input {
  width: min(100%, 360px);
  border: none;
  border-bottom: 3px solid #4b85f7;
  background: transparent;
  color: #345ea7;
  font-size: clamp(2.6rem, 4.8vw, 4.1rem);
  font-weight: 800;
  letter-spacing: -0.04em;
  text-align: center;
  outline: none;
}

.amount-number:hover {
  opacity: 0.8;
}

.amount-display {
  margin-top: 16px;
  display: flex;
  justify-content: center;
  overflow: hidden;
  min-height: 5rem;
  align-items: center;
}

.quick-amount-panel {
  margin-top: 44px;
  padding: 26px 24px;
  border-radius: 18px;
  background: #f4f7fc;
}

.quick-amount-title {
  margin-bottom: 24px;
  color: #6f7f9c;
  font-size: 0.92rem;
  font-weight: 600;
}

.quick-amount-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.pill-button {
  height: 44px;
  border: 1px solid #e3e9f3;
  border-radius: 999px;
  background: #ffffff;
  color: #53637f;
  font-size: 1rem;
  font-weight: 700;
  box-shadow: 0 3px 10px rgba(182, 194, 218, 0.14);
  cursor: pointer;
  transition:
    box-shadow 0.18s ease,
    background-color 0.18s ease;
}

.pill-button.primary {
  border-color: #315fab;
  background: #355fa8;
  color: #ffffff;
  box-shadow: 0 10px 20px rgba(59, 99, 173, 0.24);
}

.input-row {
  margin-top: 42px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.field-label {
  color: #b1bdd2;
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.field-box {
  min-height: 34px;
  border-radius: 999px;
  background: #f4f7fc;
  padding: 0 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6f809d;
  font-size: 0.92rem;
}

.date-box {
  min-height: 46px;
}

.memo-input {
  min-height: 46px;
  border: none;
  border-radius: 18px;
  background: #f4f7fc;
  padding: 12px 14px;
  color: #6f809d;
  font-size: 0.92rem;
  resize: none;
  outline: none;
}

.memo-input:focus {
  box-shadow: 0 0 0 2px rgba(75, 133, 247, 0.16);
}

.field-icon {
  color: #95a5c2;
  font-size: 1rem;
}

.category-card {
  padding: 24px 24px 22px;
}

.panel-title {
  margin-bottom: 18px;
  color: #5d6e8e;
  font-size: 0.94rem;
  font-weight: 700;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.category-button {
  min-width: 0;
  min-height: 84px;
  border: none;
  border-radius: 16px;
  background: #f7f9fd;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #7d8aa3;
  transition:
    background-color 0.2s ease,
    box-shadow 0.2s ease,
    color 0.2s ease;
  cursor: pointer;
}

.category-button.active {
  background: #ffffff;
  box-shadow:
    inset 0 0 0 2px #4b85f7,
    0 12px 18px rgba(85, 125, 208, 0.14);
  color: #3e74e8;
}

.category-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  font-size: 1.1rem;
  font-weight: 700;
}

.category-icon.orange {
  color: #ff9800;
  background: #fff8ef;
}

.category-icon.yellow {
  color: #e1a700;
  background: #fff9e8;
}

.category-icon.beige {
  color: #d4a574;
  background: #fef5f0;
}

.category-icon.sky {
  color: #5dade2;
  background: #eaf4fb;
}

.category-icon.green {
  color: #2fa86b;
  background: #edf9f2;
}

.category-icon.gray {
  color: #b2bdd1;
  background: #f0f3f8;
}

.category-button.active .category-icon {
  background: #4b85f7;
  color: #ffffff;
}

.category-name {
  font-size: 0.9rem;
  font-weight: 600;
}

.need-section {
  margin-top: 26px;
  padding-top: 6px;
}

.question-title {
  margin-bottom: 8px;
  color: #4f5f7d;
  font-size: 0.96rem;
  font-weight: 700;
}

.question-subtitle {
  margin-bottom: 16px;
  color: #b1bdd0;
  font-size: 0.82rem;
}

.toggle-wrap {
  padding: 4px;
  border-radius: 999px;
  background: #eef2f8;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 6px;
}

.toggle-button {
  min-height: 28px;
  border: none;
  border-radius: 999px;
  background: transparent;
  color: #6d7f9b;
  font-size: 0.8rem;
  font-weight: 700;
  cursor: pointer;
  transition:
    background-color 0.2s ease,
    color 0.2s ease,
    box-shadow 0.2s ease;
}

.toggle-button.active {
  background: #ffffff;
  color: #4280f5;
  box-shadow: 0 6px 14px rgba(75, 133, 247, 0.14);
}

.action-area {
  margin-top: 22px;
  padding-top: 18px;
  border-top: 1px solid #edf1f7;
}

.status-text {
  margin-bottom: 12px;
  padding: 12px 14px;
  border-radius: 14px;
  font-size: 0.88rem;
  font-weight: 700;
  text-align: center;
}

.status-text.success {
  background: #e7f8ed;
  color: #287548;
}

.status-text.error {
  background: #fdecec;
  color: #c24747;
}

.submit-button {
  width: 100%;
  height: 48px;
  border: none;
  border-radius: 999px;
  background: linear-gradient(90deg, #2f6be8 0%, #67a1ff 100%);
  color: #ffffff;
  font-size: 1rem;
  font-weight: 700;
  box-shadow: 0 14px 24px rgba(72, 122, 228, 0.24);
  cursor: pointer;
}

.cancel-button {
  width: 100%;
  margin-top: 12px;
  border: none;
  background: transparent;
  color: #b1b9c7;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
}

.footer-quote {
  width: min(1180px, calc(100% - 48px));
  margin: 24px auto 18px;
  padding: 38px 24px;
  text-align: center;
  color: #b8c3d5;
  font-size: 0.92rem;
}

@media (max-width: 960px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .content-grid,
  .footer-quote {
    width: min(100% - 24px, 100%);
  }

  .amount-card {
    padding: 34px 18px 24px;
  }

  .quick-amount-grid,
  .input-row,
  .category-grid {
    grid-template-columns: 1fr;
  }
}
</style>
