<template>
  <div class="background">
    <AppNav />
    <div class="contents">
      <div class="titleGroup">
        <p class="maintitle">Welcome, <span>Family</span></p>
        <p class="subtitle">
          아이와 함께 가꾸는 우리 가족의 경제지도,<br />
          작은 저축이 큰 꿈이 되는 과정을 한 곳에서 경험하세요.
        </p>
      </div>
      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
      <div class="userGroup">
        <UserList
          :parent="session.parent"
          :children="children"
          @deleted="loadChildren"
        />
      </div>
      <div class="msg">
        <div class="msgBox">
          <img src="@/assets/images/notofication.png" alt="alert" />
          <span>10대들의 평균 용돈은 <b>100,000원</b>입니다!</span>
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import AppNav from '@/components/common/AppNav.vue';
import Footer from '@/components/common/Footer.vue';
import UserList from '@/components/user/UserList.vue';
import { fetchChildren } from '@/api/children';
import { useSessionStore } from '@/stores/session';

const session = useSessionStore();
const children = ref([]);
const errorMessage = ref('');

const loadChildren = async () => {
  try {
    children.value = await fetchChildren(session.parent.parentId);
  } catch (error) {
    errorMessage.value = error.message;
  }
};

onMounted(loadChildren);
</script>

<style scoped>
.background {
  background-color: #f7f9fb;
}
.contents {
  width: 100%;
  max-width: 1260px;
  margin: 0 auto;
  padding: 100px clamp(16px, 5vw, 100px);
}
.footer {
  height: 80px;
  padding: 32px;
}

/* css */
.titleGroup {
  margin-bottom: 64px;
  width: 100%;
  padding-top: 90px;
}
.maintitle {
  font-weight: 900;
  font-size: clamp(40px, 9vw, 72px);
}
.maintitle > span {
  color: #2456cc;
}
.subtitle {
  margin-top: 10px;
}

.userGroup {
  width: 100%;
  margin-bottom: 50px;
  overflow-x: auto;
  white-space: nowrap;
}

.msg {
  width: 100%;
  display: flex;
  justify-content: center;
}
.msgBox {
  width: min(70vw, 100%);
  min-height: 80px;
  padding: 12px 20px;
  text-align: center;
  background-color: rgba(185, 185, 185, 0.2);
  border-radius: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.msgBox > img {
  width: 20px;
  margin-right: 30px;
}

.error {
  color: #a83836;
  margin-bottom: 20px;
}
</style>
