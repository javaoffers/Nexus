<script>
import { isDataTypeMatch } from '@/utils/dataType';

export default {
  props: {
    modelValue: String,
    options: {
      type: Array,
      default: () => [],
    },
    filterDataType: Object,
    size: {
      type: String,
      default: 'default',
    },
  },
  emits: ['update:modelValue', 'change'],
  computed: {
    innerOptions() {
      const result = (this.options || []).map(option => {
        const newItem = {
          label: option.variableName,
          value: option.variableKey,
        };
        if (this.handleShowObject(option.dataType, newItem.value)) {
          return {
            ...newItem,
            children: this.getOptionChildren(option.dataType, newItem.value),
          };
        }
        return null;
      }).filter(item => item !== null);
      return result;
    },
    hasObjectOption() {
      return this.options?.some(item => item.dataType.type === 'Object');
    },
  },
  methods: {
    handleShowObject(dataType, preValue) {
      if (dataType.type == 'List' && this.filterDataType?.type == 'List'
        && dataType.itemType != this.filterDataType.itemType) {
        return false;
      }
      if (dataType.type !== 'Object') {
        return true;
      }
      const propList = this.getOptionChildren(dataType, preValue);
      if (this.filterDataType) {
        if (this.filterDataType.type == 'Object') {
          return this.filterDataType.type == dataType.type;
        } else {
          if (propList?.length == 0) {
            return false;
          }
        }
      }
      return true;
    },
    getOptionChildren(dataType, preValue, count = 0) {
      if (dataType.type === 'Object') {
        if (count > 99) {
          return;
        }
        let list = (dataType.objectStructure || []);
        if (this.filterDataType) {
          list = list.filter(item => isDataTypeMatch(item.dataType, this.filterDataType));
        }
        return list.map(item => {
          const newItem = {
            label: item.propName,
            value: preValue + '.' + item.propKey,
          };
          return {
            ...newItem,
            children: this.getOptionChildren(item.dataType, newItem.value, count + 1),
          };
        });
      }
    },
    handleChange(val) {
      this.$emit('update:modelValue', val);
      this.$emit('change', val);
    },
    getLabel(val) {
      if (!val) return '';
      const arr = val.split('.');
      let key = arr.shift();
      let parent = this.innerOptions?.find(item => item?.value === key);
      let result = parent?.label;
      while (parent && arr.length > 0) {
        key = key + '.' + arr.shift();
        parent = parent?.children?.find(item => item.value === key);
        if (parent) {
          result += '.' + parent.label;
        }
      }
      return result;
    },
  },
};
</script>

<template>
  <el-tree-select
    v-if="hasObjectOption"
    :modelValue="modelValue"
    :data="innerOptions"
    check-strictly
    :render-after-expand="false"
    :size="size"
    @change="handleChange"
  >
    <template #label="{ value }">
      <span>{{ getLabel(value) }} </span>
    </template>
  </el-tree-select>
  <el-select v-else :modelValue="modelValue" :size="size" @change="handleChange">
    <el-option
        v-for="item in innerOptions"
        :key="item?.value"
        :value="item?.value"
        :label="item?.label"
        :title="item?.label"
    >
      <span style="float: left">{{ item.value }}</span>
      <span style="float: right;color: var(--el-text-color-secondary);font-size: 13px;margin-left: 5px;">
        {{ item.label }}
      </span>
    </el-option>
  </el-select>
</template>

<style scoped></style>
