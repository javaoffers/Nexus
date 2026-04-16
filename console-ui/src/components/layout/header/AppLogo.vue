<template>
  <div class="app-logo"><img src="@/assets/logo.png" alt="nexus logo"/>NEXUS</div>
</template>
<style lang="less" scoped>
.app-logo {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 2px;
  color: var(--nexus-primary);
  padding: 0 24px;
  width: var(--nexus-sidebar-width);
  border-right: 1px solid var(--nexus-border-light);
  img {
    width: 36px;
    height: 36px;
    margin-right: 8px;
  }
}
</style>
