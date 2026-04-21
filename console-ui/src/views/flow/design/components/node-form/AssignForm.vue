<script>
import { ElementType, FlowVariableType } from '../../types';
import { cloneDeep } from 'lodash-es';
import { ElMessage } from 'element-plus';
import { Delete } from '@element-plus/icons-vue';
import { valueType } from '@/typings';
import FilterValue from '@/components/filter/FilterValue.vue';
import { getVariableDataType, isDataTypeEqual, isDataTypeMatch } from '@/utils/dataType';
import VariableSelect from '@/components/common/VariableSelect.vue';
import DataTypeDisplay from '@/components/common/DataTypeDisplay.vue';

var assignTypeList = [
  { value: valueType.CONSTANT, labelKey: 'common.constant' },
  { value: valueType.VARIABLE, labelKey: 'common.variable' },
];

var columns = [
  { nameKey: 'design.varName2', prop: 'source' },
  { nameKey: 'common.dataType', prop: 'sourceDataType' },
  { nameKey: 'common.assignMethod', prop: 'targetType' },
  { nameKey: 'common.assign', prop: 'target' },
];

function getDefaultData() {
  return {
    key: '',
    name: '',
    desc: '',
    outgoings: [],
    incomings: [],
    elementType: ElementType.ASSIGN,
    assignRules: [],
  };
}

export default {
  components: {
    Delete,
    FilterValue,
    VariableSelect,
    DataTypeDisplay,
  },
  props: {
    data: {
      type: Object,
      required: true,
    },
  },
  emits: ['update', 'cancel'],
  data() {
    return {
      nodeData: getDefaultData(),
      assignTypeList: assignTypeList,
      columns: columns,
      valueType: valueType,
    };
  },
  computed: {
    targetVariableList() {
      return this.$store.state.flow.flowVariables;
    },
    sourceVariableList() {
      var flowVariables = this.$store.state.flow.flowVariables;
      return flowVariables.filter(function (item) {
        return [FlowVariableType.INPUT, FlowVariableType.TEMP].indexOf(item.variableType) !== -1;
      });
    },
  },
  watch: {
    data: {
      handler(val) {
        if (val !== this.nodeData) {
          this.nodeData = Object.assign(getDefaultData(), cloneDeep(val));
        }
      },
      immediate: true,
    },
  },
  methods: {
    validate() {
      if (!this.nodeData.name) {
        ElMessage.error(this.$t('design.nodeNameRequired'));
        return false;
      }
      return true;
    },
    onSubmit() {
      if (!this.validate()) {
        return;
      }
      this.$emit('update', cloneDeep(this.nodeData));
    },
    onCancel() {
      this.$emit('cancel');
    },
    getAvailableTarget(target) {
      var self = this;
      return this.targetVariableList.filter(function (item) {
        if (item.variableKey === target) {
          return true;
        }
        return self.nodeData.assignRules.map(function (r) { return r.target; }).indexOf(item.variableKey) === -1;
      });
    },
    getAvailableSource(target, targetDataType) {
      return this.sourceVariableList.filter(function (item) {
        if (item.variableKey === target) {
          return false;
        }
        return isDataTypeMatch(item.dataType, targetDataType);
      });
    },
    onTargetVariableChange(rowIndex) {
      var target = this.nodeData.assignRules[rowIndex].target;
      var param = this.targetVariableList.find(function (item) { return item.variableKey === target; });
      this.nodeData.assignRules[rowIndex].source = '';
      this.nodeData.assignRules[rowIndex].sourceDataType = param ? param.dataType : null;
      this.nodeData.assignRules[rowIndex].targetDataType = param ? param.dataType : null;
    },
    onAssignTypeChange(rowIndex) {
      this.nodeData.assignRules[rowIndex].source = '';
    },
    onSourceVariableChange(rowIndex) {
      var target = this.nodeData.assignRules[rowIndex].target;
      var targetVariable = getVariableDataType(target, this.targetVariableList);

      var source = this.nodeData.assignRules[rowIndex].source;
      var sourceVariable = getVariableDataType(source, this.sourceVariableList);
      if (!isDataTypeEqual(targetVariable.dataType, sourceVariable.dataType)) {
        ElMessage.error(this.$t('common.typeMismatch'));
        this.nodeData.assignRules[rowIndex].source = '';
        return;
      }
      this.nodeData.assignRules[rowIndex].sourceDataType = sourceVariable.dataType;
    },
    addAssignRule() {
      if (this.nodeData.assignRules === null) {
        this.nodeData.assignRules = [];
      }
      this.nodeData.assignRules.push({
        source: '',
        sourceDataType: null,
        sourceType: valueType.VARIABLE,
        target: '',
        targetDataType: null,
        targetType: valueType.VARIABLE,
      });
    },
    removeRule(rowIndex) {
      this.nodeData.assignRules.splice(rowIndex, 1);
    },
  },
};
</script>

<template>
  <div class="node-method-form">
    <el-form label-position="top">
      <el-form-item :label="$t('design.nodeCode')">
        <span>{{ nodeData.key }}</span>
      </el-form-item>
      <el-form-item :label="$t('design.nodeName')">
        <el-input v-model="nodeData.name" maxlength="16" :placeholder="$t('common.pleaseInput')"></el-input>
      </el-form-item>
      <el-form-item :label="$t('design.nodeDesc')">
        <el-input v-model="nodeData.desc" maxlength="60" :placeholder="$t('common.pleaseInput')" :rows="2" type="textarea"></el-input>
      </el-form-item>
      <el-form-item :label="$t('design.assignRule')">
        <div class="rule-setting">
          <div class="rule-setting-head">
            <div class="rule-setting-tr">
              <template v-for="column in columns" :key="column.prop">
                <div class="rule-setting-td">{{ $t(column.nameKey) }}</div>
              </template>
              <div class="rule-setting-td delete-td"></div>
            </div>
          </div>
          <div class="rule-setting-body">
            <div class="rule-setting-tr" v-for="(rule, rowIndex) in nodeData.assignRules" :key="rowIndex">
              <template v-for="column in columns" :key="column.prop">
                <div class="rule-setting-td" v-if="column.prop === 'source'">
                  <el-select v-model="rule.target" size="small" @change="onTargetVariableChange(rowIndex)">
                    <el-option
                        v-for="item in getAvailableTarget(rule.target)"
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
                <div class="rule-setting-td" v-if="column.prop === 'sourceDataType'">
                  <DataTypeDisplay :dataType="rule.targetDataType"/>
                </div>
                <div class="rule-setting-td" v-if="column.prop === 'targetType'">
                  <el-select v-model="rule.sourceType" size="small" @change="onAssignTypeChange(rowIndex)">
                    <el-option v-for="item in assignTypeList" :key="item.value" :value="item.value" :label="$t(item.labelKey)" />
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
                      @change="onSourceVariableChange(rowIndex)"
                  />
                </div>
              </template>
              <div class="rule-setting-td delete-td">
                <el-icon @click="removeRule(rowIndex)"><Delete /></el-icon>
              </div>
            </div>
          </div>
          <div class="rule-setting-foot">
            <el-button size="small" type="info" @click="addAssignRule">{{ $t('design.addAssign') }}</el-button>
          </div>
        </div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">{{ $t('common.confirm') }}</el-button>
        <el-button @click="onCancel">{{ $t('common.cancel') }}</el-button>
      </el-form-item>
    </el-form>
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
