<template>
  <div ref="monacoEditorRef" :style="monacoEditorStyle"></div>
</template>
<script>
import { codeEditorMixin } from '@/mixins/codeEditor';

export default {
  mixins: [codeEditorMixin],
  props: {
    modelValue: String,
    width: {
      type: String,
      default: '100%',
    },
    height: {
      type: String,
      default: '100%',
    },
    language: {
      type: String,
      default: 'javascript',
    },
    editorOption: {
      type: Object,
      default: () => ({}),
    },
  },
  emits: ['blue', 'update:modelValue'],
  computed: {
    monacoEditorStyle() {
      return {
        width: this.width,
        height: this.height,
      };
    },
  },
  watch: {
    modelValue() {
      this.updateMonacoVal(this.modelValue);
    },
  },
  mounted() {
    const monacoEditor = this.createEditor(this.editorOption);
    this.updateMonacoVal(this.modelValue);
    monacoEditor?.onDidChangeModelContent(() => {
      this.$emit('update:modelValue', monacoEditor.getValue());
    });
    monacoEditor?.onDidBlurEditorText(() => {
      this.$emit('blue');
    });
  },
  methods: {
    updateMonacoVal(val) {
      if (val !== this.getEditor()?.getValue()) {
        this.updateVal(val);
      }
    },
  },
};
</script>
<style scoped></style>
