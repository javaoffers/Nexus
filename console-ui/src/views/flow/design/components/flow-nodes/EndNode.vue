<script>
import { Handle, Position } from '@vue-flow/core';

export default {
  components: { Handle },
  props: {
    data: { type: Object, required: true },
  },
  setup() {
    return { Position };
  },
};
</script>

<template>
  <div class="end-node">
    <Handle type="target" :position="Position.Top" />
    <div class="end-circle">
      <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
        <rect x="4" y="4" width="12" height="12" rx="2" fill="#fff" />
      </svg>
    </div>
  </div>
</template>

<style scoped>
.end-node {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}
.end-circle {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #86909c, #a0a5ab);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(134, 144, 156, 0.3);
  transition: box-shadow 0.2s;
}
.end-node:hover .end-circle {
  box-shadow: 0 6px 20px rgba(134, 144, 156, 0.45);
}
</style>
