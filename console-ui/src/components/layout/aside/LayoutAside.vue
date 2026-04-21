<script>
import { Coin } from '@element-plus/icons-vue';
import IconFlow from '@/components/icons/IconFlow.vue';
import IconInterface from '@/components/icons/IconInterface.vue';
import IconSetting from '@/components/icons/IconSetting.vue';

export default {
  components: {
    Coin,
    IconFlow,
    IconInterface,
    IconSetting,
  },
};
</script>

<template>
  <div class="layout-aside">
    <el-menu :default-active="$route.fullPath" :unique-opened="true" class="el-menu-vertical-demo" style="height: 100%" router>
      <el-sub-menu index="2">
        <template #title>
          <el-icon><IconFlow /></el-icon>
          <span>{{ $t('menu.flow') }}</span>
        </template>
        <el-menu-item index="/flow/define">{{ $t('menu.flowDefine') }}</el-menu-item>
        <el-menu-item index="/flow/list">{{ $t('menu.flowList') }}</el-menu-item>
      </el-sub-menu>
      <el-menu-item index="/suite/list">
        <el-icon><IconInterface /></el-icon>
        <span>{{ $t('menu.suite') }}</span>
      </el-menu-item>
      <el-menu-item index="/object/list">
        <el-icon><Coin /></el-icon>
        <span>{{ $t('menu.object') }}</span>
      </el-menu-item>
      <el-sub-menu index="5">
        <template #title>
          <el-icon><IconSetting/></el-icon>
          <span>{{ $t('menu.settings') }}</span>
        </template>
        <el-menu-item index="/system/token">{{ $t('menu.token') }}</el-menu-item>
        <el-menu-item index="/system/datasource">{{ $t('menu.dataSource') }}</el-menu-item>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<style scoped>
.layout-aside {
  height: 100%;
  border-right: 1px solid var(--nexus-border-light);
  background-color: var(--nexus-bg-sidebar);
}

:deep(.el-menu) {
  border-right: none;
  padding: 8px;
}

:deep(.el-menu-item) {
  margin-bottom: 2px;
  border-radius: var(--nexus-border-radius-sm);
  height: 40px;
  line-height: 40px;
  color: var(--nexus-text-secondary);
  transition: all 0.2s;
}

:deep(.el-menu-item):hover {
  background-color: var(--nexus-primary-light-9) !important;
  color: var(--nexus-primary);
}

:deep(.el-sub-menu .el-sub-menu__title) {
  border-radius: var(--nexus-border-radius-sm);
  height: 40px;
  line-height: 40px;
  color: var(--nexus-text-secondary);
}

:deep(.el-sub-menu .el-sub-menu__title):hover {
  background-color: var(--nexus-primary-light-9) !important;
  color: var(--nexus-primary);
}

:deep(.el-menu-item.is-active) {
  background: var(--nexus-primary-light-9) !important;
  color: var(--nexus-primary) !important;
  font-weight: 500;
  border-right: none;
  position: relative;
}

:deep(.el-menu-item.is-active)::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: var(--nexus-primary);
  border-radius: 0 2px 2px 0;
}
</style>
