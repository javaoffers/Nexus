<script>
import { suiteService } from '@/service';

export default {
  props: ['modelValue', 'auto'],
  emits: ['update:modelValue', 'change'],
  data() {
    return {
      suiteList: [],
      suiteLoading: false,
      suiteLoaded: false,
      currentValue: this.modelValue,
    };
  },
  watch: {
    modelValue(val) {
      this.currentValue = val;
    },
  },
  created() {
    if (this.auto) {
      this.loadData();
    }
  },
  methods: {
    async loadData() {
      this.suiteLoading = true;
      const res = await suiteService.querySuiteList();
      if (res.success) {
        this.suiteList = res.result.map((item) => ({ label: item.suiteName, value: item.id }));
      }
      this.suiteLoaded = true;
      this.suiteLoading = false;
    },
    onVisibleChange(val) {
      if (this.suiteLoaded) {
        return;
      }
      if (val) {
        this.loadData();
      }
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
    :placeholder="$t('design.selectSuite')"
    style="width: 100%"
    filterable
    @visibleChange="onVisibleChange"
    @change="onChange"
  >
    <template v-slot:empty>
      <div class="select-option-empty" v-loading="suiteLoading">
        <span v-if="!suiteLoading">{{ $t('design.noData') }}</span>
      </div>
    </template>
    <el-option v-for="item in suiteList" :key="item.value" :label="item.label" :value="item.value" />
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