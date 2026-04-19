<script>
import { resizableDrawerMixin, getDrawerSize, setDrawerSize } from '@/mixins/resizableDrawer';

export default {
  mixins: [resizableDrawerMixin],
  props: {
    drawerKey: String,
    size: {
      type: Number,
      default: 560,
    },
    title: {
      type: String,
      default: '',
    },
    modelValue: {
      type: Boolean,
      default: false,
    },
    direction: {
      type: String,
      default: 'rtl',
    },
  },
  emits: ['update:modelValue', 'closed'],
  data() {
    return {
      drawerSize: getDrawerSize(this.drawerKey, this.size),
      visible: this.modelValue,
    };
  },
  watch: {
    modelValue(newVal) {
      this.visible = newVal;
    },
    drawerSize(newSize) {
      if (this.drawerKey) {
        setDrawerSize(this.drawerKey, newSize);
      }
    },
  },
};
</script>

<template>
  <el-drawer
      v-model="visible"
      :class="drawerClass"
      class="resizable-drawer"
      :size="drawerSize"
      :title="title"
      :direction="direction"
      @closed="onClosed"
  >
    <div :class="resizeBarClass" ref="resizeBar" @mousedown="onMouseDown"></div>
    <slot></slot>
  </el-drawer>
</template>

<style>
.el-drawer.resizable-drawer {
  transition: width, height 0s ease;
}

.resize-bar {
  position: absolute;
  background-color: rgba(0, 0, 0, 0.1);
  z-index: 1;
}

.resize-bar.resize-bar-ltr,
.resize-bar.resize-bar-rtl {
  cursor: col-resize;
  width: 4px;
  height: 100%;
  top: 0;
}

.resize-bar.resize-bar-ltr {
  right: 0;
}

.resize-bar.resize-bar-rtl {
  left: 0;
}

.resize-bar.resize-bar-ttb,
.resize-bar.resize-bar-btt {
  cursor: row-resize;
  height: 4px;
  width: 100%;
  left: 0;
}

.resize-bar.resize-bar-ttb {
  bottom: 0;
}

.resize-bar.resize-bar-btt {
  top: 0;
}
</style>
