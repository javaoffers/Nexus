<script>
import ParamSetting from '@/components/form/ParamSetting.vue';
import { apiService } from '@/service';
import { ApiRequestContentTypeMap, ApiRequestTypeMap } from '@/const';
import ResizableDrawer from '@/components/common/ResizableDrawer.vue';

export default {
  components: {
    ParamSetting,
    ResizableDrawer,
  },
  emits: ['add', 'edit'],
  data() {
    return {
      ApiRequestTypes: Object.keys(ApiRequestTypeMap),
      ApiRequestContentTypes: Object.keys(ApiRequestContentTypeMap),
      dialogVisible: false,
      editItem: undefined,
      suitFlag: undefined,
      formValue: this.getDefault(),
      rules: {
        apiName: [{ required: true, message: this.$t('suite.inputApiName'), trigger: 'blur' }],
        apiUrl: [
          { required: true, message: this.$t('suite.inputApiUrl'), trigger: 'blur' },
          { type: 'url', message: this.$t('suite.apiUrlFormat'), trigger: ['blur', 'change'] },
        ],
        apiRequestType: [{ required: true, message: this.$t('suite.selectRequestType'), trigger: 'blur' }],
        apiRequestContentType: [{ required: true, message: this.$t('suite.selectContentType'), trigger: 'blur' }],
      },
    };
  },
  computed: {
    title() {
      if (this.editItem) {
        return this.$t('suite.editApi');
      }
      return this.$t('suite.addApi');
    },
  },
  methods: {
    getDefault() {
      return {
        id: null,
        suiteId: null,
        apiCode: '',
        apiName: '',
        apiDesc: '',
        apiUrl: '',
        apiRequestType: '',
        apiRequestContentType: '',
        apiHeaders: [],
        apiInputParams: [],
        apiOutputParams: [],
      };
    },
    onCancel() {
      this.dialogVisible = false;
    },
    async onSubmit() {
      if (!this.$refs.apiFormRef) return;
      const valid = await this.$refs.apiFormRef.validate(() => {});
      if (!valid) {
        return;
      }

      this.dialogVisible = false;
      if (this.editItem) {
        this.$emit('edit', { ...this.editItem, ...this.formValue });
      } else {
        this.$emit('add', this.formValue);
      }
    },
    open(item) {
      this.editItem = item;
      this.dialogVisible = true;
      this.$nextTick(async () => {
        this.$refs.apiFormRef?.resetFields();
        if (item) {
          const res = await apiService.queryApiInfo(item.id);
          if (res.success) {
            this.formValue.id = res.result.id;
            this.formValue.suiteId = res.result.suiteId;
            this.formValue.apiCode = res.result.apiCode;
            this.formValue.apiName = res.result.apiName;
            this.formValue.apiDesc = res.result.apiDesc;
            this.formValue.apiUrl = res.result.apiUrl;
            this.formValue.apiRequestType = res.result.apiRequestType;
            this.formValue.apiRequestContentType = res.result.apiRequestContentType;
            this.formValue.apiHeaders = res.result.apiHeaders;
            this.formValue.apiInputParams = res.result.apiInputParams;
            this.formValue.apiOutputParams = res.result.apiOutputParams;
            this.suitFlag = res.result.suiteFlag;
          }
        } else {
          Object.assign(this.formValue, this.getDefault());
        }
      });
    },
  },
};
</script>
<template>
  <ResizableDrawer v-model="dialogVisible" :size="600" :title="title" destroyOnClose drawer-key="API">
    <div class="form">
      <el-form ref="apiFormRef" label-position="top" :model="formValue" :rules="rules">
        <el-form-item :label="$t('suite.apiName')" prop="apiName">
          <el-input v-model="formValue.apiName" maxlength="30" />
        </el-form-item>
        <el-form-item :label="$t('suite.apiUrl')" prop="apiUrl">
          <el-input v-model="formValue.apiUrl" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item :label="$t('suite.requestType')" prop="apiRequestType">
              <el-select v-model="formValue.apiRequestType">
                <el-option v-for="op in ApiRequestTypes" :value="op" :key="op">{{ op }}</el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="18">
            <el-form-item :label="$t('suite.contentType')" prop="apiRequestContentType">
              <el-select v-model="formValue.apiRequestContentType" style="width: 100%">
                <el-option v-for="op in ApiRequestContentTypes" :value="op" :key="op">{{ op }}</el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item :label="$t('suite.apiDesc')" prop="apiDesc">
          <el-input v-model="formValue.apiDesc" type="textarea" :rows="2" maxlength="120" />
        </el-form-item>
        <el-form-item :label="$t('suite.requestHeaders')">
          <ParamSetting v-model="formValue.apiHeaders" :addText="$t('suite.addHeaders')" dataTypeClassify="basic" showRequired />
        </el-form-item>
        <el-form-item :label="$t('suite.inputParams')">
          <ParamSetting v-model="formValue.apiInputParams" :addText="$t('suite.addInput')" showParamPosition showRequired />
        </el-form-item>
        <el-form-item :label="$t('suite.outputParams')">
          <ParamSetting v-model="formValue.apiOutputParams" :addText="$t('suite.addOutput')" />
        </el-form-item>
        <el-form-item v-if="suitFlag !== 1">
          <el-button type="primary" @click="onSubmit">{{ $t('common.confirm') }}</el-button>
          <el-button @click="onCancel">{{ $t('common.cancel') }}</el-button>
        </el-form-item>
      </el-form>
    </div>
  </ResizableDrawer>
</template>

<style scoped>
.dialog-footer button:first-child {
  margin-right: 10px;
}
</style>
