<template>
  <AppNav />
  <div class="transactions-page">
    <div class="transactions-card">
      <div class="top-area">
        <div class="top-content">
          <h1 v-if="child">
            <template v-if="role === 'parent'">
              우리 <span class="blue">{{ child.nickname }}</span
              >의 거래 내역
            </template>
            <template v-else>
              <span class="blue">{{ child.nickname }}</span> - 나의 거래 내역
            </template>
          </h1>
          <div class="filter-btns">
            <button
              v-for="filter in FILTERS"
              :key="filter.label"
              :class="{ active: filterType === filter.type }"
              @click="filterType = filter.type"
            >
              {{ filter.label }}
            </button>
          </div>
          <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
          <TransactionList :transactions="transactions" />
        </div>
      </div>
    </div>
  </div>
  <!-- 거래 수정 모달 (중첩 라우트). 저장·삭제가 끝나면 목록을 다시 불러온다 -->
  <RouterView @saved="load" />
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import AppNav from '@/components/common/AppNav.vue';
import TransactionList from '@/components/transactions/TransactionList.vue';
import { fetchChild } from '@/api/children';
import { fetchTransactions } from '@/api/transactions';

// type이 undefined면 서버가 전체를 돌려준다
const FILTERS = [
  { label: '전체', type: undefined },
  { label: '수입', type: 'INCOME' },
  { label: '지출', type: 'EXPENSE' },
];

const route = useRoute();
const role = computed(() => route.meta.role);
const childId = route.params.id;

const child = ref(null);
const transactions = ref([]);
const filterType = ref(undefined);
const errorMessage = ref('');

const load = async () => {
  try {
    errorMessage.value = '';
    [child.value, transactions.value] = await Promise.all([
      fetchChild(childId),
      fetchTransactions(childId, filterType.value),
    ]);
  } catch (error) {
    errorMessage.value = error.message;
  }
};

onMounted(load);
watch(filterType, load);
</script>

<style scoped>
.transactions-page {
  min-height: 100vh;
  background: #f7f9fb;
  padding: 32px 20px;
  padding-top: 80px;
  box-sizing: border-box;
}

.transactions-card {
  max-width: 820px;
  margin: 0 auto;
}

.top-area {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 28px;
}

.top-content {
  width: 100%;
}

h1 {
  margin: 0;
  color: #2f2f33;
  padding-top: 40px;
  padding-bottom: 20px;
}

.filter-btns {
  display: flex;
  gap: 12px;
  margin: 16px 0 20px;
}

.filter-btns button {
  border: none;
  background: white;
  padding: 10px 18px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
}

.filter-btns button.active {
  background: #4466ea;
  color: white;
}

.blue {
  color: #2456cc;
}

.error {
  color: #a83836;
}
</style>
