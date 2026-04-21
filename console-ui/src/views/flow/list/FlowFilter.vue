<script>
export default {
  emits: ['search'],
  data() {
    return {
      formValue: {
        flowName: '',
        flowType: '',
      },
    };
  },
  methods: {
    onSubmit() {
      this.$emit('search', this.formValue);
    },
    onReset() {
      this.formValue.flowName = '';
      this.formValue.flowType = '';
    },
  },
};
</script>

<template>
  <el-form :inline="true" :model="formValue">
    <el-form-item :label="$t('flow.flowName')">
      <el-input v-model="formValue.flowName" :placeholder="$t('flow.inputFlowName')" />
    </el-form-item>
    <el-form-item :label="$t('flow.flowType')" style="width: 250px">
      <el-select v-model="formValue.flowType" :placeholder="$t('flow.selectFlowType')">
        <el-option key="all" :label="$t('flow.all')" value="" />
        <el-option key="sync" :label="$t('flow.sync')" value="sync" />
        <el-option key="async" :label="$t('flow.async')" value="async" />
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
