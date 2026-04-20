import * as monaco from 'monaco-editor';
import { nextTick, markRaw } from 'vue';

/**
 * Mixin that wraps a Monaco code editor instance.
 *
 * The consuming component MUST:
 *   1. Include a template ref named "monacoEditorRef" on the target DOM element:
 *        <div ref="monacoEditorRef"></div>
 *   2. Optionally provide a `language` prop (defaults to 'javascript').
 *
 * Data provided:   _monacoEditor (the editor instance, initially null)
 * Methods:         createEditor, formatDoc, updateVal, updateOptions, getOption, getEditor
 */
export const codeEditorMixin = {
  data() {
    return {
      _monacoEditor: null,
    };
  },

  methods: {
    /**
     * Create a Monaco editor inside the element referenced by "monacoEditorRef".
     * @param {object} editorOption  Extra options merged into the defaults.
     * @returns {object|undefined}   The Monaco editor instance.
     */
    createEditor(editorOption = {}) {
      const el = this.$refs.monacoEditorRef;
      if (!el) return;

      const language = this.language || 'javascript';

      this._monacoEditor = markRaw(monaco.editor.create(el, {
        model: monaco.editor.createModel('', language),
        minimap: { enabled: false },
        roundedSelection: true,
        theme: 'vs',
        multiCursorModifier: 'ctrlCmd',
        scrollbar: {
          verticalScrollbarSize: 8,
          horizontalScrollbarSize: 8,
        },
        lineNumbers: 'on',
        tabSize: 2,
        fontSize: 16,
        autoIndent: 'advanced',
        automaticLayout: true,
        ...editorOption,
      }));

      return this._monacoEditor;
    },

    /** Run the built-in "format document" action. */
    async formatDoc() {
      await this._monacoEditor?.getAction('editor.action.formatDocument')?.run();
    },

    /**
     * Set the editor's value, ensuring readOnly is temporarily lifted if needed,
     * and auto-format afterwards.
     * @param {*} val
     */
    updateVal(val) {
      nextTick(() => {
        if (this.getOption(monaco.editor.EditorOption.readOnly)) {
          this.updateOptions({ readOnly: false });
        }
        this._monacoEditor?.setValue(val.toString());
        setTimeout(async () => {
          await this.formatDoc();
        }, 10);
      });
    },

    /**
     * Update editor options at runtime.
     * @param {object} opt
     */
    updateOptions(opt) {
      this._monacoEditor?.updateOptions(opt);
    },

    /**
     * Read a single editor option by its enum key.
     * @param {number} name  A value from monaco.editor.EditorOption.
     * @returns {*}
     */
    getOption(name) {
      return this._monacoEditor?.getOption(name);
    },

    /**
     * Return the raw Monaco editor instance.
     * @returns {object|null}
     */
    getEditor() {
      return this._monacoEditor;
    },
  },

  beforeUnmount() {
    if (this._monacoEditor) {
      this._monacoEditor.dispose();
      this._monacoEditor = null;
    }
  },
};
