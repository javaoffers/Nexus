<script>
import { flowDefineService, flowVersionService } from '@/service';
import { ElMessage } from 'element-plus';
import CodeEditor from '@/components/common/CodeEditor.vue';
import FilterValue from '@/components/filter/FilterValue.vue';
import { InfoFilled } from '@element-plus/icons-vue';
import DataTypeDisplay from '@/components/common/DataTypeDisplay.vue';
import { safeTrim } from '@/utils/CommonUtil.js';

export default {
  components: {
    CodeEditor,
    FilterValue,
    InfoFilled,
    DataTypeDisplay,
  },
  data() {
    return {
      debugUrl: '',
      flowResponseJson: '',
      flowDefine: undefined,
      responseHeaderData: [],
      timerId: null,
    };
  },
  created() {
    this.queryFlowDefineInfo();
  },
  beforeUnmount() {
    if (this.timerId) {
      clearInterval(this.timerId);
    }
  },
  methods: {
    async queryFlowDefineInfo() {
      const res = await flowDefineService.getDefineInfo(this.$route.params.flowDefinitionId);
      if (res.success) {
        this.debugUrl = window.location.origin + '/v1/flow/definition/debug/' + this.$route.params.flowKey;
        this.flowDefine = res.result;
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    async sendFlowDebug() {
      if (!this.validate()) {
        return;
      }
      const params = {
        flowData: this.getParams(),
      };
      const res = await flowDefineService.debugFlow(this.$route.params.flowKey, params);
      if (res.success) {
        if (this.flowDefine?.flowType === 'sync') {
          this.flowResponseJson = JSON.stringify(res.result);
        } else {
          this.timerId = setInterval(this.getAsyncFlowResult, 1000, res.result.flowInstanceId);
        }
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
      this.responseHeaderData = Object.entries(res.response?.headers).map(([key, value]) => {
        return {
          headerKey: key,
          headerValue: value,
        };
      });
    },
    async getAsyncFlowResult(flowInstanceId) {
      const res = await flowVersionService.getAsyncFlowResult(flowInstanceId);
      if (res.success) {
        if (res.result) {
          this.flowResponseJson = JSON.stringify(res.result);
          clearInterval(this.timerId);
        }
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    isEmpty(val) {
      return val === undefined || val === null || val === '';
    },
    validate() {
      const flowInputParams = this.flowDefine?.flowInputParams || [];
      const errors = [];
      flowInputParams.forEach((param) => {
        if (param.required && this.isEmpty(param.value)) {
          param.error = this.$t('common.requiredEmpty');
          errors.push(param.paramKey);
        } else {
          param.error = '';
        }
      });
      return errors.length === 0;
    },
    getParams() {
      const flowInputParams = this.flowDefine?.flowInputParams || [];
      const params = {};
      flowInputParams.forEach((param) => {
        const dataType = param.dataType;
        if (!this.isEmpty(param.value) && dataType ) {
          if (dataType.type === 'Object' || dataType.type === 'List') {
            params[param.paramKey] = JSON.parse(param.value);
          } else if (dataType.type === 'String') {
            params[param.paramKey] = safeTrim(param.value);
          } else {
            params[param.paramKey] = param.value;
          }
        } else {
          if (dataType && dataType.type === 'Boolean') {
            params[param.paramKey] = false;
          }
        }
      });
      return params;
    },
    resetParams() {
      this.flowDefine?.flowInputParams.forEach((param) => {
        param.value = '';
        param.error = '';
      });
    },
  },
};
</script>

<template>
  <div class="flow-debug">
    <el-row :gutter="16">
      <el-col :span="20">
        <el-input v-model="debugUrl" />
      </el-col>
      <el-col :span="4">
        <el-button type="primary" @click="sendFlowDebug">{{ $t('common.send') }}</el-button>
        <el-button @click="resetParams">{{ $t('common.reset') }}</el-button>
      </el-col>
    </el-row>
    <el-tabs model-value="inputParam">
      <el-tab-pane :label="$t('flow.requestParams')" name="inputParam">
        <div class="input-param-head">
          <div class="input-param-tr">
            <div class="input-param-td"></div>
            <div class="input-param-td">{{ $t('common.paramName') }}</div>
            <div class="input-param-td">{{ $t('common.paramDesc') }}</div>
            <div class="input-param-td">{{ $t('common.paramType') }}</div>
            <div class="input-param-td td-value">{{ $t('common.paramValue') }}</div>
          </div>
        </div>
        <div class="input-param-body">
          <div class="input-param-tr" v-for="param in flowDefine?.flowInputParams" :key="param.paramKey">
            <div class="input-param-td">
              <template v-if="param.required">*</template>
            </div>
            <div class="input-param-td" >{{ param.paramKey }}</div>
            <div class="input-param-td" :title="param.paramName">
              {{ param.paramName }}
              <el-tooltip v-if="param.paramDesc" effect="dark" placement="top" :content="param.paramDesc">
                <el-icon><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
            <div class="input-param-td">
              <DataTypeDisplay :dataType="param.dataType"/>
            </div>
            <div class="input-param-td td-value">
              <FilterValue v-model="param.value" :dataType="param.dataType" />
            </div>
            <div class="input-param-td td-error">{{ param.error || '' }}</div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-tabs model-value="result">
      <el-tab-pane :label="$t('flow.responseContent')" name="result">
        <el-text line-clamp="2">
          <CodeEditor ref="codeEditRef" v-model="flowResponseJson" width="1000px" height="200px" language="json" />
        </el-text>
      </el-tab-pane>
      <el-tab-pane :label="$t('flow.responseHeaders')" name="responseHeader">
        <el-table :data="responseHeaderData" style="width: 100%">
          <el-table-column prop="headerKey" :label="$t('flow.responseHeaders')" width="350" />
          <el-table-column prop="headerValue" :label="$t('flow.value')" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.flow-debug {
  background-color: var(--el-bg-color-overlay);
  padding: 24px 40px;
}

.flow-debug .input-param-body {
  color: var(--nexus-text-secondary);
}

.flow-debug .input-param-tr {
  display: flex;
  font-size: 14px;
  line-height: 28px;
  margin-bottom: 8px;
}

.flow-debug .input-param-td {
  margin-right: 12px;
  width: 120px;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}

.flow-debug .input-param-td:first-child {
  margin: 0;
  width: 12px;
  color: var(--nexus-danger);
}

.flow-debug .input-param-td.td-value {
  width: 240px;
}

.flow-debug .input-param-td.td-error {
  color: var(--nexus-danger);
}
</style>
