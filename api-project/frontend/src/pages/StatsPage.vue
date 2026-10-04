<template>
  <div id="container">
    <AppNav />
    <div id="dashboard">
      <h1 v-if="child">
        <template v-if="role === 'parent'">
          우리 <span class="blue">{{ child.nickname }}</span
          >의 소비 통계
        </template>
        <template v-else>
          <span class="blue">{{ child.nickname }}</span> - 나의 소비 통계
        </template>
      </h1>
      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
      <Stats v-if="child && stats" :balance="child.balance" :stats="stats" />
    </div>
    <Footer />
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import AppNav from '@/components/common/AppNav.vue';
import Footer from '@/components/common/Footer.vue';
import Stats from '@/components/transactions/Stats.vue';
import { fetchChild } from '@/api/children';
import { fetchWeeklyStats } from '@/api/stats';

const route = useRoute();
const role = computed(() => route.meta.role);

const child = ref(null);
const stats = ref(null);
const errorMessage = ref('');

onMounted(async () => {
  try {
    [child.value, stats.value] = await Promise.all([
      fetchChild(route.params.id),
      fetchWeeklyStats(route.params.id),
    ]);
  } catch (error) {
    errorMessage.value = error.message;
  }
});
</script>

<style scoped>
#dashboard {
  background-color: #f7f9fb;
  padding: 4em clamp(16px, 4vw, 4em);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 50px;
  min-height: calc(100vh - 148px);
  margin: 0 auto;
  width: 100%;
  max-width: 1260px;
}

h1 {
  margin-top: 68px;
  font-weight: 600;
  width: 100%;
  padding-left: clamp(0px, 3vw, 40px);
}

#container {
  background-color: #f7f9fb;
}

.blue {
  color: #2456cc;
}

.error {
  color: #a83836;
}
</style>
