<script>
import { commonService } from '@/service';
import { DataTypeEnum } from '@/typings';

export default {
  props: {
    modelValue: Object,
    type: String,
    disabled: Boolean,
    size: {
      type: String,
      default: 'default',
    },
  },
  emits: ['update:modelValue', 'change'],
  data() {
    return {
      dataTypeList: [],
      cascaderProps: {
        expandTrigger: 'hover',
      },
    };
  },
  computed: {
    options() {
      const basicTypeList = this.dataTypeList.filter(item => item.dataTypeClassify === DataTypeEnum.Basic);
      const objectTypeList = this.dataTypeList.filter(item => item.dataTypeClassify === DataTypeEnum.Object);
      const basicNode = {
        value: 'Basic',
        label: '基础类型',
        children: basicTypeList.map(item => ({ value: item.type, label: item.displayName })),
      };
      const objectNode = {
        value: 'Object',
        label: '对象类型',
        children: objectTypeList.map(item => ({ value: item.objectKey, label: item.displayName })),
      };
      const listNode = {
        value: 'List',
        label: '列表类型',
        children: [basicNode, objectNode].map(node => {
          return {
            ...node,
            children: node.children.map(child => {
              return {
                ...child,
                label: '[ ' + child.label + ' ]',
              };
            }),
          };
        }),
      };
      return [basicNode, listNode, objectNode];
    },
    modelValueObj() {
      let val = null;
      if (this.modelValue) {
        val = { ...this.modelValue };
      }
      return val;
    },
    innerValue() {
      let val = this.modelValueObj;
      let result = [];
      if (!val) {
        return result;
      }
      result = this.typeToArray(val.type, val);
      return result;
    },
    innerBasicValue() {
      if (this.innerValue[0] === 'Basic') {
        return this.innerValue[1];
      }
      return null;
    },
  },
  created() {
    this.loadData();
  },
  methods: {
    async loadData() {
      const res = await commonService.dataType.getList();
      this.dataTypeList = res || [];
    },
    typeToArray(type, dataType) {
      const item = this.dataTypeList.find(item => item.type === type);
      if (!item) {
        return [];
      }
      if (item.dataTypeClassify === DataTypeEnum.Basic) {
        return ['Basic', type];
      } else if (item.dataTypeClassify === DataTypeEnum.Object) {
        return ['Object', dataType.objectKey];
      } else if (item.dataTypeClassify === DataTypeEnum.List) {
        return ['List', ...this.typeToArray(dataType.itemType, dataType)];
      }
      return [];
    },
    arrayToType(arr) {
      if (!Array.isArray(arr) || arr.length === 0) {
        return null;
      }
      const result = {
        type: '',
        itemType: null,
        objectKey: null,
        objectStructure: null,
      };
      if (arr[0] === 'Basic') {
        result.type = arr[1];
      } else if (arr[0] === 'Object') {
        result.type = 'Object';
        result.objectKey = arr[1];
      } else if (arr[0] === 'List') {
        result.type = 'List';
        if (arr[1] === 'Basic') {
          result.itemType = arr[2];
        } else if (arr[1] === 'Object') {
          result.itemType = 'Object';
          result.objectKey = arr[2];
        }
      }
      if (result.objectKey) {
        const item = this.dataTypeList.find(item => item.objectKey === result.objectKey);
        if (item) {
          result.objectStructure = item.objectStructure;
        }
      }
      return result;
    },
    handleChange(val) {
      if (!Array.isArray(val)) {
        this.$emit('update:modelValue', null);
        this.$emit('change', null);
        return;
      }
      const result = this.arrayToType(val);
      this.$emit('update:modelValue', result);
      this.$emit('change', result);
    },
    handleBasicChange(val) {
      if (!val) {
        this.$emit('update:modelValue', null);
        this.$emit('change', null);
        return;
      }
      let result = {
        type: val,
        itemType: null,
        objectKey: null,
        objectStructure: null,
      };
      this.$emit('update:modelValue', result);
      this.$emit('change', result);
    },
  },
};
</script>

<template>
  <el-select :size="size" v-if="type === 'basic'" :modelValue="innerBasicValue" :disabled="disabled" @change="handleBasicChange">
    <el-option v-for="item in options[0]?.children" :key="item.value" :label="item.label" :value="item.value" />
  </el-select>
  <el-cascader
    v-else
    :modelValue="innerValue"
    :options="options"
    :props="cascaderProps"
    :disabled="disabled"
    :show-all-levels="false"
    :size="size"
    style="width: 100%"
    @change="handleChange"
    :filterable="true"
  />
</template>

<style scoped></style>
