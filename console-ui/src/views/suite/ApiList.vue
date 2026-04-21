<script>
import { Plus } from '@element-plus/icons-vue';
import { ListFilter, ListTable, ListForm } from './api';
import { apiService } from '@/service';
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  components: {
    ListFilter,
    ListTable,
    ListForm,
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
    };
  },
  created() {
    this.queryPage();
  },
  methods: {
    async queryPage() {
      this.loading = true;
      const params = {
        pageSize: this.pageSize,
        pageNum: this.pageNum,
        suiteId: Number(this.$route.params.suiteId),
        ...this.filter,
      };
      const res = await apiService.listQuery(params);
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
    onSearch(val) {
      this.filter = val;
      this.onPageChange(1);
    },
    openAdd() {
      this.$refs.formRef.open();
    },
    openEdit(row) {
      this.$refs.formRef.open(row);
    },
    openDelete(row) {
      ElMessageBox.confirm(this.$t('suite.confirmDeleteApi', { name: row.apiName }), this.$t('common.operationConfirm'), {
        type: 'warning',
        cancelButtonText: this.$t('common.cancel'),
        confirmButtonText: this.$t('common.confirm'),
      }).then(() => {
          this.deleteApiItem(row);
        })
        .catch(() => {});
    },
    async addApiItem(row) {
      row.suiteId = Number(this.$route.params.suiteId);
      const res = await apiService.listAdd(row);
      if (res.success) {
        ElMessage({ type: 'success', message: this.$t('common.createSuccess') });
        await this.queryPage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    async editApiItem(row) {
      const res = await apiService.listUpdate(row);
      if (res.success) {
        ElMessage({ type: 'success', message: this.$t('common.editSuccess') });
        await this.queryPage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    async deleteApiItem(row) {
      const res = await apiService.listDelete(row.id);
      if (res.success) {
        ElMessage({ type: 'success', message: this.$t('common.deleteSuccess') });
        await this.queryPage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
  },
};
</script>
<template>
  <div class="page-interface-list">
    <el-container class="table-container">
      <el-header class="page-header">
        <ListFilter @search="onSearch" />
        <el-button :icon="Plus" type="primary" @click="openAdd">{{ $t('common.create') }}</el-button>
      </el-header>
      <el-main class="page-body">
        <ListTable
          :dataRows="dataRows"
          :dataTotal="dataTotal"
          :pageNum="pageNum"
          :pageSize="pageSize"
          :loading="loading"
          @pageChange="onPageChange"
          @edit="openEdit"
          @delete="openDelete"
        />
      </el-main>
    </el-container>
    <ListForm ref="formRef" @add="addApiItem" @edit="editApiItem" />
  </div>
</template>
