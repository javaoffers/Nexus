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
          { required: true, message: '请输入对象编码', trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9_]+$/, message: '请输入大小写字母、下划线和数字', trigger: 'blur' },
          { validator: this.validateObjectKey, trigger: 'blur' },
        ],
        objectName: [{ required: true, message: '请输入对象名称', trigger: 'blur' }],
      },
    };
  },
  computed: {
    title() {
      if (this.editItem) {
        return '编辑对象';
      }
      return '新增对象';
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
          callback(new Error('对象key已经存在'));
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
          ElMessage.error(`对象属性的第${index + 1}行: ${error}`);
          return false;
        }
      }
      return true;
    },
    validateProps(props) {
      if (!props.paramKey) {
        return '属性编码不能为空';
      }
      if (!/^[a-zA-Z0-9_]+$/.test(props.paramKey)) {
        return '属性编码只能包含大小写字母数字或下划线';
      }
      if (!props.paramName) {
        return '属性名称不能为空';
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
        <el-form-item label="对象编码" prop="objectKey">
          <el-input v-model="objectFormValue.objectKey" maxlength="20" />
        </el-form-item>
        <el-form-item label="对象名称" prop="objectName">
          <el-input v-model="objectFormValue.objectName" maxlength="30" />
        </el-form-item>
        <el-form-item label="对象描述">
          <el-input type="textarea" v-model="objectFormValue.objectDesc" maxlength="120" />
        </el-form-item>
        <el-form-item label="对象属性">
          <ParamSetting v-model="objectFormValue.props" :paramTypeName="'属性'" addText="新增属性" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">确定</el-button>
          <el-button @click="onCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </ResizableDrawer>
</template>
