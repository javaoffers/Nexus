<script>
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';

export default {
  components: {
    Plus,
  },
  emits: ['add', 'edit'],
  data() {
    return {
      dialogVisible: false,
      suiteFlag: undefined,
      editItem: undefined,
      formValue: {
        suiteImage: '',
        suiteCode: '',
        suiteName: '',
        suiteDesc: '',
      },
      rules: {
        suiteCode: [
          { required: true, message: '请输入套件编码', trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9_]+$/, message: '请输入大小写字母数字或下划线', trigger: 'blur' },
        ],
        suiteName: [{ required: true, message: '请输入套件名称', trigger: 'blur' }],
      },
    };
  },
  computed: {
    isEditMode() {
      return !!this.editItem;
    },
    title() {
      if (this.editItem) {
        return '编辑套件';
      }
      return '新增套件';
    },
  },
  methods: {
    onCancel() {
      this.dialogVisible = false;
    },
    async onSubmit() {
      if (!this.$refs.formRef) return;
      const valid = await this.$refs.formRef.validate(() => {});
      if (!valid) {
        return;
      }

      this.dialogVisible = false;
      if (this.editItem) {
        this.$emit('edit', { ...this.editItem, ...this.formValue });
      } else {
        this.$emit('add', this.formValue);
      }
    },
    open(item) {
      this.editItem = item;
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.$refs.formRef?.resetFields();
        this.suiteFlag = null;
        this.formValue.suiteImage = '';
        if (item) {
          this.suiteFlag = item.suiteFlag;
          this.formValue.suiteImage = item.suiteImage;
          this.formValue.suiteCode = item.suiteCode;
          this.formValue.suiteName = item.suiteName;
          this.formValue.suiteDesc = item.suiteDesc;
        }
      });
    },
    beforeSuiteImageUpload(file) {
      const isJPG = file.raw.type === 'image/jpeg';
      const isPNG = file.raw.type === 'image/png';
      const isLt30K = file.size / 1024 < 30;
      if (!isJPG && !isPNG) {
        ElMessage.error('只能上传 JPG/PNG 格式的图片');
        return false;
      }
      if (!isLt30K) {
        ElMessage.error('图片大小不能超过30KB');
        return false;
      }
      this.convertImageToBase64(file);
      return false;
    },
    convertImageToBase64(file) {
      const reader = new FileReader();
      reader.readAsDataURL(file.raw);
      reader.onload = () => {
        const base64Data = reader.result;
        this.formValue.suiteImage = base64Data;
      };
    },
  },
};
</script>
<template>
  <el-dialog v-model="dialogVisible" :title="title" width="600">
    <div class="form">
      <el-form ref="formRef" label-position="top" :model="formValue" :rules="rules">
        <el-form-item label="套件图像">
          <el-upload class="suite-image-uploader" :show-file-list="false" :on-change="beforeSuiteImageUpload" :auto-upload="false">
            <img v-if="formValue.suiteImage" :src="formValue.suiteImage" class="avatar" alt="" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="套件编码" prop="suiteCode">
          <el-input v-model="formValue.suiteCode" maxlength="20" :disabled="isEditMode"/>
        </el-form-item>
        <el-form-item label="套件名称" prop="suiteName">
          <el-input v-model="formValue.suiteName" maxlength="30" />
        </el-form-item>
        <el-form-item label="套件描述" prop="suiteDesc">
          <el-input v-model="formValue.suiteDesc" type="textarea" :rows="3" maxlength="120" />
        </el-form-item>
      </el-form>
    </div>
    <template #footer v-if="suiteFlag !== 1">
      <span class="dialog-footer">
        <el-button @click="onCancel">取消</el-button>
        <el-button type="primary" @click="onSubmit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style>
.suite-image-uploader .avatar {
  width: 90px;
  height: 90px;
  display: block;
}

.suite-image-uploader .el-upload {
  border: 1px dashed #4c4d4f;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.suite-image-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 90px;
  height: 90px;
  text-align: center;
}
</style>
