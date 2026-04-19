<template>
  <div class="header-menu"></div>
</template>
<style scoped>
.header-menu {
  float: left;
  line-height: var(--nexus-header-height);
}
.header-menu li {
  display: inline-block;
  margin-right: 40px;
}
.header-menu li a {
  text-decoration: none;
  cursor: pointer;
  font-size: 14px;
  color: var(--nexus-text-secondary);
  transition: color 0.2s;
}
.header-menu li a:hover {
  color: var(--nexus-primary);
}
</style>
