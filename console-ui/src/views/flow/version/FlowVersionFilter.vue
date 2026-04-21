<script>
export default {
  emits: ['search'],
  data() {
    return {
      formValue: {
        flowVersionStatus: '',
      },
    };
  },
  methods: {
    onSubmit() {
      this.$emit('search', this.formValue);
    },
    onReset() {
      this.formValue.flowVersionStatus = '';
    },
  },
};
</script>

<template>
  <el-form :inline="true" :model="formValue">
    <el-form-item :label="$t('flow.flowStatus')" style="width: 250px">
      <el-select v-model="formValue.flowVersionStatus" :placeholder="$t('flow.selectFlowType')">
        <el-option key="all" :label="$t('flow.all')" value="" />
        <el-option key="ENABLE" :label="$t('flow.enabled')" value="1" />
        <el-option key="DISABLED" :label="$t('flow.disabled')" value="0" />
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="onSubmit">{{ $t('common.search') }}</el-button>
    </el-form-item>
    <el-form-item>
      <el-button @click="onReset">{{ $t('common.reset') }}</el-button>
    </el-form-item>
  </el-form>
</template>
