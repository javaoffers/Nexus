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
    };
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
      this.$emit('update:modelValue', val);
      this.$emit('change', val);
    },
  },
};
</script>
<template>
  <el-select
    :modelValue="modelValue"
    placeholder="请选择套件"
    style="width: 100%"
    filterable
    @visibleChange="onVisibleChange"
    @change="onChange"
  >
    <template v-slot:empty>
      <div class="select-option-empty" v-loading="suiteLoading">
        <span v-if="!suiteLoading">无数据</span>
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
