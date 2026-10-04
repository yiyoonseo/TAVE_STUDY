<template>
  <div class="container">
    <AppNav />
    <div id="dashboard">
      <div>
        <h1>
          안녕하세요,
          <span class="blue">{{ session.parent.nickname }} 님</span>
        </h1>
        <div class="gray">티끌저금통과 함께 자녀 용돈을 손쉽게 관리하세요.</div>
      </div>
      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
      <ChildList class="child-list" :children="children" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import AppNav from '@/components/common/AppNav.vue';
import ChildList from '@/components/user/ChildList.vue';
import { fetchChildren } from '@/api/children';
import { useSessionStore } from '@/stores/session';

const session = useSessionStore();
const children = ref([]);
const errorMessage = ref('');

onMounted(async () => {
  try {
    children.value = await fetchChildren(session.parent.parentId);
  } catch (error) {
    errorMessage.value = error.message;
  }
});
</script>

<style scoped>
.container {
  background-color: #f7f9fb;
}
#dashboard {
  background-color: #f7f9fb;
  padding: 4em clamp(16px, 8vw, 8em);
  display: flex;
  flex-direction: column;
  gap: 50px;
}
.blue {
  color: #2456cc;
}
.gray {
  color: gray;
}
h1 {
  margin-top: 68px;
  font-weight: 600;
}

.error {
  color: #a83836;
}
</style>
