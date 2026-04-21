<script>
import { apiService } from '@/service';
import { ElMessage } from 'element-plus';
import FilterValue from '@/components/filter/FilterValue.vue';
import { InfoFilled } from '@element-plus/icons-vue';
import DataTypeDisplay from '@/components/common/DataTypeDisplay.vue';
import { safeTrim } from '@/utils/CommonUtil.js';
import VueJsonPretty from 'vue-json-pretty';

export default {
  components: {
    FilterValue,
    InfoFilled,
    DataTypeDisplay,
    VueJsonPretty,
  },
  data() {
    return {
      loading: false,
      flowResponseJson: undefined,
      apiInfo: {
        id: null,
        suiteId: null,
        suiteFlag: null,
        apiCode: '',
        apiUrl: '',
        apiName: '',
        apiDesc: '',
        apiRequestType: '',
        apiRequestContentType: '',
        apiHeaders: [],
        apiInputParams: [],
        apiOutputParams: [],
      },
      responseHeaderData: [],
    };
  },
  created() {
    this.queryApiInfo();
  },
  methods: {
    async queryApiInfo() {
      const res = await apiService.queryApiInfo(this.$route.params.apiId);
      if (res.success) {
        this.apiInfo = res.result;
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    async sendApiDebug() {
      if (!this.validate()) {
        return;
      }
      this.loading = true;
      const params = {
        headerData: this.getHeaders(),
        inputParamData: this.getParams(),
      };
      const res = await apiService.debugApi(this.$route.params.apiId, params);
      if (res.success) {
        this.flowResponseJson = res.result;
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
      this.responseHeaderData = Object.entries(res.response?.headers).map(([key, value]) => {
        return {
          headerKey: key,
          headerValue: value,
        };
      });
      this.loading = false;
    },
    isEmpty(val) {
      return val === undefined || val === null || val === '';
    },
    validate() {
      const apiHeaders = this.apiInfo?.apiHeaders || [];
      const apiInputParams = this.apiInfo?.apiInputParams || [];
      const errors = [];
      apiHeaders.forEach((param) => {
        if (param.required && this.isEmpty(param.value)) {
          param.error = this.$t('common.requiredEmpty');
          errors.push(param.paramKey);
        } else {
          param.error = '';
        }
      });
      apiInputParams.forEach((param) => {
        if (param.required && this.isEmpty(param.value)) {
          param.error = this.$t('common.requiredEmpty');
          errors.push(param.paramKey);
        } else {
          param.error = '';
        }
      });
      return errors.length === 0;
    },
    getHeaders() {
      const apiHeaders = this.apiInfo?.apiHeaders || [];
      const params = {};
      apiHeaders.forEach((param) => {
        if (!this.isEmpty(param.value)) {
          params[param.paramKey] = param.value;
        }
      });
      return params;
    },
    getParams() {
      const apiInputParams = this.apiInfo?.apiInputParams || [];
      const params = {};
      apiInputParams.forEach((param) => {
        const dataType = param.dataType;
        if (!this.isEmpty(param.value)) {
          if (dataType.type === 'Object' || dataType.type === 'List') {
            params[param.paramKey] = JSON.parse(param.value);
          } else if (dataType.type === 'String') {
            params[param.paramKey] = safeTrim(param.value);
          } else {
            params[param.paramKey] = param.value;
          }
        } else {
          if (dataType.type === 'Boolean') {
            params[param.paramKey] = false;
          }
        }
      });
      return params;
    },
    resetParams() {
      this.apiInfo?.apiHeaders.forEach((param) => {
        param.value = '';
        param.error = '';
      });
      this.apiInfo?.apiInputParams.forEach((param) => {
        param.value = '';
        param.error = '';
      });
    },
  },
};
</script>

<template>
  <div class="flow-debug"
       v-loading="loading"
       :element-loading-text="$t('suite.apiRequesting')">
    <div class="flow-debug-content">
       <div class="debug-header">
          <div class="debug-url">
            <el-input v-model="apiInfo.apiUrl">
              <template #prepend>
                <el-select v-model="apiInfo.apiRequestType" disabled style="width: 115px">
                  <el-option label="GET" value="GET" />
                  <el-option label="POST" value="POST" />
                  <el-option label="PUT" value="PUT" />
                  <el-option label="DELETE" value="DELETE" />
                </el-select>
              </template>
            </el-input>
          </div>
          <div class="debug-actions">
            <el-button type="primary" @click="sendApiDebug">{{ $t('common.send') }}</el-button>
            <el-button @click="resetParams">{{ $t('common.reset') }}</el-button>
          </div>
        </div>
       <div class="debug-inputParam">
        <el-tabs model-value="inputParam">
          <el-tab-pane :label="$t('suite.requestHeaders')" name="headerParam">
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
              <div class="input-param-tr" v-for="header in apiInfo?.apiHeaders" :key="header.paramKey">
                <div class="input-param-td">
                  <template v-if="header.required">*</template>
                </div>
                <div class="input-param-td" :title="header.paramKey">{{ header.paramKey }}</div>
                <div class="input-param-td" :title="header.paramName">
                  {{ header.paramName }}
                  <el-tooltip v-if="header.paramDesc" effect="dark" placement="top" :content="header.paramDesc">
                    <el-icon><InfoFilled /></el-icon>
                  </el-tooltip>
                </div>
                <div class="input-param-td">
                  <DataTypeDisplay :dataType="header.dataType"/>
                </div>
                <div class="input-param-td td-value">
                  <FilterValue v-model="header.value" :dataType="header.dataType" />
                </div>
                <div class="input-param-td td-error">{{ header.error || '' }}</div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane :label="$t('suite.inputParams')" name="inputParam">
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
              <div class="input-param-tr" v-for="param in apiInfo?.apiInputParams" :key="param.paramKey">
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
      </div>
       <div class="debug-result">
        <el-tabs model-value="result">
          <el-tab-pane :label="$t('flow.responseContent')" name="result">
            <div style="width: 80%">
              <vue-json-pretty
                  :data="flowResponseJson"
                  :deep="3"
                  show-length
                  show-line-num
              />
            </div>
          </el-tab-pane>
          <el-tab-pane :label="$t('flow.responseHeaders')" name="responseHeader">
            <el-table :data="responseHeaderData" style="width: 80%">
              <el-table-column prop="headerKey" :label="$t('flow.responseHeaders')" width="350" />
              <el-table-column prop="headerValue" :label="$t('flow.value')" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<style scoped>
.flow-debug {
  background-color: var(--el-bg-color-overlay);
  padding: 24px 40px;
}

.flow-debug .flow-debug-content {
  width: 70%;
  margin-left: 210px;
}

.flow-debug .input-param-body {
  color: var(--nexus-text-secondary);
}

.flow-debug .debug-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.flow-debug .debug-header .debug-url {
  width: 704px;
  margin-right: 14px;
}

.flow-debug .input-param-tr {
  display: flex;
  font-size: 14px;
  line-height: 28px;
  margin-bottom: 8px;
}

.flow-debug .input-param-td {
  margin-right: 12px;
  width: 130px;
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
