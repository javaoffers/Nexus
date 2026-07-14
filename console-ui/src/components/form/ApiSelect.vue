<script>
import { apiService } from '@/service';

export default {
  props: ['modelValue', 'suiteCode'],
  emits: ['update:modelValue', 'change'],
  data() {
    return {
      apiList: [],
      apiLoading: false,
      currentValue: this.modelValue || '',
    };
  },
  watch: {
    modelValue: {
      handler(val) {
        this.currentValue = val || '';
      },
      immediate: true,
    },
    suiteCode: {
      handler(val) {
        if (val) {
          this.apiList = [];
          this.loadData();
        }
      },
      immediate: true,
    },
  },
  methods: {
    async loadData() {
      this.apiLoading = true;
      const res = await apiService.getApiListBySuiteCode(this.suiteCode);
      if (res.success) {
        this.apiList = res.result;
      }
      this.apiLoading = false;
    },
    onChange(val) {
      if (val === '' || val === null || val === undefined) return;
      this.$emit('update:modelValue', val);
      this.$emit('change', val);
    },
  },
};
</script>
<template>
  <el-select
    v-model="currentValue"
    :placeholder="$t('design.selectApi')"
    style="width: 100%"
    @change="onChange"
  >
    <template v-slot:empty>
      <div class="select-option-empty" v-loading="apiLoading">
        <span v-if="!apiLoading">{{ $t('design.noData') }}</span>
      </div>
    </template>
    <el-option v-for="item in apiList" :key="item.id" :label="item.apiName" :value="item.id" />
  </el-select>
</template>
<style>
.select-option-empty {
  display: flex;
  height: 120px;
  align-items: center;
  justify-content: center;
}
</style>