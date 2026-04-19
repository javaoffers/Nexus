<script>
import { FlowTable, FlowFilter } from './list';
import { flowService } from '@/service';
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  components: {
    FlowTable,
    FlowFilter,
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
    this.queryFlowPage();
  },
  methods: {
    onSearch(param) {
      this.filter = param;
      this.onPageChange(1);
    },
    async queryFlowPage() {
      this.loading = true;
      const res = await flowService.queryFlowPage({
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
      this.queryFlowPage();
    },
    openUpdateFlowStatus(row) {
      ElMessageBox.confirm(`确定${row.flowStatus == 0 ? '启用' : '禁用'}'${row.flowName}'流程吗?`, '操作确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
          this.updateFlowStatus(row);
        })
        .catch(() => {});
    },
    async updateFlowStatus(row) {
      const res = await flowService.updateFlowStatus(row.id, row.flowStatus);
      if (res.success) {
        ElMessage({ type: 'success', message: '操作成功' });
        await this.queryFlowPage();
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
          this.deleteFlowItem(row);
        })
        .catch(() => {});
    },
    async deleteFlowItem(row) {
      const res = await flowService.deleteFlowById(row.id);
      if (res.success) {
        ElMessage({ type: 'success', message: '删除成功' });
        await this.queryFlowPage();
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
      <FlowFilter @search="onSearch" />
    </div>
    <el-container class="table-container">
      <el-header class="page-header">

      </el-header>
      <el-main class="page-body">
        <FlowTable
          :dataRows="dataRows"
          :dataTotal="dataTotal"
          :pageNum="pageNum"
          :pageSize="pageSize"
          :loading="loading"
          @pageChange="onPageChange"
          @flowStatusChange="openUpdateFlowStatus"
          @delete="openDelete"
        />
      </el-main>
    </el-container>
  </div>
</template>
