<template>
  <div id="stats">
    <div id="money-container">
      <div class="money-card">
        <div class="money-text">총 잔액</div>
        <div id="balance" class="money-amount">&#8361 {{ formatMoney(balance) }}</div>
      </div>
      <div class="money-card">
        <div class="money-text">이번 주 수입</div>
        <div id="income" class="money-amount">&#8361 {{ formatMoney(stats.totalIncome) }}</div>
      </div>
      <div class="money-card">
        <div class="money-text">이번 주 지출</div>
        <div id="expenditure" class="money-amount">&#8361 {{ formatMoney(stats.totalExpense) }}</div>
      </div>
    </div>
    <div id="graph-container">
      <DoughnutChart
        title="소비 균형 리포트"
        description="Need(필요) vs Want(욕구) - 횟수 기준"
        :labels="['Need', 'Want']"
        :values="[stats.needWantCount.need, stats.needWantCount.want]"
        :colors="['#4C77EF', '#ACB3B7']"
      />
      <DoughnutChart
        title="카테고리별 지출"
        description="어디에 가장 많이 썼을까요? - 금액 기준"
        :labels="categoryLabels"
        :values="stats.categoryAmounts.map((item) => item.amount)"
        :colors="CATEGORY_CHART_COLORS"
      />
    </div>
  </div>
</template>

<script setup>
import DoughnutChart from './DoughnutChart.vue';
import { CATEGORY_CHART_COLORS, EXPENSE_CATEGORIES } from '@/constants/categories';
import { formatMoney } from '@/utils/format';

// balance: 자녀의 현재 잔액, stats: 서버의 주간 통계 응답
defineProps({
  balance: { type: Number, required: true },
  stats: { type: Object, required: true },
});

const categoryLabels = EXPENSE_CATEGORIES.map((category) => category.label);
</script>

<style scoped>
#money-container {
  display: flex;
  flex-wrap: wrap;
  gap: 2em;
}

.money-card {
  background-color: white;
  flex: 1 1 260px;
  min-width: 0;
  min-height: 150px;
  border-radius: 32px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 16px;
  padding: clamp(20px, 3vw, 32px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
}

.money-text {
  color: gray;
}

.money-amount {
  font-size: clamp(28px, 4vw, 40px);
  font-weight: 700;
}

#balance {
  color: #2456cc;
}

#income {
  color: #256b4f;
}

#expenditure {
  color: #a83836;
}

#graph-container {
  display: flex;
  flex-wrap: wrap;
  gap: 2em;
}

#stats {
  display: flex;
  flex-direction: column;
  gap: 3em;
  width: 100%;
}
</style>
