import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { createParent, fetchParent } from '@/api/parents';

const STORAGE_KEY = 'dustbank.parent';

const load = () => {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY));
  } catch {
    return null;
  }
};

// 로그인한 부모 정보. localStorage에 저장해 새로고침해도 유지한다.
export const useSessionStore = defineStore('session', () => {
  const parent = ref(load());
  const isLoggedIn = computed(() => parent.value !== null);

  const save = (value) => {
    parent.value = value;
    localStorage.setItem(STORAGE_KEY, JSON.stringify(value));
  };

  const login = async (parentId) => save(await fetchParent(parentId));

  const register = async (body) => save(await createParent(body));

  const logout = () => {
    parent.value = null;
    localStorage.removeItem(STORAGE_KEY);
  };

  return { parent, isLoggedIn, login, register, logout };
});
