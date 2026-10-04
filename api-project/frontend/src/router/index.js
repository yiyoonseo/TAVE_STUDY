import { createRouter, createWebHistory } from 'vue-router';
import { useSessionStore } from '@/stores/session';
import Home from '@/pages/Home.vue';
import Register from '@/pages/Register.vue';
import Family from '@/pages/Family.vue';
import ParentDashboard from '@/pages/parent/ParentDashboard.vue';
import AddChild from '@/pages/parent/AddChild.vue';
import AddIncome from '@/pages/parent/AddIncome.vue';
import AddExpenditure from '@/pages/child/AddExpenditure.vue';
import StatsPage from '@/pages/StatsPage.vue';
import TransactionsPage from '@/pages/TransactionsPage.vue';
import EditTransaction from '@/pages/EditTransaction.vue';

// meta.role: 'parent' | 'child' → 상단 메뉴와 제목이 역할에 맞게 바뀐다
const auth = (role) => ({ requiresAuth: true, role });

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'Home', component: Home },
    { path: '/register', name: 'Register', component: Register },
    { path: '/family', name: 'Family', component: Family, meta: auth() },

    // 부모
    { path: '/parent', name: 'ParentDashboard', component: ParentDashboard, meta: auth('parent') },
    { path: '/parent/add-child', name: 'AddChild', component: AddChild, meta: auth('parent') },
    { path: '/parent/children/:id/income', name: 'AddIncome', component: AddIncome, meta: auth('parent') },
    { path: '/parent/children/:id/stats', name: 'ParentChildStats', component: StatsPage, meta: auth('parent') },
    {
      path: '/parent/children/:id/transactions',
      name: 'ParentChildTransactions',
      component: TransactionsPage,
      meta: auth('parent'),
      children: [
        { path: ':tid/edit', name: 'ParentEditTransaction', component: EditTransaction },
      ],
    },

    // 자녀
    { path: '/child/:id', name: 'ChildDashboard', component: StatsPage, meta: auth('child') },
    { path: '/child/:id/expenditure', name: 'AddExpenditure', component: AddExpenditure, meta: auth('child') },
    {
      path: '/child/:id/transactions',
      name: 'ChildTransactions',
      component: TransactionsPage,
      meta: auth('child'),
      children: [
        { path: ':tid/edit', name: 'ChildEditTransaction', component: EditTransaction },
      ],
    },

    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
});

router.beforeEach((to) => {
  const session = useSessionStore();
  if (to.meta.requiresAuth && !session.isLoggedIn) {
    return { name: 'Home' };
  }
  // 이미 로그인했다면 홈·회원가입 대신 가족 페이지로
  if ((to.name === 'Home' || to.name === 'Register') && session.isLoggedIn) {
    return { name: 'Family' };
  }
});

export default router;
