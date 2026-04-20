<script>
import { Plus } from '@element-plus/icons-vue';
import FilterList from './FilterList.vue';

export default {
  components: {
    FilterList,
    Plus,
  },
  props: ['value', 'sourceList', 'targetList'],
  emits: ['change'],
  methods: {
    onChange(item, index) {
      let result = [...(this.value || [])];
      result[index] = item;
      result = result.filter((item, index) => index === 0 || item.length > 0);
      if (result.length === 0) {
        result = [[]];
      }
      this.$emit('change', result);
    },
    onAdd() {
      const result = [...(this.value || [])];
      result.push([{}]);
      this.$emit('change', result);
    },
  },
};
</script>

<template>
  <div class="filter-condition">
    <FilterList
      v-for="(list, index) in (value || [])"
      :key="index"
      :list="list"
      :sourceList="sourceList"
      :targetList="targetList"
      @change="onChange($event, index)"
    />
    <el-button @click="onAdd"
      ><el-icon><Plus /></el-icon>或条件</el-button
    >
  </div>
</template>
