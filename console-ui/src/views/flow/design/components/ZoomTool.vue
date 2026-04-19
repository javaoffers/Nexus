<script>
import { Plus, Minus } from '@element-plus/icons-vue';

export default {
  components: {
    Plus,
    Minus,
  },
  props: {
    scale: {
      type: Number,
      default: 1,
    },
  },
  emits: ['change'],
  data() {
    return {
      step: 0.1,
    };
  },
  computed: {
    percent() {
      return Math.round(this.scale * 100) + '%';
    },
  },
  methods: {
    plus() {
      this.$emit('change', this.scale + this.step);
    },
    minus() {
      this.$emit('change', this.scale - this.step);
    },
  },
};
</script>

<template>
  <div class="zoom-tool">
    <el-icon @click="minus"><Minus /></el-icon>
    <span>{{ percent }}</span>
    <el-icon @click="plus"><Plus /></el-icon>
  </div>
</template>

<style scoped>
.zoom-tool {
  width: 112px;
  height: 28px;
  padding: 0 6px;
  background-color: var(--nexus-bg-card);
  box-shadow: var(--nexus-shadow-base);
  border-radius: var(--nexus-border-radius-base);
  display: flex;
  align-items: center;
  justify-content: space-around;
  font-size: 14px;
  user-select: none;
  position: absolute;
  right: 16px;
  bottom: 16px;
}
.zoom-tool .el-icon {
  cursor: pointer;
}
</style>
