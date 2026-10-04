<template>
  <li>
    <router-link
      :to="{ name: 'ParentDashboard' }"
      style="text-decoration: none"
    >
      <div class="imgBox">
        <img :src="iconPath(parent.iconId)" />
      </div>
      <p class="nickname">{{ parent.nickname }}</p>
      <p class="role">Parent</p>
    </router-link>
  </li>
</template>

<script setup>
import { iconPath } from '@/utils/format';

defineProps({
  parent: { type: Object, required: true },
});
</script>

<style scoped>
li {
  list-style: none;
  margin: 0;
  width: 180px;
  height: 260px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 0;
  margin-right: 50px;
}
.imgBox {
  width: 168px;
  height: 168px;
  border-radius: 50%;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0;
}
.imgBox > img {
  width: 152px;
  height: 152px;
  background-color: rgb(203, 236, 255);
  border-radius: 50%;
  margin: 0;
}
.nickname {
  font-size: 20px;
  font-weight: 800;
  width: 100%;
  text-align: center;
  margin-top: 24px;
  margin-bottom: 5px;
  color: black;
}
.role {
  font-size: 12px;
  text-align: center;
  color: #596064;
}
</style>
