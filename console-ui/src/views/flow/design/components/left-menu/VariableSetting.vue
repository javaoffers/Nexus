<script>
import { Edit, Delete, Search } from '@element-plus/icons-vue';
import ParamSettingModal from './ParamSettingModal.vue';
import { cloneDeep } from 'lodash-es';

export default {
  components: {
    Edit,
    Delete,
    Search,
    ParamSettingModal,
  },
  data() {
    return {
      searchValue: '',
    };
  },
  computed: {
    treeData() {
      var flowVariables = this.$store.state.flow.flowVariables.map(function (v) {
        return Object.assign({}, v, { label: v.variableName });
      });
      return [
        {
          label: this.$t('design.inputVars'),
          variableKey: 'in',
          children: flowVariables.filter(function (v) { return v.variableType === 1; }),
        },
        {
          label: this.$t('design.outputVars'),
          variableKey: 'out',
          children: flowVariables.filter(function (v) { return v.variableType === 2; }),
        },
        {
          label: this.$t('design.tempVars'),
          variableKey: 'temp',
          children: flowVariables.filter(function (v) { return v.variableType === 3; }),
        },
      ];
    },
    maxId() {
      var ids = this.$store.state.flow.flowVariables.map(function (v) { return v.id; });
      return Math.max.apply(null, ids.concat([0]));
    },
  },
  methods: {
    onAddOpen() {
      this.$refs.paramSettingModalRef.add(this.maxId + 1);
    },
    onEditOpen(data) {
      this.$refs.paramSettingModalRef.edit(data);
    },
    onAdd(data) {
      var cloned = cloneDeep(data);
      this.$store.commit('flow/UPDATE_FLOW_CONTENT', function (state) {
        var index = state.flowVariables.findIndex(function (item) { return item.id === cloned.id; });
        if (index === -1) {
          state.flowVariables.push(cloned);
        }
      });
    },
    onEdit(data) {
      var cloned = cloneDeep(data);
      this.$store.commit('flow/UPDATE_FLOW_CONTENT', function (state) {
        var index = state.flowVariables.findIndex(function (item) { return item.id === cloned.id; });
        if (index > -1) {
          state.flowVariables.splice(index, 1, cloned);
        }
      });
    },
    onDelete(data) {
      this.$store.commit('flow/UPDATE_FLOW_CONTENT', function (state) {
        var index = state.flowVariables.findIndex(function (item) { return item.variableKey === data.variableKey; });
        if (index > -1) {
          state.flowVariables.splice(index, 1);
        }
      });
    },
    searchVariable(value) {
      this.$refs.treeRef.filter(value);
    },
    filterNode(value, data) {
      if (!value) return true;
      return data.label.includes(value) || data.variableKey.includes(value);
    },
  },
};
</script>

<template>
  <div class="flow-variable-setting">
    <div class="variable-head">
      <el-button type="primary" size="small" class="add-temp-variable" @click="onAddOpen">{{ $t('design.addTempVar') }}</el-button>
      <el-input
        v-model="searchValue"
        :prefix-icon="Search"
        :placeholder="$t('design.searchVar')"
        class="variable-search-input"
        size="small"
        @input="searchVariable"
      />
    </div>
    <div class="variable-body">
      <el-tree ref="treeRef" :data="treeData" node-key="variableKey" default-expand-all :filter-node-method="filterNode">
        <template #default="{ node, data }">
          <span class="custom-tree-node">
            <span v-if="data.children" class="custom-tree-node-label">{{ node.label }}</span>
            <template v-else>
              <span class="custom-tree-node-label" :title="node.label">
                <span>{{ data.variableKey }}</span>
                <span class="custom-tree-node-name">{{ node.label }}</span>
              </span>
              <span class="tree-node-action" v-if="data.variableType === 3">
                <el-icon @click="onEditOpen(data)"><Edit /></el-icon>
                <el-icon @click="onDelete(data)" style="margin-left: 8px"><Delete /></el-icon>
              </span>
            </template>
          </span>
        </template>
      </el-tree>
    </div>
    <ParamSettingModal ref="paramSettingModalRef" @edit="onEdit" @add="onAdd" />
  </div>
</template>

<style scoped>
.flow-variable-setting {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.flow-variable-setting .variable-head {
  display: flex;
  flex-direction: column;
  padding: 16px 12px 8px;
}
.flow-variable-setting .variable-body {
  flex: 1;
  overflow: auto;
  min-height: 0;
}
.flow-variable-setting .add-temp-variable {
  margin-bottom: 12px;
}
.flow-variable-setting .custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  padding-right: 8px;
  min-width: 0;
}
.flow-variable-setting .custom-tree-node-label {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.flow-variable-setting .custom-tree-node-name {
  font-size: 12px;
  color: #999;
  margin-left: 6px;
}
.flow-variable-setting .tree-node-action {
  display: none;
}
</style>
<style>
.flow-variable-setting .el-tree-node__content:hover .tree-node-action {
  display: flex;
}
</style>
