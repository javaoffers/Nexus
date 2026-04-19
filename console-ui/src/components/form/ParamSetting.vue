<script>
import DataTypeSelect from './DataTypeSelect.vue';
import { Delete } from '@element-plus/icons-vue';

export default {
  components: {
    DataTypeSelect,
    Delete,
  },
  props: {
    modelValue: Array,
    showRequired: {
      type: Boolean,
      default: false,
    },
    showParamPosition: {
      type: Boolean,
      default: false,
    },
    paramTypeName: {
      type: String,
      default: '参数',
    },
    dataTypeClassify: String,
    addText: String,
  },
  emits: ['update:modelValue'],
  data() {
    return {
      params: [...(this.modelValue || [])],
      columns: [
        { name: this.paramTypeName + '编码', prop: 'paramKey' },
        { name: this.paramTypeName + '名称', prop: 'paramName' },
        { name: '参数位置', prop: 'paramPosition' },
        { name: '数据类型', prop: 'dataType' },
        { name: '必填', prop: 'required' },
        { name: '描述', prop: 'paramDesc' },
      ],
    };
  },
  watch: {
    modelValue(val) {
      if (val !== this.params) {
        this.params = [...val];
      }
    },
  },
  methods: {
    addParam() {
      this.params.push({
        paramKey: '',
        paramName: '',
        paramPosition: '',
        dataType: { type: 'String', itemType: null, objectKey: null, objectStructure: null },
        required: false,
        paramDesc: '',
      });
      this.onChange();
    },
    removeParam(rowIndex) {
      this.params.splice(rowIndex, 1);
      this.onChange();
    },
    onChange() {
      this.$emit('update:modelValue', this.params);
    },
  },
};
</script>

<template>
  <div class="param-setting">
    <div class="param-setting-head">
      <div class="param-setting-tr">
        <template v-for="column in columns" :key="column.prop">
          <div class="param-setting-td" v-if="column.prop === 'paramKey'">{{ column.name }}</div>
          <div class="param-setting-td" v-if="column.prop === 'paramName'">{{ column.name }}</div>
          <div class="param-setting-td" v-if="showParamPosition && column.prop === 'paramPosition'">{{ column.name }}</div>
          <div class="param-setting-td" v-if="column.prop === 'dataType'">{{ column.name }}</div>
          <div class="param-setting-td required-td" v-if="showRequired && column.prop === 'required'">{{ column.name }}</div>
          <div class="param-setting-td" v-if="column.prop === 'paramDesc'">{{ column.name }}</div>
        </template>
        <div class="param-setting-td delete-td"></div>
      </div>
    </div>
    <div class="param-setting-body">
      <div class="param-setting-tr" v-for="(param, rowIndex) in params" :key="rowIndex">
        <template v-for="column in columns" :key="column.prop">
          <div class="param-setting-td" v-if="column.prop === 'paramKey'">
            <el-input v-model="param.paramKey" size="small" @change="onChange" />
          </div>
          <div class="param-setting-td" v-if="column.prop === 'paramName'">
            <el-input v-model="param.paramName" size="small" @change="onChange" />
          </div>
          <div class="param-setting-td" v-if="showParamPosition && column.prop === 'paramPosition'">
            <el-select v-model="param.paramPosition" size="small" @change="onChange">
              <el-option label="path" value="path" />
              <el-option label="query" value="query" />
              <el-option label="body" value="body" />
            </el-select>
          </div>
          <div class="param-setting-td" v-else-if="column.prop === 'dataType'">
            <DataTypeSelect v-model="param.dataType" :type="dataTypeClassify" size="small" @change="onChange" />
          </div>
          <div class="param-setting-td required-td" v-else-if="showRequired && column.prop === 'required'">
            <el-checkbox v-model="param.required" @change="onChange" />
          </div>
          <div class="param-setting-td" v-if="column.prop === 'paramDesc'">
            <el-input v-model="param.paramDesc" size="small" @change="onChange" />
          </div>
        </template>
        <div class="param-setting-td delete-td">
          <el-icon @click="removeParam(rowIndex)"><Delete /></el-icon>
        </div>
      </div>
    </div>
    <div class="param-setting-foot">
      <el-button size="small" type="info" @click="addParam">{{ addText || '新增入参' }}</el-button>
    </div>
  </div>
</template>

<style scoped>
.param-setting {
  width: 100%;
}
.param-setting-head {
  background-color: #f2f2f2;
  padding: 0 1px;
}
.param-setting-body {
  border-left: 1px solid #f2f2f2;
  border-right: 1px solid #f2f2f2;
}
.param-setting-tr {
  display: flex;
  border-bottom: 1px solid #f2f2f2;
  height: 36px;
}
.param-setting-td {
  flex: 1;
  min-width: 0;
  padding: 0 6px;
  display: flex;
  align-items: center;
}
.param-setting-td.delete-td,
.param-setting-td.required-td {
  width: 40px;
  flex: none;
  justify-content: center;
}
.param-setting-td.delete-td {
  width: 20px;
  margin-right: 10px;
}
.param-setting-td.delete-td > .el-icon {
  cursor: pointer;
  color: #999;
}
.param-setting-foot {
  text-align: center;
  padding: 6px 0;
}
</style>
