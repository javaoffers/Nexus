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
    <el-form-item :label="$t('object.objectName')">
      <el-input v-model="objectFilterFormValue.objectName" :placeholder="$t('object.inputObjectName')" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="onSubmit">{{ $t('common.search') }}</el-button>
    </el-form-item>
    <el-form-item>
      <el-button @click="onReset">{{ $t('common.reset') }}</el-button>
    </el-form-item>
  </el-form>
</template>
