<script>
import { CaretLeft } from '@element-plus/icons-vue';
import VariableSetting from './VariableSetting.vue';

export default {
  components: {
    CaretLeft,
    VariableSetting,
  },
  data() {
    return {
      activeItem: '',
    };
  },
  computed: {
    menuItems() {
      return [
        {
          key: 'variable',
          icon: '[x]',
          name: this.$t('design.variables'),
        },
      ];
    },
  },
  methods: {
    switchItem(key) {
      if (this.activeItem === key) {
        this.activeItem = '';
        return;
      }
      this.activeItem = key;
    },
  },
};
</script>

<template>
  <div class="flow-design-left-menu">
    <div class="left-menu-panel" :class="{ active: activeItem }">
      <VariableSetting v-if="activeItem === 'variable'" />
      <div class="left-menu-close" @click="switchItem('')" v-if="activeItem">
        <el-icon><CaretLeft /></el-icon>
      </div>
    </div>
    <div class="left-menu">
      <div class="left-menu-list">
        <div
          class="left-menu-item"
          v-for="item in menuItems"
          :key="item.key"
          :class="{ active: activeItem === item.key }"
          @click="switchItem(item.key)"
        >
          <div class="left-menu-icon" v-if="typeof item.icon === 'string'">{{ item.icon }}</div>
          <component v-else :is="item.icon"></component>
          <div class="left-menu-name">{{ item.name }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.flow-design-left-menu {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
}
.flow-design-left-menu .left-menu {
  position: absolute;
  top: 50px;
  left: 0;
  height: 100%;
  width: 60px;
  border-right: solid 1px var(--el-menu-border-color);
  border-bottom: solid 1px var(--el-menu-border-color);
  border-top-left-radius: 4px;
  border-bottom-left-radius: 4px;
}
.flow-design-left-menu .left-menu .left-menu-list {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: center;
  justify-content: center;
  background-color: var(--nexus-bg-card);
}
.flow-design-left-menu .left-menu .left-menu-item {
  position: relative;
  width: 48px;
  height: 48px;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
  user-select: none;
}
.flow-design-left-menu .left-menu .left-menu-item::after {
  content: '';
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  left: 0;
  width: 2px;
  height: 24px;
  background-color: var(--nexus-primary);
  opacity: 0;
  transition: opacity 0.3s;
}
.flow-design-left-menu .left-menu .left-menu-item:hover,
.flow-design-left-menu .left-menu .left-menu-item.active {
  background-color: var(--nexus-primary-light-9);
}
.flow-design-left-menu .left-menu .left-menu-item.active::after {
  opacity: 1;
}
.flow-design-left-menu .left-menu .left-menu-icon {
  font-size: 16px;
  line-height: 14px;
  margin-bottom: 4px;
}
.flow-design-left-menu .left-menu .left-menu-name {
  font-size: 12px;
}
.flow-design-left-menu .left-menu-panel {
  position: absolute;
  top: 50px;
  left: 0;
  transform: translateX(-180px);
  width: 240px;
  height: 100%;
  border-right: 1px solid var(--el-menu-border-color);
  border-left: none;
  background-color: var(--nexus-bg-card);
  transition: transform ease 0.2s;
}
.flow-design-left-menu .left-menu-panel.active {
  transform: translateX(60px);
}
.flow-design-left-menu .left-menu-panel .left-menu-close {
  position: absolute;
  top: 50%;
  right: -20px;
  background-color: var(--nexus-bg-card);
  border: 1px solid var(--el-menu-border-color);
  border-top-right-radius: 18px;
  border-bottom-right-radius: 18px;
  border-left: none;
  transform: translateY(-50%);
  width: 20px;
  height: 60px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 16px;
  color: var(--nexus-text-tertiary);
  cursor: pointer;
}
</style>
