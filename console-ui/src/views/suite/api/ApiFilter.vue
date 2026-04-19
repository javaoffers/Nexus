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
    <el-form-item label="接口名称">
      <el-input v-model="formValue.apiName" placeholder="请输入接口名称" />
    </el-form-item>
    <el-form-item label="接口地址">
      <el-input v-model="formValue.apiUrl" placeholder="请输入接口地址" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="onSubmit">查询</el-button>
    </el-form-item>
    <el-form-item>
      <el-button @click="onReset">重置</el-button>
    </el-form-item>
  </el-form>
</template>
