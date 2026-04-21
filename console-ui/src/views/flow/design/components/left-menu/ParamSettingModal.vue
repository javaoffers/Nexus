<script>
import DataTypeSelect from '@/components/form/DataTypeSelect.vue';

export default {
  components: {
    DataTypeSelect,
  },
  emits: ['add', 'edit'],
  data() {
    var self = this;
    return {
      visible: false,
      isEdit: false,
      _originalData: null,
      form: {
        variableKey: '',
        variableName: '',
        dataType: null,
        variableType: 3,
        id: 0,
      },
      rules: {
        variableKey: [
          { required: true, message: self.$t('design.inputVarKey'), trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9_]+$/, message: self.$t('design.varKeyFormat'), trigger: 'blur' },
          {
            validator: function (_, value, callback) {
              if (self._originalData && value === self._originalData.variableKey) {
                callback();
                return;
              }
              var item = self.$store.state.flow.flowVariables.find(function (item) { return item.variableKey === value; });
              if (item) {
                callback(new Error(self.$t('design.varKeyExists')));
                return;
              }
              callback();
            },
            trigger: 'blur',
          },
        ],
        variableName: [{ required: true, message: self.$t('design.inputVarName'), trigger: 'blur' }],
        dataType: [{ required: true, message: self.$t('design.selectDataType'), trigger: 'blur' }],
      },
    };
  },
  methods: {
    add(maxId) {
      this.isEdit = false;
      this.form.variableKey = '';
      this.form.variableName = '';
      this.form.dataType = null;
      this.form.variableType = 3;
      this.form.id = maxId;
      this._originalData = Object.assign({}, this.form);
      this.visible = true;
    },
    edit(data) {
      this.isEdit = true;
      Object.assign(this.form, data);
      this._originalData = Object.assign({}, this.form);
      this.visible = true;
    },
    onCancel() {
      this.visible = false;
    },
    async onSubmit() {
      if (!this.$refs.formRef) return;
      var valid = await this.$refs.formRef.validate().catch(function () { return false; });
      if (!valid) {
        return;
      }
      this.visible = false;
      var result = Object.assign({}, this.form);
      if (this.isEdit) {
        this.$emit('edit', result);
      } else {
        this.$emit('add', result);
      }
    },
  },
};
</script>

<template>
  <el-dialog :title="isEdit ? $t('design.editVar') : $t('design.addTempVar')" v-model="visible" append-to-body :width="400">
    <el-form labelPosition="top" ref="formRef" :model="form" :rules="rules">
      <el-form-item :label="$t('design.varKey')" prop="variableKey">
        <el-input v-model="form.variableKey" :placeholder="$t('common.pleaseInput')" maxlength="30" />
      </el-form-item>
      <el-form-item :label="$t('design.varName')" prop="variableName">
        <el-input v-model="form.variableName" :placeholder="$t('common.pleaseInput')" maxlength="30" />
      </el-form-item>
      <el-form-item :label="$t('design.varType')" prop="dataType">
        <DataTypeSelect v-model="form.dataType" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="onCancel">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="onSubmit">{{ $t('common.confirm') }}</el-button>
      </span>
    </template>
  </el-dialog>
</template>
