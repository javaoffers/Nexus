<script>
import { Plus } from '@element-plus/icons-vue';
import FilterItem from './FilterItem.vue';

export default {
  components: {
    FilterItem,
    Plus,
  },
  props: ['list', 'sourceList', 'targetList'],
  emits: ['change'],
  methods: {
    onDelete(index) {
      const result = [...this.list];
      result.splice(index, 1);
      this.$emit('change', result);
    },
    onChange(item, index) {
      const result = [...this.list];
      result[index] = item;
      this.$emit('change', result);
    },
    onAdd() {
      const result = [...this.list];
      result.push({});
      this.$emit('change', result);
    },
  },
};
</script>

<template>
  <div class="filter-list">
    <FilterItem
      v-for="(item, index) in list"
      :key="index"
      :item="item"
      :sourceList="sourceList"
      :targetList="targetList"
      @delete="onDelete(index)"
      @change="onChange($event, index)"
    />
    <el-button @click="onAdd"
      ><el-icon><Plus /></el-icon>{{ $t('filter.andCondition') }}</el-button
    >
  </div>
</template>

<style scoped>
.filter-list {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 10px;
}
</style>
