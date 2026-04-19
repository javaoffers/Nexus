<script>
import { ElementType, FlowVariableType } from '../../types';
import { cloneDeep } from 'lodash-es';
import { ElMessage } from 'element-plus';
import CodeEditor from '@/components/common/CodeEditor.vue';
import { dataSourceService } from '@/service';

function getDefaultData() {
  return {
    key: '',
    name: '',
    desc: '',
    outgoings: [],
    incomings: [],
    elementType: ElementType.MYSQL,
    dataSourceId: null,
    operationType: '',
    sql: '',
    outputVariableKey: '',
  };
}

export default {
  components: {
    CodeEditor,
  },
  props: {
    data: {
      type: Object,
      required: true,
    },
  },
  emits: ['update', 'cancel'],
  data() {
    return {
      nodeData: getDefaultData(),
      dataSourceList: [],
      sqlDialogVisible: false,
    };
  },
  computed: {
    outputVariableList() {
      var flowVariables = this.$store.state.flow.flowVariables;
      return flowVariables
        .filter(function (item) { return item.variableType !== FlowVariableType.INPUT; })
        .map(function (item) { return { label: item.variableName, value: item.variableKey }; });
    },
  },
  watch: {
    data: {
      handler(val) {
        if (val !== this.nodeData) {
          this.nodeData = Object.assign(getDefaultData(), cloneDeep(val));
        }
      },
      immediate: true,
    },
  },
  created() {
    this.loadDataSourceData();
  },
  methods: {
    validate() {
      if (!this.nodeData.name) {
        ElMessage.error('节点名称不能为空');
        return false;
      }
      if (!this.nodeData.dataSourceId) {
        ElMessage.error('数据源不能为空');
        return false;
      }
      if (!this.nodeData.operationType) {
        ElMessage.error('操作类型不能为空');
        return false;
      }
      return true;
    },
    onSubmit() {
      if (!this.validate()) {
        return;
      }
      this.$emit('update', cloneDeep(this.nodeData));
    },
    onCancel() {
      this.$emit('cancel');
    },
    async loadDataSourceData() {
      var res = await dataSourceService.queryDataSourceList();
      if (res.success) {
        this.dataSourceList = res.result.map(function (item) { return { label: item.dataSourceName, value: item.id }; });
      }
    },
  },
};
</script>

<template>
  <div class="node-method-form">
    <el-form label-position="top">
      <el-form-item label="节点编码">
        <span>{{ nodeData.key }}</span>
      </el-form-item>
      <el-form-item label="节点名称" required>
        <el-input v-model="nodeData.name" maxlength="16" placeholder="请输入"></el-input>
      </el-form-item>
      <el-form-item label="节点描述">
        <el-input v-model="nodeData.desc" maxlength="60" placeholder="请输入" :rows="2" type="textarea"></el-input>
      </el-form-item>
      <el-form-item label="数据源" required>
        <el-select v-model="nodeData.dataSourceId" placeholder="请选择数据源">
          <el-option v-for="item in dataSourceList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作类型" required>
        <el-select v-model="nodeData.operationType" placeholder="请选择操作类型">
          <el-option key="change" label="更改(增/删/改)" value="change" />
          <el-option key="query" label="查询" value="query" />
        </el-select>
      </el-form-item>
      <el-form-item label="SQL语句">
        <div class="sql-btn"> <el-button @click="sqlDialogVisible = true">编辑SQL</el-button></div>
        <CodeEditor ref="codeEditRef" v-model="nodeData.sql" height="200px" language="sql" />
      </el-form-item>
      <el-form-item v-if="nodeData.operationType == 'query'" label="结果输出">
        <el-select v-model="nodeData.outputVariableKey" placeholder="请选择变量">
          <el-option v-for="item in outputVariableList" :key="item.value" :label="item.label" :value="item.value" >
            <span style="float: left">{{ item.value }}</span>
            <span style="float: right;color: var(--el-text-color-secondary);font-size: 13px;margin-left: 5px;">{{ item.label }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">确定</el-button>
        <el-button @click="onCancel">取消</el-button>
      </el-form-item>
    </el-form>
    <el-dialog v-model="sqlDialogVisible" title="SQL代码" width="1000">
      <CodeEditor ref="codeEditRef" v-model="nodeData.sql" width="960px" height="500px" :language="'sql'" />
    </el-dialog>
  </div>
</template>

<style scoped>
.sql-btn {
  padding-bottom: 5px;
  margin-left: auto;
}
</style>
