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
      ElMessageBox.confirm(this.$t('system.confirmDeleteDS', { name: row.dataSourceName }), this.$t('common.operationConfirm'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
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
        ElMessage({ type: 'success', message: this.$t('common.editSuccess') });
        await this.queryPage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    async deleteDataSourceItem(row) {
      const res = await dataSourceService.deleteDataSource(row.id);
      if (res.result) {
        ElMessage({ type: 'success', message: this.$t('common.deleteSuccess') });
        await this.queryPage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    async connectDataSource(row) {
      const res = await dataSourceService.connectDataSource(row.id);
      if (res.result) {
        ElMessage({ type: 'success', message: this.$t('system.connectSuccess') });
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
        <el-button :icon="Plus" type="primary" @click="openAdd">{{ $t('common.create') }}</el-button>
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
