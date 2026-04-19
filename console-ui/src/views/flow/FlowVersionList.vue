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
      ElMessageBox.confirm(`确定${row.flowVersionStatus == 0 ? '启用' : '禁用'} ${row.flowName} 流程的 ${row.flowVersion} 版本吗?`, '操作确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
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
        ElMessage({ type: 'success', message: '操作成功' });
        await this.queryFlowVersionPage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    openDelete(row) {
      ElMessageBox.confirm(`确定删除'${row.flowName}'流程吗?`, '操作确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
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
        ElMessage({ type: 'success', message: '删除成功' });
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
