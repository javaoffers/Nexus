<script>
export default {
  emits: ['add', 'edit'],
  data() {
    return {
      dialogVisible: false,
      editItem: undefined,
      formValue: {
        tokenDesc: '',
      },
      rules: {
        tokenDesc: [{ required: true, message: this.$t('system.inputTokenDesc'), trigger: 'blur' }],
      },
    };
  },
  computed: {
    title() {
      if (this.editItem) {
        return this.$t('system.editToken');
      }
      return this.$t('system.addToken');
    },
  },
  methods: {
    onCancel() {
      this.dialogVisible = false;
    },
    async onSubmit() {
      if (!this.$refs.formRef) return;
      const valid = await this.$refs.formRef.validate(() => {});
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
      this.$nextTick(() => {
        this.$refs.formRef?.resetFields();
        if (item) {
          this.formValue.tokenDesc = item.tokenDesc;
        }
      });
    },
  },
};
</script>
<template>
  <el-dialog v-model="dialogVisible" :title="title" width="400">
    <div class="form">
      <el-form ref="formRef" label-position="top" :model="formValue" :rules="rules">
        <el-form-item :label="$t('system.tokenDesc')" prop="tokenDesc">
          <el-input v-model="formValue.tokenDesc" maxlength="120" />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="onCancel">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="onSubmit">{{ $t('common.confirm') }}</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped>
.dialog-footer button:first-child {
  margin-right: 10px;
}
</style>
