<script>
export default {
  emits: ['search'],
  data() {
    return {
      formValue: {
        suiteId: null,
        apiName: '',
        apiUrl: '',
      },
    };
  },
  methods: {
    onSubmit() {
      this.$emit('search', this.formValue);
    },
    onReset() {
      this.formValue.suiteId = null;
      this.formValue.apiName = '';
      this.formValue.apiUrl = '';
    },
  },
};
</script>
<template>
  <el-form :inline="true" :model="formValue">
    <el-form-item :label="$t('suite.apiName')">
      <el-input v-model="formValue.apiName" :placeholder="$t('suite.inputApiName')" />
    </el-form-item>
    <el-form-item :label="$t('suite.apiUrl')">
      <el-input v-model="formValue.apiUrl" :placeholder="$t('suite.inputApiUrl')" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="onSubmit">{{ $t('common.search') }}</el-button>
    </el-form-item>
    <el-form-item>
      <el-button @click="onReset">{{ $t('common.reset') }}</el-button>
    </el-form-item>
  </el-form>
</template>
