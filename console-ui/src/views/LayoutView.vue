<script setup lang="ts">
import { RouterView } from 'vue-router';
import LayoutHeader from '../components/layout/header/LayoutHeader.vue';
import LayoutAside from '../components/layout/aside/LayoutAside.vue';
import MainBreadcrumb from '../components/layout/main/MainBreadcrumb.vue';
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

<style lang="less">
.layout-view {
  height: 100%;
  background: var(--nexus-bg-page);

  .layout-container {
    height: 100%;
  }

  .layout-header {
    padding: 0;
    height: var(--nexus-header-height);
  }

  .layout-body {
    min-height: 0;
  }

  .layout-aside {
    width: var(--nexus-sidebar-width);
  }

  .layout-main {
    padding: 16px 16px 8px;
  }

  .layout-main-container {
    height: 100%;
  }

  .layout-main-header {
    padding: 0;
    height: 36px;
  }

  .layout-main-main {
    padding: 0;
    overflow: auto;
  }

  .layout-router-view {
    height: 100%;
    border-radius: var(--nexus-border-radius-base);
    overflow: auto;
  }
}
</style>
