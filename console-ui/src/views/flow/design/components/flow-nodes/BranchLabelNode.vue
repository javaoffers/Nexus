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
  computed: {
    branchName() {
      const dataNode = this.data.dataNode;
      if (!dataNode) return '';
      const parent = dataNode.getParent();
      if (!parent || !parent.raw || !parent.raw.conditions) return '';
      const condition = parent.raw.conditions[dataNode.branchIndex];
      return condition?.conditionName || '';
    },
    isDefault() {
      const dataNode = this.data.dataNode;
      if (!dataNode) return false;
      const parent = dataNode.getParent();
      if (!parent || !parent.raw || !parent.raw.conditions) return false;
      const condition = parent.raw.conditions[dataNode.branchIndex];
      return condition?.conditionType === 'DEFAULT';
    },
  },
  methods: {
    onEdit() {
      if (this.data.onEditBranch) this.data.onEditBranch(this.data.dataNode);
    },
  },
};
</script>

<template>
  <div class="node-wrapper">
    <div class="branch-label-node">
      <Handle type="target" :position="Position.Left" />
      <div class="branch-pill" :class="{ 'is-default': isDefault }" @click.stop="onEdit">
        <span class="branch-name">{{ branchName }}</span>
        <svg class="edit-icon" width="12" height="12" viewBox="0 0 14 14" fill="none">
          <path d="M10.5 1.5l2 2L4.5 11.5H2.5v-2l8-8z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <Handle type="source" :position="Position.Right" />
    </div>
    <div class="add-btn-wrap">
      <div class="add-btn" @click="data.onAdd && data.onAdd(data.dataNode)">
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
.branch-label-node {
  position: relative;
}
.branch-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 16px;
  border-radius: 20px;
  background: #f2f3f5;
  border: 1px solid #e5e6e8;
  font-size: 13px;
  color: #4e5969;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
  white-space: nowrap;
}
.branch-pill:hover {
  background: #e8e9eb;
  border-color: #c9cdd4;
}
.branch-pill.is-default {
  background: #fff7e6;
  border-color: #ffe4a3;
  color: #d46b08;
}
.branch-pill.is-default:hover {
  background: #fff1cc;
}
.edit-icon {
  opacity: 0;
  color: #86909c;
  transition: opacity 0.15s;
}
.branch-pill:hover .edit-icon {
  opacity: 1;
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