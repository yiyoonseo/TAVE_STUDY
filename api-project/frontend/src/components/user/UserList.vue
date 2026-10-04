<template>
  <ul class="userList">
    <UserParentItem :parent="parent" />
    <UserChildItem
      v-for="child in children"
      :key="child.childId"
      :child="child"
      @deleted="$emit('deleted')"
    />
    <button class="addChild" @click="addChild">
      <img src="@/assets/images/plus.png" alt="자녀 추가" />
    </button>
  </ul>
</template>

<script setup>
import { useRouter } from 'vue-router';
import UserChildItem from './UserChildItem.vue';
import UserParentItem from './UserParentItem.vue';

defineProps({
  parent: { type: Object, required: true },
  children: { type: Array, required: true },
});
defineEmits(['deleted']);

const router = useRouter();
const addChild = () => router.push({ name: 'AddChild' });
</script>

<style scoped>
.userList {
  width: 100%;
  display: flex;
  padding: 0;
}
.addChild {
  background-color: white;
  width: 168px;
  height: 168px;
  min-width: 168px;
  border-radius: 50%;
  border-style: dashed;
  border-color: rgb(201, 201, 201);
  cursor: pointer;
}
.addChild > img {
  width: 30px;
  height: 30px;
}
</style>
