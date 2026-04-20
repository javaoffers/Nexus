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
    nodeConfig() {
      return this.data.nodeConfig || {};
    },
    headerColor() {
      return this.nodeConfig.nodeHeaderColor || '#5b5fe6';
    },
    typeName() {
      return this.nodeConfig.nodeName || '';
    },
    nodeName() {
      return this.data.dataNode?.raw?.name || '';
    },
  },
  methods: {
    onEdit() {
      if (this.data.onEdit) this.data.onEdit(this.data.dataNode);
    },
    onDelete() {
      if (this.data.onDelete) this.data.onDelete(this.data.dataNode);
    },
    onAdd() {
      if (this.data.onAdd) this.data.onAdd(this.data.dataNode);
    },
  },
};
</script>

<template>
  <div class="normal-node" :style="{ '--header-color': headerColor }">
    <Handle type="target" :position="Position.Top" />
    <div class="node-card">
      <div class="node-header">
        <span class="type-dot" :style="{ background: headerColor }"></span>
        <span class="type-name">{{ typeName }}</span>
      </div>
      <div class="node-body">
        <span class="node-name">{{ nodeName }}</span>
      </div>
      <div class="hover-actions">
        <div class="action-btn edit-btn" @click.stop="onEdit" title="Edit">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
            <path d="M10.5 1.5l2 2L4.5 11.5H2.5v-2l8-8z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="action-btn delete-btn" @click.stop="onDelete" title="Delete">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
            <path d="M3 3.5l1 8.5h6l1-8.5M2 3.5h10M5.5 1.5h3" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
      </div>
    </div>
    <Handle type="source" :position="Position.Bottom" />
  </div>
  <div class="add-btn-wrap">
    <div class="add-btn" @click="onAdd">
      <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
        <path d="M7 1v12M1 7h12" stroke="#fff" stroke-width="2" stroke-linecap="round" />
      </svg>
    </div>
  </div>
</template>

<style scoped>
.normal-node {
  position: relative;
}
.node-card {
  width: 220px;
  background: #fff;
  border-radius: 10px;
  border: 1px solid #e8e8e8;
  border-top: 4px solid var(--header-color);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.2s, border-color 0.2s;
  overflow: hidden;
  position: relative;
}
.normal-node:hover .node-card {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  border-color: var(--header-color);
}
.node-header {
  padding: 8px 12px 4px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.type-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}
.type-name {
  font-size: 11px;
  color: #86909c;
  font-weight: 500;
  letter-spacing: 0.3px;
}
.node-body {
  padding: 4px 12px 10px;
}
.node-name {
  font-size: 14px;
  font-weight: 500;
  color: #1d2129;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.hover-actions {
  position: absolute;
  top: 6px;
  right: 6px;
  display: none;
  gap: 4px;
}
.normal-node:hover .hover-actions {
  display: flex;
}
.action-btn {
  width: 26px;
  height: 26px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.15s;
}
.edit-btn {
  color: #5b5fe6;
  background: rgba(91, 95, 230, 0.08);
}
.edit-btn:hover {
  background: rgba(91, 95, 230, 0.18);
}
.delete-btn {
  color: #f53f3f;
  background: rgba(245, 63, 63, 0.08);
}
.delete-btn:hover {
  background: rgba(245, 63, 63, 0.18);
}
.add-btn-wrap {
  display: flex;
  justify-content: center;
  margin-top: 12px;
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
