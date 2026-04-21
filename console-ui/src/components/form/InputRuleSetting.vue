<script>
import { valueType } from '@/typings';
import { Delete } from '@element-plus/icons-vue';
import { getVariableDataType, isDataTypeEqual, isDataTypeMatch } from '@/utils/dataType';
import FilterValue from '../filter/FilterValue.vue';
import DataTypeDisplay from '@/components/common/DataTypeDisplay.vue';
import VariableSelect from '@/components/common/VariableSelect.vue';
import { ElMessage } from 'element-plus';

import i18n from '@/i18n';

const assignTypeList = [
  { value: valueType.CONSTANT, get label() { return i18n.global.t('common.constant'); } },
  { value: valueType.VARIABLE, get label() { return i18n.global.t('common.variable'); } },
];

export default {
  components: {
    FilterValue,
    DataTypeDisplay,
    VariableSelect,
    Delete,
  },
  props: {
    modelValue: Array,
    showRequired: {
      type: Boolean,
      default: false,
    },
    showTargetType: {
      type: Boolean,
      default: true,
    },
    addText: String,
    sourceList: {
      type: Array,
      default: () => [],
    },
    targetList: {
      type: Array,
      default: () => [],
    },
    requiredKeys: {
      type: Array,
      default: () => [],
    },
  },
  emits: ['update:modelValue'],
  data() {
    return {
      rules: [...(this.modelValue || [])],
      columns: [
        { get name() { return i18n.global.t('common.paramDesc'); }, prop: 'source' },
        { get name() { return i18n.global.t('common.dataType'); }, prop: 'sourceDataType' },
        { get name() { return i18n.global.t('common.assignMethod'); }, prop: 'targetType' },
        { get name() { return i18n.global.t('common.assign'); }, prop: 'target' },
      ],
      assignTypeList,
      valueType,
    };
  },
  watch: {
    modelValue(val) {
      if (val !== this.rules) {
        this.rules = [...val];
      }
    },
  },
  methods: {
    addRule() {
      this.rules.push({
        source: '',
        sourceDataType: null,
        sourceType: valueType.VARIABLE,
        target: '',
        targetDataType: null,
        targetType: valueType.INPUT_PARAM,
        required: false,
      });
      this.onChange();
    },
    removeRule(rowIndex) {
      this.rules.splice(rowIndex, 1);
      this.onChange();
    },
    onChange() {
      this.$emit('update:modelValue', this.rules);
    },
    onSourceTypeChange(rowIndex) {
      this.rules[rowIndex].source = '';
      this.rules[rowIndex].sourceDataType = null;
      this.onChange();
    },
    onSourceVarChange(rowIndex) {
      const target = this.rules[rowIndex].target;
      const targetVariable = this.sourceList.find(item => item.paramKey === target);

      const source = this.rules[rowIndex].source;
      let sourceVariable = getVariableDataType(source, this.targetList);
      if (!isDataTypeEqual(targetVariable.dataType, sourceVariable.dataType)) {
        ElMessage.error(this.$t('common.typeMismatch'));
        this.rules[rowIndex].source = '';
        return;
      }
      this.rules[rowIndex].sourceDataType = sourceVariable.dataType;
      this.onChange();
    },
    getAvailableTarget(target) {
      return this.sourceList.filter(item => {
        if (item.paramKey === target) {
          return item;
        }
        return !this.rules.map(item => item.target).includes(item.paramKey);
      });
    },
    getAvailableSource(target, targetDataType) {
      return this.targetList.filter(item => {
        if (item.variableKey === target) {
          return false;
        }
        return isDataTypeMatch(item.dataType, targetDataType);
      });
    },
    onTargetChange(rowIndex) {
      const target = this.rules[rowIndex].target;
      const param = this.sourceList.find(item => item.paramKey === target);
      this.rules[rowIndex].source = '';
      this.rules[rowIndex].targetDataType = param.dataType;
      this.rules[rowIndex].targetType = param.targetType;
      if (this.requiredKeys.includes(target)) {
        this.rules[rowIndex].required = true;
      }
    },
  },
};
</script>

<template>
  <div class="rule-setting">
    <div class="rule-setting-head">
      <div class="rule-setting-tr">
        <template v-for="column in columns" :key="column.prop">
          <template v-if="column.prop === 'targetType'">
            <div class="rule-setting-td" v-if="showTargetType">{{ column.name }}</div>
          </template>
          <div class="rule-setting-td" v-else>{{ column.name }}</div>
        </template>
        <div class="rule-setting-td delete-td"></div>
      </div>
    </div>
    <div class="rule-setting-body">
      <div class="rule-setting-tr" v-for="(rule, rowIndex) in rules" :key="rowIndex">
        <template v-for="column in columns" :key="column.prop">
          <div class="rule-setting-td" v-if="column.prop === 'source'">
            <div class="required"><span v-if="requiredKeys.includes(rule.target)">*</span></div>
            <el-select v-model="rule.target" :disabled="requiredKeys.includes(rule.target)" size="small" @change="onTargetChange(rowIndex)">
              <el-option
                v-for="item in getAvailableTarget(rule.target)"
                :key="item.paramKey"
                :value="item.paramKey"
                :label="item.paramName"
                :title="item.paramName"
              />
            </el-select>
          </div>
          <div class="rule-setting-td" v-if="column.prop === 'sourceDataType'">
            <DataTypeDisplay :dataType="rule.targetDataType"/>
          </div>
          <div class="rule-setting-td" v-if="column.prop === 'targetType' && showTargetType">
            <el-select v-model="rule.sourceType" size="small" @change="onSourceTypeChange(rowIndex)">
              <el-option v-for="item in assignTypeList" :key="item.value" :value="item.value" :label="item.label" />
            </el-select>
          </div>
          <div class="rule-setting-td" v-if="column.prop === 'target'">
            <!-- 常量 -->
            <template v-if="rule.sourceType === valueType.CONSTANT">
              <FilterValue v-model="rule.source" :dataType="rule.targetDataType" size="small" :showNumberControls="false" />
            </template>
            <!-- 变量 -->
            <VariableSelect
                v-else
                v-model="rule.source"
                size="small"
                :options="getAvailableSource(rule.target, rule.targetDataType)"
                :filterDataType="rule.targetDataType"
                @change="onSourceVarChange(rowIndex)"
            />
          </div>
        </template>
        <div class="rule-setting-td delete-td">
          <el-icon @click="removeRule(rowIndex)" v-if="!requiredKeys.includes(rule.target)"><Delete /></el-icon>
        </div>
      </div>
    </div>
    <div class="rule-setting-foot">
      <el-button size="small" type="info" @click="addRule">{{ addText || $t('common.addInput') }}</el-button>
    </div>
  </div>
</template>

<style scoped>
.rule-setting {
  width: 100%;
}
.rule-setting-head {
  background-color: #f2f2f2;
  padding: 0 1px;
}
.rule-setting-body {
  border-left: 1px solid #f2f2f2;
  border-right: 1px solid #f2f2f2;
}
.rule-setting-tr {
  display: flex;
  border-bottom: 1px solid #f2f2f2;
  height: 36px;
}
.rule-setting-td {
  flex: 1;
  min-width: 0;
  padding: 0 6px;
  display: flex;
  align-items: center;
}
.rule-setting-td .required {
  color: red;
  width: 10px;
}
.rule-setting-td.delete-td {
  width: 20px;
  flex: none;
  justify-content: center;
  margin-right: 10px;
}
.rule-setting-td.delete-td > .el-icon {
  cursor: pointer;
  color: #999;
}
.rule-setting-foot {
  text-align: center;
  padding: 6px 0;
}
.rule-setting-td .dataTypeName {
  color: var(--el-text-color-regular);
}
</style>
