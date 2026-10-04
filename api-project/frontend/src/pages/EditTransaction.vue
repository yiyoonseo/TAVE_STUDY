<template>
  <div v-if="loaded" class="modal-overlay">
    <div class="modal">
      <button class="close-btn" @click="close">✕</button>
      <h2 class="title">거래 정보 수정</h2>
      <div class="amount-box">
        <span>금액</span>
        <div class="amount-input">
          <span class="won">₩</span>
          <input v-model.number="form.amount" type="number" placeholder="0" />
        </div>
      </div>

      <div class="income-category" v-if="form.type === 'INCOME'">
        <div class="category-title">카테고리</div>
        <div class="category">
          <button
            v-for="income in INCOME_TYPES"
            :key="income.value"
            :class="{ active: form.incomeType === income.value }"
            @click="form.incomeType = income.value"
          >
            {{ income.icon }} {{ income.label }}
          </button>
        </div>
      </div>
      <div class="expenditure-category" v-else-if="form.type === 'EXPENSE'">
        <div class="category-title">카테고리</div>
        <div class="category">
          <button
            v-for="category in EXPENSE_CATEGORIES"
            :key="category.value"
            :class="{ active: form.expenseCategory === category.value }"
            @click="form.expenseCategory = category.value"
          >
            {{ category.icon }} {{ category.label }}
          </button>
        </div>
        <div class="expenditure-type">
          <div class="title">꼭 필요한가요?</div>
          <div class="toggle-wrap">
            <button
              type="button"
              class="toggle-button"
              :class="{ active: form.needType === 'NEED' }"
              @click="form.needType = 'NEED'"
            >
              필요해요
            </button>
            <button
              type="button"
              class="toggle-button"
              :class="{ active: form.needType === 'WANT' }"
              @click="form.needType = 'WANT'"
            >
              원해요
            </button>
          </div>
        </div>
      </div>

      <div class="dateTime">
        <div class="date">
          <p>일시</p>
          <input
            class="date-input"
            v-model="form.transactedAt"
            type="datetime-local"
          />
        </div>
      </div>
      <div class="memo">
        <p>메모</p>
        <textarea
          class="memo-box"
          v-model="form.memo"
          placeholder="메모 입력"
        ></textarea>
      </div>
      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
      <div class="actions">
        <button class="delete" @click="remove">삭제</button>
        <button class="save" @click="save">수정 완료</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  deleteTransaction,
  fetchTransaction,
  updateTransaction,
} from '@/api/transactions';
import { EXPENSE_CATEGORIES, INCOME_TYPES } from '@/constants/categories';
import { toApiValue, toInputValue } from '@/utils/format';

const route = useRoute();
const router = useRouter();
const emit = defineEmits(['saved']);

const transactionId = route.params.tid;
const errorMessage = ref('');
// 거래 정보를 불러오기 전에는 빈 모달이 깜빡이지 않도록 숨긴다
const loaded = ref(false);

// type은 서버에서 수정할 수 없는 값이라 화면에서는 분류 표시용으로만 쓴다
const form = reactive({
  type: '',
  amount: 0,
  incomeType: null,
  expenseCategory: null,
  needType: null,
  memo: '',
  transactedAt: '',
});

onMounted(async () => {
  try {
    const transaction = await fetchTransaction(transactionId);
    Object.assign(form, {
      type: transaction.type,
      amount: transaction.amount,
      incomeType: transaction.incomeType,
      expenseCategory: transaction.expenseCategory,
      needType: transaction.needType,
      memo: transaction.memo ?? '',
      transactedAt: toInputValue(transaction.transactedAt),
    });
  } catch (error) {
    errorMessage.value = error.message;
  } finally {
    loaded.value = true;
  }
});

const close = () => router.back();

// 수입은 incomeType, 지출은 needType·expenseCategory만 보낸다
const buildBody = () => {
  const common = {
    amount: form.amount,
    memo: form.memo,
    transactedAt: form.transactedAt ? toApiValue(form.transactedAt) : null,
  };
  return form.type === 'INCOME'
    ? { ...common, incomeType: form.incomeType }
    : { ...common, needType: form.needType, expenseCategory: form.expenseCategory };
};

const finish = () => {
  emit('saved');
  router.back();
};

const save = async () => {
  try {
    await updateTransaction(transactionId, buildBody());
    finish();
  } catch (error) {
    errorMessage.value = error.message;
  }
};

const remove = async () => {
  if (!confirm('정말로 삭제하시겠습니까?')) return;
  try {
    await deleteTransaction(transactionId);
    finish();
  } catch (error) {
    errorMessage.value = error.message;
  }
};
</script>

<style scoped>
button {
  cursor: pointer;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal {
  position: relative;
  box-sizing: border-box;
  width: min(420px, calc(100vw - 32px));
  max-height: calc(100vh - 32px);
  overflow-y: auto;
  background: white;
  padding: 24px;
  border-radius: 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 1px solid #e5e7eb;
  background: white;
}

.amount-box {
  background: #f3f4f6;
  border-radius: 16px;
  padding: 16px;
  text-align: center;
}

.amount-input {
  font-size: 28px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.amount-input input {
  border: none;
  background: transparent;
  font-size: 28px;
  font-weight: bold;
  width: 200px;
  text-align: center;
  outline: none;
}

.won {
  color: #2563eb;
}

.category {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.category button {
  padding: 12px;
  border-radius: 20px;
  border: none;
  background: #f3f4f6;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.category button.active {
  border: 2px solid #2563eb;
  background: white;
  color: #2563eb;
}

.dateTime {
  display: flex;
  justify-content: space-between;
}

.date-input {
  padding: 12px;
  border-radius: 12px;
  border: none;
  background: #f3f4f6;
  width: 180px;
}

.date > p {
  margin-bottom: 8px;
}
.inputGroup > p {
  margin-bottom: 8px;
}
.inputGroup > .time {
  padding: 12px;
  border-radius: 12px;
  border: none;
  background: #f3f4f6;
  width: 180px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 15px;
}

.inputGroup > .time > input {
  border: none;
  width: 60px;
  text-align: center;
  outline: none;
  box-sizing: border-box;
  background-color: transparent;
}
.memo {
  width: 100%;
}
.memo > p {
  margin-bottom: 8px;
}
.memo-box {
  width: 100%;
  height: 60px;
  resize: none;
  border-radius: 12px;
  border: none;
  padding: 12px;
  background: #f3f4f6;
}

.actions {
  display: flex;
  justify-content: space-between;
}

.delete {
  background: #e5e7eb;
  border: none;
  padding: 10px 20px;
  border-radius: 20px;
}

.save {
  background: #2563eb;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 20px;
}
.category-title {
  margin-bottom: 8px;
}
.toggle-wrap {
  padding: 4px;
  border-radius: 999px;
  background: #f3f4f6;
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
    color 0.2s ease;
}

.toggle-button.active {
  background: #ffffff;
  color: #4280f5;
}

.expenditure-type > .title {
  margin: 8px 0;
}

.error {
  color: #a83836;
  margin: 12px 0 0;
  font-size: 14px;
}
</style>
