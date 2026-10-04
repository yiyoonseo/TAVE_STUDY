<template>
  <header class="header">
    <router-link :to="{ name: 'Family' }" class="logo">티끌저금통</router-link>
    <nav class="nav">
      <router-link :to="{ name: 'Family' }">Home</router-link>
      <template v-if="role === 'parent'">
        <router-link :to="{ name: 'ParentDashboard' }">Dashboard</router-link>
      </template>
      <template v-else-if="role === 'child'">
        <router-link :to="{ name: 'ChildDashboard', params: { id: childId } }"
          >Dashboard</router-link
        >
        <router-link :to="{ name: 'ChildTransactions', params: { id: childId } }"
          >Transaction</router-link
        >
        <router-link :to="{ name: 'AddExpenditure', params: { id: childId } }"
          >Expenditure</router-link
        >
      </template>
    </nav>
    <div class="logout">
      <img
        src="@/assets/images/logout.png"
        class="logout-btn"
        alt="로그아웃"
        @click="logout"
      />
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useSessionStore } from '@/stores/session';

const route = useRoute();
const router = useRouter();
const session = useSessionStore();

const role = computed(() => route.meta.role);
const childId = computed(() => route.params.id);

const logout = () => {
  session.logout();
  router.push('/');
};
</script>

<style scoped>
.header {
  position: fixed;
  height: 68px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 40px;
  background: white;
  border-bottom: 1px solid #e5e7eb;
  width: 100%;
  z-index: 10;
}

.logo {
  font-size: 22px;
  font-weight: 700;
  text-decoration: none;
  color: #2563eb;
}

.nav {
  display: flex;
  gap: 40px;
}

.nav a {
  text-decoration: none;
  color: #6b7280;
  font-weight: 500;
}

.nav a.router-link-active {
  color: #2563eb;
  border-bottom: 2px solid #2563eb;
  padding-bottom: 4px;
}

.logout {
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
}

.logout-btn {
  width: 20px;
}

@media (max-width: 720px) {
  .header {
    padding: 0 16px;
  }

  .nav {
    gap: 20px;
  }
}

@media (max-width: 520px) {
  .logo {
    display: none;
  }

  .nav {
    flex: 1;
    justify-content: space-around;
    gap: 8px;
    font-size: 14px;
  }

  .logout {
    margin-left: 8px;
  }
}
</style>
