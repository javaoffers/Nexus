<script>
export default {
  emits: ['search'],
  data() {
    return {
      objectFilterFormValue: {
        objectName: '',
      },
    };
  },
  methods: {
    onSubmit() {
      this.$emit('search', this.objectFilterFormValue);
    },
    onReset() {
      this.objectFilterFormValue.objectName = '';
    },
  },
};
</script>

<template>
  <el-form :inline="true" :model="objectFilterFormValue">
    <el-form-item label="对象名称">
      <el-input v-model="objectFilterFormValue.objectName" placeholder="请输入对象名称" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="onSubmit">查询</el-button>
    </el-form-item>
    <el-form-item>
      <el-button @click="onReset">重置</el-button>
    </el-form-item>
  </el-form>
</template>
