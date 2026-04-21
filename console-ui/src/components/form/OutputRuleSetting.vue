<script>
import { valueType } from '@/typings';
import { Delete } from '@element-plus/icons-vue';
import { isDataTypeEqual } from '@/utils/dataType';
import DataTypeDisplay from '@/components/common/DataTypeDisplay.vue';
import i18n from '@/i18n';

export default {
  components: {
    DataTypeDisplay,
    Delete,
  },
  props: {
    modelValue: Array,
    addText: String,
    sourceList: {
      type: Array,
      default: () => [],
    },
    targetList: {
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
        { get name() { return i18n.global.t('common.assign'); }, prop: 'target' },
      ],
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
        sourceType: valueType.INPUT_PARAM,
        target: '',
        targetDataType: null,
        targetType: valueType.VARIABLE,
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
    onTargetVarChange(rowIndex) {
      const target = this.rules[rowIndex].target;
      const param = this.targetList.find(item => item.variableKey === target);
      this.rules[rowIndex].targetDataType = param.dataType;
      this.onChange();
    },
    getAvailableSource(source) {
      return this.sourceList.filter(item => {
        if (item.paramKey === source) {
          return item;
        }
        return !this.rules.map(item => item.source).includes(item.paramKey);
      });
    },
    getAvailableTarget(source, sourceDataType) {
      return this.targetList.filter(item => {
        if (item.variableKey === source) {
          return false;
        }
        return isDataTypeEqual(item.dataType, sourceDataType);
      });
    },
    onSourceChange(rowIndex) {
      const source = this.rules[rowIndex].source;
      const param = this.sourceList.find(item => item.paramKey === source);
      this.rules[rowIndex].target = '';
      this.rules[rowIndex].sourceDataType = param.dataType;
      this.rules[rowIndex].sourceType = param.sourceType;
    },
  },
};
</script>

<template>
  <div class="rule-setting">
    <div class="rule-setting-head">
      <div class="rule-setting-tr">
        <template v-for="column in columns" :key="column.prop">
          <div class="rule-setting-td">{{ column.name }}</div>
        </template>
        <div class="rule-setting-td delete-td"></div>
      </div>
    </div>
    <div class="rule-setting-body">
      <div class="rule-setting-tr" v-for="(rule, rowIndex) in rules" :key="rowIndex">
        <template v-for="column in columns" :key="column.prop">
          <div class="rule-setting-td" v-if="column.prop === 'source'">
            <el-select v-model="rule.source" size="small" @change="onSourceChange(rowIndex)">
              <el-option
                v-for="item in getAvailableSource(rule.source)"
                :key="item.paramKey"
                :value="item.paramKey"
                :label="item.paramName"
                :title="item.paramName"
              />
            </el-select>
          </div>
          <div class="rule-setting-td" v-if="column.prop === 'sourceDataType'">
            <DataTypeDisplay :dataType="rule.sourceDataType"/>
          </div>
          <div class="rule-setting-td" v-if="column.prop === 'target'">
            <!-- 变量 -->
            <el-select v-model="rule.target" size="small" @change="onTargetVarChange(rowIndex)">
              <el-option
                v-for="item in getAvailableTarget(rule.source, rule.sourceDataType)"
                :key="item.variableKey"
                :value="item.variableKey"
                :label="item.variableName"
                :title="item.variableName"
              >
                <span style="float: left">{{ item.variableKey }}</span>
                <span style="float: right;color: var(--el-text-color-secondary);font-size: 13px;margin-left: 5px;">
                        {{ item.variableName }}
                      </span>
              </el-option>
            </el-select>
          </div>
        </template>
        <div class="rule-setting-td delete-td">
          <el-icon @click="removeRule(rowIndex)"><Delete /></el-icon>
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
.rule-setting-td.delete-td,
.rule-setting-td.required-td {
  width: 40px;
  flex: none;
  justify-content: center;
}
.rule-setting-td.delete-td {
  width: 20px;
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
