<template>
  <footer class="footer">
    <div class="footer-bottom">
      {{ text }}
    </div>
  </footer>
</template>

<script setup>
defineProps({
  text: {
    type: String,
    default: "© 2026 KB IT's Your Life 24회차 싹쓰리. All rights reserved.",
  },
});
</script>

<style scoped>
.footer {
  bottom: 0px;
  width: 100%;
  height: 80px;
  background: #f0f4f7;
  padding: 32px 0;
}

.footer-bottom {
  max-width: 1450px;
  margin: 0 auto;
  font-size: 12px;
  color: #9ca3af;
  text-align: right;
}
</style>
