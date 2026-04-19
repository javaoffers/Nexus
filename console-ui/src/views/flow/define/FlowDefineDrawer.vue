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
        flowName: [{ required: true, message: '请输入流程名称', trigger: 'blur' }],
        flowType: [{ required: true, message: '请选择流程类型', trigger: 'blur' }],
      },
    };
  },
  computed: {
    title() {
      if (this.editItem) {
        return '编辑流程定义';
      }
      return '新增流程定义';
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
        <el-form-item label="流程名称" prop="flowName">
          <el-input v-model="flowDefineFormValue.flowName" maxlength="30" />
        </el-form-item>
        <el-form-item label="流程类型" prop="flowType">
          <el-select placeholder="请选择流程类型" v-model="flowDefineFormValue.flowType">
            <el-option label="同步" value="sync" />
            <el-option label="异步" value="async" />
          </el-select>
        </el-form-item>
        <el-form-item label="流程描述">
          <el-input type="textarea" v-model="flowDefineFormValue.remark" maxlength="120" />
        </el-form-item>
        <el-form-item label="流程入参">
          <ParamSetting v-model="flowDefineFormValue.flowInputParams" addText="新增入参" showRequired />
        </el-form-item>
        <el-form-item label="流程出参">
          <ParamSetting v-model="flowDefineFormValue.flowOutputParams" addText="新增出参" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">确定</el-button>
          <el-button @click="onCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </ResizableDrawer>
</template>

<style scoped></style>
