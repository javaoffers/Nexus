<script>
import { Plus } from '@element-plus/icons-vue';
import { DataSourceTable, DataSourceDrawer } from './datasource';
import { dataSourceService } from '@/service';
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  components: {
    DataSourceTable,
    DataSourceDrawer,
    Plus,
  },
  data() {
    return {
      pageNum: 1,
      pageSize: 10,
      dataTotal: 0,
      dataRows: [],
      loading: false,
    };
  },
  created() {
    this.queryPage();
  },
  methods: {
    async queryPage() {
      this.loading = true;
      const res = await dataSourceService.queryDataSourcePage({
        pageSize: this.pageSize,
        pageNum: this.pageNum,
      });
      if (res.success) {
        this.dataTotal = res.total;
        this.dataRows = res.result;
      }
      this.loading = false;
    },
    onPageChange(page) {
      this.pageNum = page;
      this.queryPage();
    },
    openAdd() {
      this.$refs.formRef.open();
    },
    openEdit(row) {
      this.$refs.formRef.open(row);
    },
    openDelete(row) {
      ElMessageBox.confirm(`确定删除'${row.dataSourceName}'吗?,删除后将影响正在使用该数据源的数据节点`, '操作确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          this.deleteDataSourceItem(row);
        })
        .catch(() => {});
    },
    async addDataSourceItem(row) {
      const res = await dataSourceService.addDataSource(row);
      if (res.result) {
        await this.queryPage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    async editDataSourceItem(row) {
      const res = await dataSourceService.updateDataSource(row);
      if (res.result) {
        ElMessage({ type: 'success', message: '编辑成功' });
        await this.queryPage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    async deleteDataSourceItem(row) {
      const res = await dataSourceService.deleteDataSource(row.id);
      if (res.result) {
        ElMessage({ type: 'success', message: '删除成功' });
        await this.queryPage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    async connectDataSource(row) {
      const res = await dataSourceService.connectDataSource(row.id);
      if (res.result) {
        ElMessage({ type: 'success', message: '连接成功' });
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
  },
};
</script>
<template>
  <div class="page-interface-token">
    <el-container class="table-container">
      <el-header class="page-header">
        <el-button :icon="Plus" type="primary" @click="openAdd">新建</el-button>
      </el-header>
      <el-main class="page-body">
        <DataSourceTable
          :dataRows="dataRows"
          :dataTotal="dataTotal"
          :pageNum="pageNum"
          :pageSize="pageSize"
          :loading="loading"
          @pageChange="onPageChange"
          @connect="connectDataSource"
          @edit="openEdit"
          @delete="openDelete"
        />
      </el-main>
    </el-container>
    <DataSourceDrawer ref="formRef" @add="addDataSourceItem" @edit="editDataSourceItem" />
  </div>
</template>
