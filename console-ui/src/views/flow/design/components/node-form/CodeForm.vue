<script>
import { ElementType } from '../../types';
import { cloneDeep } from 'lodash-es';
import { ElMessage } from 'element-plus';
import CodeEditor from '@/components/common/CodeEditor.vue';
import i18n from '@/i18n';

function getGroovyDemoCode() {
  var t = i18n.global.t;
  return "// " + t('design.getVar') + "\n" +
    "// const variable_name = $var.getVariableValue('" + t('design.varKeyPlaceholder') + "');\n" +
    "// " + t('design.setVar') + "\n" +
    "// $var.setVariableValue('" + t('design.varKeyPlaceholder') + "'," + t('design.valuePlaceholder') + ");\n" +
    "// " + t('design.printVar') + "\n" +
    "// println(variable_name);\n";
}

function getDefaultData() {
  return {
    key: '',
    name: '',
    desc: '',
    outgoings: [],
    incomings: [],
    elementType: ElementType.CODE,
    language: 'groovy',
    content: getGroovyDemoCode(),
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
        ElMessage.error(this.$t('design.nodeNameRequired'));
        return false;
      }
      return true;
    },
    changeLanguageType(languageType) {
      var t = i18n.global.t;
      if ('groovy' === languageType) {
        this.nodeData.content = getGroovyDemoCode();
      } else if ('javascript' === languageType) {
        this.nodeData.content =
          "// " + t('design.getVar') + "\n" +
          "// const variable_name = $var.getVariableValue('" + t('design.varKeyPlaceholder') + "');\n" +
          "// " + t('design.setVar') + "\n" +
          "// $var.setVariableValue('" + t('design.varKeyPlaceholder') + "'," + t('design.valuePlaceholder') + ");\n";
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
      <el-form-item :label="$t('design.nodeCode')">
        <span>{{ nodeData.key }}</span>
      </el-form-item>
      <el-form-item :label="$t('design.nodeName')">
        <el-input v-model="nodeData.name" maxlength="16" :placeholder="$t('common.pleaseInput')"></el-input>
      </el-form-item>
      <el-form-item :label="$t('design.nodeDesc')">
        <el-input v-model="nodeData.desc" maxlength="60" :placeholder="$t('common.pleaseInput')" :rows="2" type="textarea"></el-input>
      </el-form-item>
      <el-form-item :label="$t('design.scriptLang')">
        <el-select v-model="nodeData.language" :placeholder="$t('design.selectScriptLang')" @change="changeLanguageType">
          <el-option key="groovy" label="Groovy" value="groovy" />
          <el-option key="javascript" label="JavaScript" value="javascript" />
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('design.scriptCode')">
        <div class="code-btn"> <el-button @click="codeDialogVisible = true">{{ $t('design.editCode') }}</el-button></div>
        <CodeEditor ref="codeEditRef" v-model="nodeData.content" height="200px" :language="nodeData.language" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">{{ $t('common.confirm') }}</el-button>
        <el-button @click="onCancel">{{ $t('common.cancel') }}</el-button>
      </el-form-item>
    </el-form>
    <el-dialog v-model="codeDialogVisible" :title="$t('design.scriptCode')" width="1000">
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
