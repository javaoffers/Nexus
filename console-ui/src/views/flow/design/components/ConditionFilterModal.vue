<script>
import FilterGroup from '@/components/filter/FilterGroup.vue';
import { cloneDeep } from 'lodash-es';

export default {
  components: {
    FilterGroup,
  },
  data() {
    return {
      visible: false,
      condition: this.getDefault(),
      openParams: null,
    };
  },
  computed: {
    sourceList() {
      return this.$store.state.flow.flowVariables.filter(function (item) {
        return item.variableType === 1 || item.variableType === 3;
      });
    },
  },
  methods: {
    getDefault() {
      return {
        conditionName: '',
        conditionType: 'CUSTOM',
        expression: '',
        outgoing: '',
        conditionExpressions: [],
      };
    },
    open(params) {
      this.openParams = params;
      if (typeof params.index === 'number') {
        this.condition = cloneDeep(params.data.conditions[params.index]);
        if (!Array.isArray(this.condition.conditionExpressions)) {
          this.condition.conditionExpressions = [];
        }
      } else {
        this.condition = this.getDefault();
      }
      this.visible = true;
    },
    onChange(value) {
      this.condition.conditionExpressions = value;
    },
    onCancel() {
      this.visible = false;
    },
    onSubmit() {
      this.visible = false;
      if (typeof this.openParams.afterEdit === 'function') {
        this.openParams.afterEdit(cloneDeep(this.condition));
      }
    },
  },
};
</script>

<template>
  <el-dialog title="设置分支条件" :width="640" v-model="visible" class="condition-filter-modal">
    <div class="condition-name-label">分支名称</div>
    <el-input v-model="condition.conditionName" class="condition-name-input" placeholder="请输入" />
    <FilterGroup :value="condition.conditionExpressions" @change="onChange" :sourceList="sourceList" :targetList="sourceList" />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="onCancel">取消</el-button>
        <el-button type="primary" @click="onSubmit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style>
.condition-filter-modal .el-dialog__body {
  padding-top: 10px;
}
.condition-filter-modal .condition-name-label {
  margin-bottom: 10px;
}
.condition-filter-modal .condition-name-input {
  margin-bottom: 10px;
}
</style>
