<template>
  <div class="transactionList">
    <div
      v-for="(items, date) in groupedTransactions"
      :key="date"
      class="dateGroup"
    >
      <h3 class="date">{{ date }}</h3>
      <div class="items">
        <TransactionItem
          v-for="item in items"
          :key="item.transactionId"
          :item="item"
        />
      </div>
    </div>
    <div v-if="transactions.length === 0" class="noData">
      거래 내역이 없습니다.
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import TransactionItem from '@/components/transactions/TransactionItem.vue';
import { formatDateTitle } from '@/utils/format';

// 서버가 최신순으로 정렬해서 주므로, 날짜별로 묶기만 한다.
const props = defineProps({
  transactions: { type: Array, required: true },
});

const groupedTransactions = computed(() => {
  const groups = {};
  for (const item of props.transactions) {
    const title = formatDateTitle(item.transactedAt);
    (groups[title] ??= []).push(item);
  }
  return groups;
});
</script>

<style scoped>
.transactionList {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.dateGroup {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.date {
  font-size: 15px;
  font-weight: 700;
  color: #8b95a1;
  margin: 0;
  padding-left: 8px;
}

.items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.noData {
  text-align: center;
  padding: 40px 0;
  color: #9aa3b2;
}
</style>
