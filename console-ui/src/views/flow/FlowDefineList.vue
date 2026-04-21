<script>
import { FlowDefineTable, FlowDefineDrawer, FlowDefineFilter } from './define';
import { flowDefineService, flowVersionService } from '@/service';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';

export default {
  components: {
    FlowDefineTable,
    FlowDefineDrawer,
    FlowDefineFilter,
    Plus,
  },
  data() {
    return {
      pageNum: 1,
      pageSize: 10,
      dataTotal: 0,
      dataRows: [],
      loading: false,
      filter: {},
      deployFormVisible: false,
      deployForm: {
        flowDefinitionId: '',
        flowName: '',
        flowDeployVersion: '',
        flowVersionRemark: '',
      },
    };
  },
  created() {
    this.queryFlowDefinePage();
  },
  methods: {
    onSearch(param) {
      this.filter = param;
      this.onPageChange(1);
    },
    async queryFlowDefinePage() {
      this.loading = true;
      const res = await flowDefineService.queryFlowDefinePage({
        pageSize: this.pageSize,
        pageNum: this.pageNum,
        ...this.filter,
      });
      if (res.success) {
        this.dataTotal = res.total;
        this.dataRows = res.result;
      }
      this.loading = false;
    },
    onPageChange(page) {
      this.pageNum = page;
      this.queryFlowDefinePage();
    },
    openflowDefineAdd() {
      this.$refs.drawerRef.open();
    },
    async addFlowDefineItem(row) {
      const res = await flowDefineService.addDefineInfo(row);
      if (res.result) {
        ElMessage({ type: 'success', message: this.$t('common.createSuccess') });
        await this.queryFlowDefinePage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    async updateFlowDefineItem(row) {
      const res = await flowDefineService.updateDefineInfo(row);
      if (res.result) {
        ElMessage({ type: 'success', message: this.$t('flow.updateSuccess') });
        await this.queryFlowDefinePage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    openDeployDialog(row) {
      this.deployForm.flowDefinitionId = row.id;
      this.deployForm.flowName = row.flowName;
      flowVersionService.getLatestDeployVersion(row.flowKey).then(res => {
        this.deployFormVisible = true;
        this.deployForm.flowDeployVersion = res.result;
      });
    },
    async onSubmitDeploy() {
      await this.deployFlowDefine(this.deployForm.flowDefinitionId, this.deployForm.flowDeployVersion, this.deployForm.flowVersionRemark);
    },
    async deployFlowDefine(flowDefinitionId, flowDeployVersion, flowVersionRemark) {
      const res = await flowDefineService.deployFlowDefine({
        flowDefinitionId: flowDefinitionId,
        flowDeployVersion: flowDeployVersion,
        flowVersionRemark: flowVersionRemark,
      });
      if (res.success) {
        ElMessage({ type: 'success', message: this.$t('flow.deploySuccess') });
        this.deployFormVisible = false;
        await this.queryFlowDefinePage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    openDelete(row) {
      ElMessageBox.confirm(this.$t('flow.confirmDeleteFlow', { name: row.flowName }), this.$t('common.operationConfirm'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning',
      })
        .then(() => {
          this.deleteFlowDefineItem(row);
        })
        .catch(() => {});
    },
    async deleteFlowDefineItem(row) {
      const res = await flowDefineService.deleteFlowDefineById(row.id);
      if (res.success) {
        ElMessage({ type: 'success', message: this.$t('common.deleteSuccess') });
        await this.queryFlowDefinePage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    openEdit(row) {
      this.$refs.drawerRef.open(row);
    },
  },
};
</script>

<template>
  <div class="page-flow-define">
    <div class="search-bar">
      <FlowDefineFilter @search="onSearch" />
    </div>
    <el-container class="table-container">
      <el-header class="page-header">
        <el-button :icon="Plus" type="primary" @click="openflowDefineAdd">{{ $t('common.create') }}</el-button>
      </el-header>
      <el-main class="page-body">
        <FlowDefineTable
          :dataRows="dataRows"
          :dataTotal="dataTotal"
          :pageNum="pageNum"
          :pageSize="pageSize"
          :loading="loading"
          @pageChange="onPageChange"
          @deploy="openDeployDialog"
          @edit="openEdit"
          @delete="openDelete"
        />
      </el-main>
    </el-container>
    <FlowDefineDrawer ref="drawerRef" @add="addFlowDefineItem" @edit="updateFlowDefineItem" />

    <el-dialog v-model="deployFormVisible" :show-close="false" :title="$t('flow.deployFlow')" width="400">
      <el-form :model="deployForm">
        <el-form-item :label="$t('flow.flowName')">
          <el-input v-model="deployForm.flowName" disabled />
        </el-form-item>
        <el-form-item :label="$t('flow.deployVersion')">
          <el-input v-model="deployForm.flowDeployVersion" disabled />
        </el-form-item>
        <el-form-item :label="$t('flow.versionDesc')">
          <el-input type="textarea" v-model="deployForm.flowVersionRemark" maxlength="120" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deployFormVisible = false">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="onSubmitDeploy">{{ $t('flow.deploy') }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>
