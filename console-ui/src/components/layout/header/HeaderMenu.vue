<template>
  <div class="header-menu"></div>
</template>
<style lang="less" scoped>
.header-menu {
  float: left;
  line-height: var(--nexus-header-height);
  li {
    display: inline-block;
    margin-right: 40px;
    a {
      text-decoration: none;
      cursor: pointer;
      font-size: 14px;
      color: var(--nexus-text-secondary);
      transition: color 0.2s;
      &:hover {
        color: var(--nexus-primary);
      }
    }
  }
}
</style>
