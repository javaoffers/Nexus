<script>
import ApiSelect from '@/components/form/ApiSelect.vue';
import OutputRuleSetting from '@/components/form/OutputRuleSetting.vue';
import InputRuleSetting from '@/components/form/InputRuleSetting.vue';
import { ElementType, FlowVariableType } from '../../types';
import { apiService, suiteService } from '@/service';
import { valueType } from '@/typings';
import { cloneDeep } from 'lodash-es';
import { ElMessage } from 'element-plus';

function getMethodDefaultData() {
  return {
    methodCode: null,
    suiteCode: null,
    url: '',
    requestType: '',
    requestContentType: '',
    inputParamSchemas: [],
    outputParamSchemas: [],
    headerFillRules: [],
    inputFillRules: [],
    outputFillRules: [],
  };
}

function getDefaultData() {
  return {
    key: '',
    name: '',
    outgoings: [],
    incomings: [],
    elementType: ElementType.METHOD,
    desc: '',
    method: getMethodDefaultData(),
  };
}

function paramToRule(param, sourceType) {
  return {
    source: '',
    sourceDataType: null,
    sourceType: valueType.VARIABLE,
    target: param.paramKey,
    targetDataType: param.dataType,
    targetType: sourceType,
    required: true,
  };
}

export default {
  components: {
    ApiSelect,
    OutputRuleSetting,
    InputRuleSetting,
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
      suiteList: [],
      suiteLoading: false,
      suiteLoaded: false,
      headerSourceList: [],
      inputSourceList: [],
      outputSourceList: [],
      headerRequiredKeys: [],
      inputRequiredKeys: [],
      currentSuiteCode: null,
      syncingSuite: false,
    };
  },
  computed: {
    inputTargetList() {
      var flowVariables = this.$store.state.flow.flowVariables;
      return flowVariables.filter(function (item) {
        return [FlowVariableType.INPUT, FlowVariableType.TEMP].indexOf(item.variableType) !== -1;
      });
    },
    outputTargetList() {
      var flowVariables = this.$store.state.flow.flowVariables;
      return flowVariables.filter(function (item) {
        return [FlowVariableType.OUTPUT, FlowVariableType.TEMP].indexOf(item.variableType) !== -1;
      });
    },
  },
  watch: {
    data: {
      handler(val) {
        if (val !== this.nodeData) {
          this.nodeData = Object.assign(getDefaultData(), cloneDeep(val));
          this.syncingSuite = true;
          this.currentSuiteCode = this.nodeData.method.suiteCode;
          this.$nextTick(() => { this.syncingSuite = false; });
          if (this.nodeData.method && this.nodeData.method.methodCode) {
            this.initApiSourceList(this.nodeData.method.methodCode);
          } else {
            this.nodeData.method = getMethodDefaultData();
          }
        }
      },
      immediate: true,
    },
    currentSuiteCode(val) {
      if (this.syncingSuite) return;
      this.nodeData.method = Object.assign({}, getDefaultData().method, { suiteCode: val });
    },
  },
  created() {
    this.querySuiteList();
  },
  methods: {
    async initApiSourceList(apiId) {
      var res = await apiService.queryApiInfo(apiId);
      if (res.result) {
        var result = res.result;
        this.headerSourceList = result.apiHeaders.map(function (item) { return Object.assign({}, item, { targetType: valueType.HEADER }); });
        this.inputSourceList = result.apiInputParams.map(function (item) { return Object.assign({}, item, { targetType: valueType.INPUT_PARAM }); });
        this.outputSourceList = result.apiOutputParams.map(function (item) { return Object.assign({}, item, { sourceType: valueType.OUTPUT_PARAM }); });
        var headerRequired = result.apiHeaders.filter(function (item) { return item.required; });
        this.headerRequiredKeys = headerRequired.map(function (item) { return item.paramKey; });
        var inputRequired = result.apiInputParams.filter(function (item) { return item.required; });
        this.inputRequiredKeys = inputRequired.map(function (item) { return item.paramKey; });
      }
    },
    async onApiChange(apiId) {
      var res = await apiService.queryApiInfo(apiId);
      console.log('res=', res);
      if (res.result) {
        var result = res.result;
        var method = this.nodeData.method;
        method.requestType = result.apiRequestType;
        method.requestContentType = result.apiRequestContentType;
        method.inputParamSchemas = result.apiInputParams.map(function (item) {
          return {
            key: item.paramKey,
            name: item.paramName,
            dataType: item.dataType,
            position: item.paramPosition,
          };
        });
        method.outputParamSchemas = result.apiOutputParams.map(function (item) {
          return {
            key: item.paramKey,
            name: item.paramName,
            dataType: item.dataType,
          };
        });
        this.headerSourceList = result.apiHeaders.map(function (item) { return Object.assign({}, item, { targetType: valueType.HEADER }); });
        this.inputSourceList = result.apiInputParams.map(function (item) { return Object.assign({}, item, { targetType: valueType.INPUT_PARAM }); });
        this.outputSourceList = result.apiOutputParams.map(function (item) { return Object.assign({}, item, { sourceType: valueType.OUTPUT_PARAM }); });
        var headerRequired = result.apiHeaders.filter(function (item) { return item.required; });
        method.headerFillRules = headerRequired.map(function (item) { return paramToRule(item, valueType.HEADER); });
        this.headerRequiredKeys = headerRequired.map(function (item) { return item.paramKey; });
        var inputRequired = result.apiInputParams.filter(function (item) { return item.required; });
        method.inputFillRules = inputRequired.map(function (item) { return paramToRule(item, valueType.INPUT_PARAM); });
        this.inputRequiredKeys = inputRequired.map(function (item) { return item.paramKey; });
        method.outputFillRules = [];
        method.url = result.apiUrl;
      }
    },
    validateParam(param) {
      if (!param.source) {
        return false;
      }
      if (!param.target) {
        return false;
      }
      return true;
    },
    validate() {
      if (!this.nodeData.name) {
        ElMessage.error(this.$t('design.nodeNameRequired'));
        return false;
      }
      var method = this.nodeData.method;
      if (!method.methodCode) {
        ElMessage.error(this.$t('design.serviceApiRequired'));
        return false;
      }
      if (method.headerFillRules.length > 0 && !method.headerFillRules.every(this.validateParam)) {
        ElMessage.error(this.$t('design.headerAssignIncomplete'));
        return false;
      }
      if (method.inputFillRules.length > 0 && !method.inputFillRules.every(this.validateParam)) {
        ElMessage.error(this.$t('design.inputAssignIncomplete'));
        return false;
      }
      if (method.outputFillRules.length > 0 && !method.outputFillRules.every(this.validateParam)) {
        ElMessage.error(this.$t('design.outputAssignIncomplete'));
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
    async querySuiteList() {
      this.suiteLoading = true;
      var res = await suiteService.querySuiteList();
      if (res.success) {
        this.suiteList = res.result;
      }
      this.suiteLoaded = true;
      this.suiteLoading = false;
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
      <el-form-item :label="$t('design.nodeName')" required>
        <el-input v-model="nodeData.name" maxlength="16" :placeholder="$t('common.pleaseInput')"></el-input>
      </el-form-item>
      <el-form-item :label="$t('design.nodeDesc')">
        <el-input v-model="nodeData.desc" maxlength="60" :placeholder="$t('common.pleaseInput')" :rows="2" type="textarea"></el-input>
      </el-form-item>
      <el-form-item :label="$t('design.suite')" required>
        <el-select
            v-model="currentSuiteCode"
            :placeholder="$t('design.selectSuite')"
            style="width: 100%"
            filterable
        >
          <template v-slot:empty>
            <div class="select-option-empty" v-loading="suiteLoading">
              <span v-if="!suiteLoading">{{ $t('design.noData') }}</span>
            </div>
          </template>
          <el-option v-for="item in suiteList" :key="item.suiteCode" :label="item.suiteName" :value="item.suiteCode" />
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('design.serviceApi')" required>
        <ApiSelect v-model="nodeData.method.methodCode" :suiteCode="nodeData.method.suiteCode" @change="onApiChange" />
      </el-form-item>
      <el-form-item :label="$t('design.headerAssign')">
        <InputRuleSetting
          v-model="nodeData.method.headerFillRules"
          :addText="$t('design.addHeaderAssign')"
          showRequired
          :sourceList="headerSourceList"
          :targetList="inputTargetList"
          :requiredKeys="headerRequiredKeys"
        />
      </el-form-item>
      <el-form-item :label="$t('design.inputAssign')">
        <InputRuleSetting
          v-model="nodeData.method.inputFillRules"
          :addText="$t('design.addInputAssign')"
          showRequired
          :sourceList="inputSourceList"
          :targetList="inputTargetList"
          :requiredKeys="inputRequiredKeys"
        />
      </el-form-item>
      <el-form-item :label="$t('design.outputAssign')">
        <OutputRuleSetting
          v-model="nodeData.method.outputFillRules"
          :addText="$t('design.addOutputAssign')"
          :sourceList="outputSourceList"
          :targetList="outputTargetList"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">{{ $t('common.confirm') }}</el-button>
        <el-button @click="onCancel">{{ $t('common.cancel') }}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
