<script>
import { RouterView } from 'vue-router';
import LayoutHeader from '../components/layout/header/LayoutHeader.vue';
import LayoutAside from '../components/layout/aside/LayoutAside.vue';
import MainBreadcrumb from '../components/layout/main/MainBreadcrumb.vue';

export default {
  components: {
    RouterView,
    LayoutHeader,
    LayoutAside,
    MainBreadcrumb,
  },
};
</script>

<template>
  <div class="layout-view">
    <el-container class="layout-container">
      <el-header class="layout-header"><LayoutHeader /></el-header>
      <el-container class="layout-body">
        <el-aside class="layout-aside"><LayoutAside /></el-aside>
        <el-main class="layout-main">
          <el-container class="layout-main-container">
            <el-header class="layout-main-header">
              <MainBreadcrumb />
            </el-header>
            <el-main class="layout-main-main">
              <RouterView class="layout-router-view" />
            </el-main>
          </el-container>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style>
.layout-view {
  height: 100%;
  background: var(--nexus-bg-page);
}

.layout-view .layout-container {
  height: 100%;
}

.layout-view .layout-header {
  padding: 0;
  height: var(--nexus-header-height);
}

.layout-view .layout-body {
  min-height: 0;
}

.layout-view .layout-aside {
  width: var(--nexus-sidebar-width);
}

.layout-view .layout-main {
  padding: 16px 16px 8px;
}

.layout-view .layout-main-container {
  height: 100%;
}

.layout-view .layout-main-header {
  padding: 0;
  height: 36px;
}

.layout-view .layout-main-main {
  padding: 0;
  overflow: auto;
}

.layout-view .layout-router-view {
  height: 100%;
  border-radius: var(--nexus-border-radius-base);
  overflow: auto;
}
</style>
