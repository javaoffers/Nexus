<script>
import { apiService } from '@/service';

export default {
  props: ['modelValue', 'suiteCode'],
  emits: ['update:modelValue', 'change'],
  data() {
    return {
      apiList: [],
      apiLoading: false,
    };
  },
  watch: {
    suiteCode: {
      handler(val) {
        if (val) {
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
    onChange(apiCode) {
      this.$emit('update:modelValue', apiCode);
      this.$emit('change', apiCode);
    },
  },
};
</script>
<template>
  <el-select :modelValue="modelValue" :placeholder="$t('design.selectApi')" style="width: 100%" filterable @change="onChange">
    <template v-slot:empty>
      <div class="select-option-empty" v-loading="apiLoading">
        <span v-if="!apiLoading">{{ $t('design.noData') }}</span>
      </div>
    </template>
    <el-option v-for="item in apiList" :key="item.apiCode" :label="item.apiName" :value="item.apiCode" />
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
