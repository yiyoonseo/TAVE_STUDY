<template>
  <div class="transaction-item" @click="openEdit">
    <div class="left">
      <div class="icon-box">
        {{ isExpense ? '💸' : '💰' }}
      </div>
      <div class="info">
        <p class="category">{{ categoryLabel }}</p>
        <p class="memo">{{ item.memo }}</p>
        <p class="date">{{ formatDateTime(item.transactedAt) }}</p>
      </div>
    </div>
    <div class="right">
      <p :class="isExpense ? 'expense' : 'income'">
        {{ isExpense ? '-' : '+' }}{{ formatMoney(item.amount) }}원
      </p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  EXPENSE_CATEGORIES,
  INCOME_TYPES,
  labelOf,
} from '@/constants/categories';
import { formatDateTime, formatMoney } from '@/utils/format';

const props = defineProps({
  item: { type: Object, required: true },
});

const route = useRoute();
const router = useRouter();

const isExpense = computed(() => props.item.type === 'EXPENSE');
const categoryLabel = computed(() =>
  isExpense.value
    ? labelOf(EXPENSE_CATEGORIES, props.item.expenseCategory)
    : labelOf(INCOME_TYPES, props.item.incomeType),
);

// 부모/자녀 화면에 따라 수정 모달의 라우트가 다르다 (meta.role)
const openEdit = () => {
  const name =
    route.meta.role === 'parent'
      ? 'ParentEditTransaction'
      : 'ChildEditTransaction';
  router.push({
    name,
    params: { id: route.params.id, tid: props.item.transactionId },
  });
};
</script>

<style scoped>
.transaction-item {
  background: white;
  border-radius: 28px;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
  cursor: pointer;
}

.left {
  display: flex;
  align-items: center;
  gap: 14px;
  min-width: 0;
}

.icon-box {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: #eef3ff;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 24px;
}

.info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.category {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #2f2f33;
}

.memo {
  margin: 0;
  overflow-wrap: anywhere;
  font-size: 14px;
  color: #6f7782;
}

.date {
  margin: 0;
  font-size: 13px;
  color: #9aa3b2;
}

.right {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  margin-left: 12px;
}

.expense {
  margin: 0;
  font-size: 20px;
  font-weight: 800;
  color: #f15b5b;
}

.income {
  margin: 0;
  font-size: 20px;
  font-weight: 800;
  color: #4c6fff;
}

@media (max-width: 480px) {
  .transaction-item {
    padding: 14px;
  }

  .icon-box {
    width: 42px;
    height: 42px;
    font-size: 20px;
  }

  .category {
    font-size: 16px;
  }

  .expense,
  .income {
    font-size: 17px;
  }
}
</style>
