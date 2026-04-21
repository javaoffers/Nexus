<script>
export default {
  emits: ['search'],
  data() {
    return {
      formValue: {
        suiteName: '',
        suiteFlag: null,
      },
    };
  },
  methods: {
    onSubmit() {
      this.$emit('search', this.formValue);
    },
    onReset() {
      this.formValue.suiteName = '';
      this.formValue.suiteFlag = null;
    },
  },
};
</script>
<template>
  <el-form :inline="true" :model="formValue">
    <el-form-item :label="$t('suite.suiteName')">
      <el-input v-model="formValue.suiteName" :placeholder="$t('suite.inputSuiteName')" />
    </el-form-item>
    <el-form-item :label="$t('suite.suiteType')">
      <el-select :placeholder="$t('common.pleaseSelect')" v-model="formValue.suiteFlag" clearable style="width: 200px">
        <el-option :label="$t('suite.personal')" :value="2" />
        <el-option :label="$t('suite.official')" :value="1" />
        <el-option :label="$t('suite.builtin')" :value="0" />
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
