<template>
  <div class="child-item">
    <img :src="iconPath(child.iconId)" />
    <div class="name">{{ child.nickname }}</div>
    <div class="balance">{{ formatMoney(child.balance) }} 원</div>
    <div class="btn-group">
      <router-link
        :to="{ name: 'AddIncome', params: { id: child.childId } }"
        class="rlink"
        ><div class="btn1">용돈 주기</div></router-link
      >
      <div class="btn-line">
        <router-link
          :to="{ name: 'ParentChildStats', params: { id: child.childId } }"
          class="rlink"
          ><div class="btn2">통계 보기</div></router-link
        >
        <router-link
          :to="{ name: 'ParentChildTransactions', params: { id: child.childId } }"
          class="rlink"
          ><div class="btn2">거래 내역</div></router-link
        >
      </div>
    </div>
  </div>
</template>

<script setup>
import { formatMoney, iconPath } from '@/utils/format';

defineProps({
  child: { type: Object, required: true },
});
</script>

<style scoped>
.child-item {
  background-color: white;
  width: min(360px, 100%);
  min-height: 560px;
  box-sizing: border-box;
  border-radius: 48px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 16px;
  padding: 16px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
}
img {
  width: 150px;
  height: 150px;
  border: 6px double lightgray;
  border-radius: 50%;
}
.name {
  font-size: 2em;
  font-weight: 600;
}
.balance {
  font-size: 2.5em;
  font-weight: 800;
}
.btn-group {
  display: flex;
  flex-direction: column;
  gap: 1em;
  width: 100%;
}
.btn1 {
  width: 100%;
  height: 56px;
  background-color: #e3e9ed;
  border-radius: 30px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.btn2 {
  height: 54px;
  background-color: #f7f9fb;
  border-radius: 30px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.btn-line {
  display: flex;
  gap: 16px;
}

.btn-line .rlink {
  flex: 1;
}
.rlink {
  display: block;
  text-decoration: none;
  color: black;
}
</style>
