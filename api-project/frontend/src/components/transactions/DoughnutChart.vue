<template>
  <div class="my-chart">
    <div class="chart-title">{{ title }}</div>
    <div class="chart-desc">{{ description }}</div>
    <Doughnut v-if="hasData" :data="chartData" :options="chartOptions" />
    <div v-else class="chart-empty">이번 주 지출이 없어요.</div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { Doughnut } from 'vue-chartjs';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import ChartDataLabels from 'chartjs-plugin-datalabels';

ChartJS.register(ArcElement, Tooltip, Legend, ChartDataLabels);

const props = defineProps({
  title: { type: String, required: true },
  description: { type: String, default: '' },
  labels: { type: Array, required: true },
  values: { type: Array, required: true },
  colors: { type: Array, required: true },
});

const hasData = computed(() => props.values.some((value) => value > 0));

const chartData = computed(() => ({
  labels: props.labels,
  datasets: [
    {
      backgroundColor: props.colors,
      data: props.values,
      offset: 3,
      hoverOffset: 35,
    },
  ],
}));

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  layout: { padding: 30 },
  plugins: {
    tooltip: { enabled: false },
    legend: {
      position: 'bottom',
      onClick: () => false,
      labels: { usePointStyle: true, pointStyle: 'rect', font: { size: 14 } },
    },
    datalabels: {
      // 0인 항목은 라벨을 숨기고, 나머지는 비율(%)로 표시
      formatter: (value, ctx) => {
        if (value === 0) return '';
        const sum = ctx.chart.data.datasets[0].data.reduce((a, b) => a + b, 0);
        return ((value * 100) / sum).toFixed(1) + '%';
      },
      color: 'white',
      font: { weight: 'bold', size: 20 },
    },
  },
};
</script>

<style scoped>
.my-chart {
  height: 478px;
  flex: 1 1 340px;
  max-width: 536px;
  min-width: 0;
  background-color: white;
  border-radius: 48px;
  padding: clamp(20px, 3vw, 40px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
}

.chart-title {
  font-size: 24px;
  font-weight: 500;
}

.chart-desc {
  color: gray;
}

.chart-empty {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
}

@media (max-width: 520px) {
  .my-chart {
    height: 420px;
    border-radius: 32px;
  }
}
</style>
