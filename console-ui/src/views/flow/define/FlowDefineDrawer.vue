<script>
import ParamSetting from '@/components/form/ParamSetting.vue';
import { flowDefineService } from '@/service';
import ResizableDrawer from '@/components/common/ResizableDrawer.vue';

export default {
  components: {
    ParamSetting,
    ResizableDrawer,
  },
  emits: ['add', 'edit'],
  data() {
    return {
      flowDefineDrawerVisible: false,
      editItem: undefined,
      flowDefineFormValue: this.getDefaultFlowDefine(),
      rules: {
        flowName: [{ required: true, message: this.$t('flow.inputFlowName'), trigger: 'blur' }],
        flowType: [{ required: true, message: this.$t('flow.selectFlowType'), trigger: 'blur' }],
      },
    };
  },
  computed: {
    title() {
      if (this.editItem) {
        return this.$t('flow.editFlowDefine');
      }
      return this.$t('flow.addFlowDefine');
    },
  },
  methods: {
    getDefaultFlowDefine() {
      return {
        id: null,
        flowName: '',
        flowType: '',
        remark: '',
        flowInputParams: [],
        flowOutputParams: [],
      };
    },
    onCancel() {
      this.flowDefineDrawerVisible = false;
    },
    async onSubmit() {
      if (!this.$refs.formRef) return;
      const valid = await this.$refs.formRef.validate(() => {});
      if (!valid) {
        return;
      }

      this.flowDefineDrawerVisible = false;
      if (this.editItem) {
        this.$emit('edit', { ...this.editItem, ...this.flowDefineFormValue });
      } else {
        this.$emit('add', this.flowDefineFormValue);
      }
    },
    open(item) {
      this.editItem = item;
      this.flowDefineDrawerVisible = true;
      this.$nextTick(async () => {
        this.$refs.formRef?.resetFields();
        if (item) {
          const res = await flowDefineService.getDefineInfo(item.id);
          if (res.success) {
            this.flowDefineFormValue.id = res.result.id;
            this.flowDefineFormValue.flowName = res.result.flowName;
            this.flowDefineFormValue.flowType = res.result.flowType;
            this.flowDefineFormValue.remark = res.result.remark;
            this.flowDefineFormValue.flowInputParams = res.result.flowInputParams;
            this.flowDefineFormValue.flowOutputParams = res.result.flowOutputParams;
          }
        } else {
          Object.assign(this.flowDefineFormValue, this.getDefaultFlowDefine());
        }
      });
    },
  },
};
</script>

<template>
  <ResizableDrawer v-model="flowDefineDrawerVisible" :size="600" :title="title" drawer-key="FLOW_DEFINE" destroyOnClose>
    <div>
      <el-form ref="formRef" label-position="top" :model="flowDefineFormValue" :rules="rules">
        <el-form-item :label="$t('flow.flowName')" prop="flowName">
          <el-input v-model="flowDefineFormValue.flowName" maxlength="30" />
        </el-form-item>
        <el-form-item :label="$t('flow.flowType')" prop="flowType">
          <el-select :placeholder="$t('flow.selectFlowType')" v-model="flowDefineFormValue.flowType">
            <el-option :label="$t('flow.sync')" value="sync" />
            <el-option :label="$t('flow.async')" value="async" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('flow.flowDesc')">
          <el-input type="textarea" v-model="flowDefineFormValue.remark" maxlength="120" />
        </el-form-item>
        <el-form-item :label="$t('flow.flowInput')">
          <ParamSetting v-model="flowDefineFormValue.flowInputParams" :addText="$t('flow.addInput')" showRequired />
        </el-form-item>
        <el-form-item :label="$t('flow.flowOutput')">
          <ParamSetting v-model="flowDefineFormValue.flowOutputParams" :addText="$t('flow.addOutput')" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">{{ $t('common.confirm') }}</el-button>
          <el-button @click="onCancel">{{ $t('common.cancel') }}</el-button>
        </el-form-item>
      </el-form>
    </div>
  </ResizableDrawer>
</template>

<style scoped></style>
