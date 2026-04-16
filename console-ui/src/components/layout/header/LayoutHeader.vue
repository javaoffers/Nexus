<script setup lang="ts">
import AppLogo from './AppLogo.vue';
import CurrentUser from './CurrentUser.vue';
import HeaderMenu from './HeaderMenu.vue';
</script>
<template>
  <div class="layout-header">
    <div class="header-left">
      <AppLogo />
    </div>
    <div class="header-right">
      <HeaderMenu />
      <CurrentUser />
    </div>
  </div>
</template>
<style lang="less" scoped>
.layout-header {
  background-color: var(--nexus-bg-header);
  border-bottom: 1px solid var(--nexus-border-light);
  box-shadow: var(--nexus-shadow-sm);
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: stretch;
  color: var(--nexus-text-primary);
}
</style>
