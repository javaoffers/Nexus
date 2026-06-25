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
  methods: {
    onAdd() {
      if (this.data.onAdd) this.data.onAdd(this.data.dataNode);
    },
  },
};
</script>

<template>
  <div class="node-wrapper">
    <div class="start-node">
      <div class="start-circle">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
          <path d="M8 5v14l11-7L8 5z" fill="#fff" />
        </svg>
      </div>
      <Handle type="source" :position="Position.Right" />
    </div>
    <div class="add-btn-wrap">
      <div class="add-btn" @click="onAdd">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
          <path d="M7 1v12M1 7h12" stroke="#fff" stroke-width="2" stroke-linecap="round" />
        </svg>
      </div>
    </div>
  </div>
</template>

<style scoped>
.node-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}
.start-node {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}
.start-circle {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #00b42a, #23c343);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 180, 42, 0.3);
  transition: box-shadow 0.2s, transform 0.2s;
}
.start-node:hover .start-circle {
  box-shadow: 0 6px 20px rgba(0, 180, 42, 0.45);
  transform: scale(1.05);
}
.add-btn-wrap {
  display: flex;
  justify-content: center;
}
.add-btn {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #5b5fe6;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 8px rgba(91, 95, 230, 0.3);
}
.add-btn:hover {
  transform: scale(1.15);
  box-shadow: 0 4px 12px rgba(91, 95, 230, 0.5);
}
</style>