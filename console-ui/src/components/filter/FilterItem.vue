<script>
import { getDataTypeObject, getVariableDataType, isDataTypeEqual } from '@/utils/dataType';
import { Delete } from '@element-plus/icons-vue';
import { DataTypeOperatorMap, BaseDataType, OperatorNameMap, Operator } from './config';
import FilterValue from './FilterValue.vue';
import VariableSelect from '@/components/common/VariableSelect.vue';

const FilterAssignType = {
  Constant: 'CONSTANT',
  Variable: 'VARIABLE',
};

export default {
  components: {
    FilterValue,
    VariableSelect,
    Delete,
  },
  props: {
    item: {
      type: Object,
      required: true,
    },
    sourceList: {
      type: Array,
      default: () => [],
    },
    targetList: {
      type: Array,
      default: () => [],
    },
  },
  emits: ['change', 'delete'],
  data() {
    return {
      FilterAssignType,
    };
  },
  computed: {
    sourceModel: {
      get() {
        return this.item.variableKey;
      },
      set(value) {
        const current = getVariableDataType(value, this.sourceList);
        this.$emit('change', { variableKey: value, dataType: current.dataType });
      },
    },
    operatorModel: {
      get() {
        return this.item.operator;
      },
      set(value) {
        this.$emit('change', { ...this.item, operator: value, assignType: FilterAssignType.Constant, value: null });
      },
    },
    assignTypeModel: {
      get() {
        return this.item.assignType;
      },
      set(value) {
        this.$emit('change', { ...this.item, assignType: value, value: null });
      },
    },
    targetModel: {
      get() {
        return this.item.value;
      },
      set(value) {
        this.$emit('change', { ...this.item, value: value });
      },
    },
    isConstant() {
      return this.item.assignType === FilterAssignType.Constant;
    },
    isVariable() {
      return this.item.assignType === FilterAssignType.Variable;
    },
    operatorList() {
      const dataType = getDataTypeObject(this.item.dataType)?.type;
      const operators = DataTypeOperatorMap[dataType] || [];
      return operators.map(operator => {
        return {
          value: operator,
          label: OperatorNameMap[operator],
        };
      });
    },
    isNoValueOperator() {
      return !this.item.operator || [Operator.Empty, Operator.NotEmpty].includes(this.item.operator);
    },
    filteredTargetList() {
      return this.targetList.filter((item) => {
        if (item.variableKey === this.item.variableKey) {
          return false;
        }
        return isDataTypeEqual(item.dataType, this.item.dataType);
      });
    },
  },
};
</script>

<template>
  <div class="filter-item">
    <div class="filter-item-key">
      <VariableSelect
          v-model="sourceModel"
          :options="sourceList"
      />
    </div>
    <div class="filter-item-operator">
      <el-select placeholder="请选择" v-model="operatorModel">
        <el-option v-for="operator in operatorList" :key="operator.value" :value="operator.value" :label="operator.label" />
      </el-select>
    </div>
    <div class="filter-item-assign-type">
      <template v-if="!isNoValueOperator">
        <el-select placeholder="请选择" v-model="assignTypeModel">
          <el-option :value="FilterAssignType.Constant" label="常量" />
          <el-option :value="FilterAssignType.Variable" label="变量" />
        </el-select>
      </template>
    </div>
    <div class="filter-item-value">
      <template v-if="!isNoValueOperator">
        <FilterValue v-if="isConstant" v-model="targetModel" :dataType="item.dataType" />
        <el-select v-else-if="isVariable" placeholder="请选择" v-model="targetModel">
          <el-option v-for="source in filteredTargetList" :key="source.variableKey" :value="source.variableKey" :label="source.variableName" />
        </el-select>
      </template>
    </div>
    <div class="filter-item-action">
      <el-button :icon="Delete" link @click="$emit('delete')"></el-button>
    </div>
  </div>
</template>

<style scoped>
.filter-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.filter-item .filter-item-key {
  width: 120px;
  margin-right: 10px;
}
.filter-item .filter-item-operator {
  width: 120px;
  margin-right: 10px;
}
.filter-item .filter-item-assign-type {
  width: 90px;
  margin-right: 10px;
}
.filter-item .filter-item-value {
  flex: 1;
  margin-right: 10px;
}
.filter-item .filter-item-action {
  width: 24px;
}
</style>
