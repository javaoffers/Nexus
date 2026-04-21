<script>
import { dataSourceService } from '@/service';
import ResizableDrawer from '@/components/common/ResizableDrawer.vue';

export default {
  components: {
    ResizableDrawer,
  },
  emits: ['add', 'edit'],
  data() {
    return {
      dialogVisible: false,
      editItem: undefined,
      formValue: this.getDefault(),
      rules: {
        dataSourceName: [{ required: true, message: this.$t('system.inputDataSourceName'), trigger: 'blur' }],
        dataSourceType: [{ required: true, message: this.$t('system.selectDataSourceType'), trigger: 'blur' }],
        address: [{ required: true, message: this.$t('system.inputConnAddress'), trigger: 'blur' }],
        port: [
          { required: true, message: this.$t('system.inputPort'), trigger: 'blur' },
          { pattern: /^[0-9_]+$/, message: this.$t('system.inputNumber'), trigger: 'blur' },
        ],
        userName: [{ required: true, message: this.$t('system.inputAccount'), trigger: 'blur' }],
        password: [{ required: true, message: this.$t('system.inputPassword'), trigger: 'blur' }],
        databaseName: [{ required: true, message: this.$t('system.inputDbName'), trigger: 'blur' }],
      },
    };
  },
  computed: {
    title() {
      if (this.editItem) {
        return this.$t('system.editDataSource');
      }
      return this.$t('system.addDataSource');
    },
  },
  methods: {
    getDefault() {
      return {
        id: null,
        dataSourceName: '',
        dataSourceType: '',
        dataSourceDesc: '',
        address: '',
        port: '',
        userName: '',
        password: '',
        databaseName: '',
        connectExtInfo: '',
        minPoolSize: 5,
        maxPoolSize: 5,
        queryTimeout: 30,
      };
    },
    onCancel() {
      this.dialogVisible = false;
    },
    async onSubmit() {
      if (!this.$refs.dataSourceFormRef) return;
      const valid = await this.$refs.dataSourceFormRef.validate(() => {});
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
      this.$nextTick(async () => {
        this.$refs.dataSourceFormRef?.resetFields();
        if (item) {
          const res = await dataSourceService.queryDataSourceInfo(item.id);
          if (res.success) {
            this.formValue.id = res.result.id;
            this.formValue.dataSourceName = res.result.dataSourceName;
            this.formValue.dataSourceType = res.result.dataSourceType;
            this.formValue.dataSourceDesc = res.result.dataSourceDesc;
            this.formValue.address = res.result.address;
            this.formValue.port = res.result.port;
            this.formValue.userName = res.result.userName;
            this.formValue.password = res.result.password;
            this.formValue.databaseName = res.result.databaseName;
            this.formValue.connectExtInfo = res.result.connectExtInfo;
            this.formValue.minPoolSize = res.result.minPoolSize;
            this.formValue.maxPoolSize = res.result.maxPoolSize;
            this.formValue.queryTimeout = res.result.queryTimeout;
          }
        } else {
          Object.assign(this.formValue, this.getDefault());
        }
      });
    },
  },
};
</script>
<template>
  <ResizableDrawer v-model="dialogVisible" :size="480" :title="title" destroyOnClose drawer-key="DATA_SOURCE">
    <div class="form">
      <el-form ref="dataSourceFormRef" label-position="top" :model="formValue" :rules="rules">
        <el-form-item :label="$t('system.dataSourceName')" prop="dataSourceName">
          <el-input v-model="formValue.dataSourceName" maxlength="30" />
        </el-form-item>
        <el-form-item :label="$t('system.dataSourceType')" prop="dataSourceType">
          <el-select v-model="formValue.dataSourceType">
            <el-option value="MYSQL" key="MYSQL">MySql</el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('common.description')" prop="dataSourceDesc">
          <el-input v-model="formValue.dataSourceDesc" type="textarea" :rows="2" maxlength="120" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="18">
            <el-form-item :label="$t('system.connAddress')" prop="address">
              <el-input v-model="formValue.address" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item :label="$t('system.port')" prop="port">
              <el-input v-model="formValue.port" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="$t('system.account')" prop="userName">
              <el-input v-model="formValue.userName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('system.password')" prop="password">
              <el-input type="password" v-model="formValue.password" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item :label="$t('system.dbName')" prop="databaseName">
          <el-input v-model="formValue.databaseName" />
        </el-form-item>
        <el-form-item :label="$t('system.connExtInfo')" prop="connectExtInfo">
          <el-input v-model="formValue.connectExtInfo" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="$t('system.minConn')" prop="minPoolSize">
              <el-input-number v-model="formValue.minPoolSize" controls-position="right" :min="1" :max="2000" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('system.maxConn')" prop="maxPoolSize">
              <el-input-number v-model="formValue.maxPoolSize" class="mx-4" controls-position="right" :min="1" :max="2000"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item :label="$t('system.queryTimeout')" prop="queryTimeout">
          <el-input-number v-model="formValue.queryTimeout" controls-position="right" :min="1" :max="600" style="width: 100%">
            <template #suffix>
              <span>{{ $t('system.second') }}</span>
            </template>
          </el-input-number>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">{{ $t('common.confirm') }}</el-button>
          <el-button @click="onCancel">{{ $t('common.cancel') }}</el-button>
        </el-form-item>
      </el-form>
    </div>
  </ResizableDrawer>
</template>

<style scoped>
.dialog-footer button:first-child {
  margin-right: 10px;
}
</style>
