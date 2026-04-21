<script>
import { FlowVersionTable, FlowVersionFilter } from './version';
import { flowVersionService } from '@/service';
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  components: {
    FlowVersionTable,
    FlowVersionFilter,
  },
  data() {
    return {
      pageNum: 1,
      pageSize: 10,
      dataTotal: 0,
      dataRows: [],
      loading: false,
      filter: {},
    };
  },
  created() {
    this.queryFlowVersionPage();
  },
  methods: {
    onSearch(param) {
      this.filter = param;
      this.onPageChange(1);
    },
    async queryFlowVersionPage() {
      this.loading = true;
      const res = await flowVersionService.queryFlowVersionPage({
        pageSize: this.pageSize,
        pageNum: this.pageNum,
        flowId: Number(this.$route.params.flowId),
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
      this.queryFlowVersionPage();
    },
    openUpdateFlowVersionStatus(row) {
      ElMessageBox.confirm(this.$t('flow.confirmToggleVersion', { action: this.$t(row.flowVersionStatus == 0 ? 'flow.enabled' : 'flow.disabled'), name: row.flowName, version: row.flowVersion }), this.$t('common.operationConfirm'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning',
      })
        .then(() => {
          this.updateFlowStatus(row);
        })
        .catch(() => {});
    },
    async updateFlowStatus(row) {
      const res = await flowVersionService.updateFlowVersionStatus(row.id, row.flowVersionStatus);
      if (res.success) {
        ElMessage({ type: 'success', message: this.$t('common.operationSuccess') });
        await this.queryFlowVersionPage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    openDelete(row) {
      ElMessageBox.confirm(this.$t('flow.confirmDeleteVersion', { name: row.flowName }), this.$t('common.operationConfirm'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning',
      })
        .then(() => {
          this.deleteFlowVersionItem(row);
        })
        .catch(() => {});
    },
    async deleteFlowVersionItem(row) {
      const res = await flowVersionService.deleteFlowVersionById(row.id);
      if (res.success) {
        ElMessage({ type: 'success', message: this.$t('common.deleteSuccess') });
        await this.queryFlowVersionPage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
  },
};
</script>

<template>
  <div class="page-flow">
    <div class="search-bar">
      <FlowVersionFilter @search="onSearch" />
    </div>
    <el-container class="table-container">
      <el-header class="page-header">
      </el-header>
      <el-main class="page-body">
        <FlowVersionTable
          :dataRows="dataRows"
          :dataTotal="dataTotal"
          :pageNum="pageNum"
          :pageSize="pageSize"
          :loading="loading"
          @pageChange="onPageChange"
          @flowVersionStatusChange="openUpdateFlowVersionStatus"
          @delete="openDelete"
        />
      </el-main>
    </el-container>
  </div>
</template>
