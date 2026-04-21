<script>
import { ElMessage } from 'element-plus';
import ParamSetting from '@/components/form/ParamSetting.vue';
import { objectService } from '@/service';
import ResizableDrawer from '@/components/common/ResizableDrawer.vue';

export default {
  components: {
    ParamSetting,
    ResizableDrawer,
  },
  emits: ['add', 'edit'],
  data() {
    return {
      objectDrawerVisible: false,
      editItem: undefined,
      objectFormValue: this.getDefaultObject(),
      rules: {
        objectKey: [
          { required: true, message: this.$t('object.inputObjectCode'), trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9_]+$/, message: this.$t('object.objectCodeFormat'), trigger: 'blur' },
          { validator: this.validateObjectKey, trigger: 'blur' },
        ],
        objectName: [{ required: true, message: this.$t('object.inputObjectName'), trigger: 'blur' }],
      },
    };
  },
  computed: {
    title() {
      if (this.editItem) {
        return this.$t('object.editObject');
      }
      return this.$t('object.addObject');
    },
  },
  methods: {
    getDefaultObject() {
      return {
        id: null,
        objectKey: '',
        objectName: '',
        objectDesc: '',
        props: [],
      };
    },
    validateObjectKey(rule, value, callback) {
      const param = {
        id: this.objectFormValue.id,
        objectKey: this.objectFormValue.objectKey,
      };
      objectService.isExistObjectKey(param).then((response) => {
        if (response.success && response.result) {
          callback(new Error(this.$t('object.objectKeyExists')));
        } else {
          callback();
        }
      });
    },
    onCancel() {
      this.objectDrawerVisible = false;
    },
    async onSubmit() {
      if (!this.$refs.formRef) return;
      const valid = await this.$refs.formRef.validate(() => {});
      if (!valid) {
        return;
      }
      if (!this.validateObjectProps()) {
        return;
      }

      this.objectDrawerVisible = false;
      if (this.editItem) {
        this.$emit('edit', { ...this.editItem, ...this.objectFormValue });
      } else {
        this.$emit('add', this.objectFormValue);
      }
    },
    open(item) {
      this.editItem = item;
      this.objectDrawerVisible = true;
      this.$nextTick(async () => {
        this.$refs.formRef?.resetFields();
        if (item) {
          const res = await objectService.queryObjectInfo(item.id);
          if (res.success) {
            this.objectFormValue.id = res.result.id;
            this.objectFormValue.objectKey = res.result.objectKey;
            this.objectFormValue.objectName = res.result.objectName;
            this.objectFormValue.objectDesc = res.result.objectDesc;
            const propArray = res.result.props;
            if (Array.isArray(propArray) && propArray.length !== 0) {
              const paramArray = propArray.map((item) => {
                return {
                  paramKey: item.propKey,
                  paramName: item.propName,
                  dataType: item.dataType,
                };
              });
              this.objectFormValue.props = paramArray;
            }
          }
        } else {
          Object.assign(this.objectFormValue, this.getDefaultObject());
        }
      });
    },
    validateObjectProps() {
      for (const [index, param] of this.objectFormValue.props.entries()) {
        const error = this.validateProps(param);
        if (error) {
          ElMessage.error(this.$t('object.propRowError', { index: index + 1, error: error }));
          return false;
        }
      }
      return true;
    },
    validateProps(props) {
      if (!props.paramKey) {
        return this.$t('object.propCodeRequired');
      }
      if (!/^[a-zA-Z0-9_]+$/.test(props.paramKey)) {
        return this.$t('object.propCodeFormat');
      }
      if (!props.paramName) {
        return this.$t('object.propNameRequired');
      }
      return null;
    },
  },
};
</script>

<template>
  <ResizableDrawer v-model="objectDrawerVisible" :title="title" destroyOnClose drawer-key="OBJECT">
    <div>
      <el-form ref="formRef" label-position="top" :model="objectFormValue" :rules="rules">
        <el-form-item :label="$t('object.objectCode')" prop="objectKey">
          <el-input v-model="objectFormValue.objectKey" maxlength="20" />
        </el-form-item>
        <el-form-item :label="$t('object.objectName')" prop="objectName">
          <el-input v-model="objectFormValue.objectName" maxlength="30" />
        </el-form-item>
        <el-form-item :label="$t('object.objectDesc')">
          <el-input type="textarea" v-model="objectFormValue.objectDesc" maxlength="120" />
        </el-form-item>
        <el-form-item :label="$t('object.objectProps')">
          <ParamSetting v-model="objectFormValue.props" :paramTypeName="$t('object.property')" :addText="$t('object.addProperty')" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">{{ $t('common.confirm') }}</el-button>
          <el-button @click="onCancel">{{ $t('common.cancel') }}</el-button>
        </el-form-item>
      </el-form>
    </div>
  </ResizableDrawer>
</template>
