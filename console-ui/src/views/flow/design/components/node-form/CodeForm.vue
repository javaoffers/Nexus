<script>
import { ElementType } from '../../types';
import { cloneDeep } from 'lodash-es';
import { ElMessage } from 'element-plus';
import CodeEditor from '@/components/common/CodeEditor.vue';

var groovyDemoCode =
  "// 获取变量\n" +
  "// const variable_name = $var.getVariableValue('变量key');\n" +
  "// 设置变量\n" +
  "// $var.setVariableValue('变量key',值);\n" +
  "// 打印变量\n" +
  "// println(variable_name);\n";

function getDefaultData() {
  return {
    key: '',
    name: '',
    desc: '',
    outgoings: [],
    incomings: [],
    elementType: ElementType.CODE,
    language: 'groovy',
    content: groovyDemoCode,
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
      codeDialogVisible: false,
    };
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
  methods: {
    validate() {
      if (!this.nodeData.name) {
        ElMessage.error('节点名称不能为空');
        return false;
      }
      return true;
    },
    changeLanguageType(languageType) {
      if ('groovy' === languageType) {
        this.nodeData.content = groovyDemoCode;
      } else if ('javascript' === languageType) {
        this.nodeData.content =
          "// 获取变量\n" +
          "// const variable_name = $var.getVariableValue('变量key');\n" +
          "// 设置变量\n" +
          "// $var.setVariableValue('变量key',值);\n";
      }
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
  },
};
</script>

<template>
  <div class="node-method-form">
    <el-form label-position="top">
      <el-form-item label="节点编码">
        <span>{{ nodeData.key }}</span>
      </el-form-item>
      <el-form-item label="节点名称">
        <el-input v-model="nodeData.name" maxlength="16" placeholder="请输入"></el-input>
      </el-form-item>
      <el-form-item label="节点描述">
        <el-input v-model="nodeData.desc" maxlength="60" placeholder="请输入" :rows="2" type="textarea"></el-input>
      </el-form-item>
      <el-form-item label="脚本语言">
        <el-select v-model="nodeData.language" placeholder="请选择脚本语言" @change="changeLanguageType">
          <el-option key="groovy" label="Groovy" value="groovy" />
          <el-option key="javascript" label="JavaScript" value="javascript" />
        </el-select>
      </el-form-item>
      <el-form-item label="脚本代码">
        <div class="code-btn"> <el-button @click="codeDialogVisible = true">编辑代码</el-button></div>
        <CodeEditor ref="codeEditRef" v-model="nodeData.content" height="200px" :language="nodeData.language" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">确定</el-button>
        <el-button @click="onCancel">取消</el-button>
      </el-form-item>
    </el-form>
    <el-dialog v-model="codeDialogVisible" title="脚本代码" width="1000">
      <CodeEditor ref="codeEditRef" v-model="nodeData.content" width="960px" height="500px" :language="nodeData.language" />
    </el-dialog>
  </div>
</template>

<style scoped>
.code-btn {
  padding-bottom: 5px;
  margin-left: auto;
}
</style>
