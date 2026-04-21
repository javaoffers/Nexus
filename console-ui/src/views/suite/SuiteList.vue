<script>
import { Plus } from '@element-plus/icons-vue';
import { SuiteFilter, SuiteTable, SuiteForm } from './suite';
import { suiteService } from '@/service';
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  components: {
    SuiteFilter,
    SuiteTable,
    SuiteForm,
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
    this.querySuitePage();
  },
  methods: {
    async querySuitePage() {
      this.loading = true;
      const res = await suiteService.querySuitePage({
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
      this.querySuitePage();
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
      ElMessageBox.confirm(this.$t('suite.confirmDelete', { name: row.suiteName }), this.$t('common.operationConfirm'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning',
      })
        .then(() => {
          this.deleteSuiteItem(row);
        })
        .catch(() => {});
    },
    async addSuiteItem(row) {
      const res = await suiteService.addSuite(row);
      if (res.result) {
        ElMessage({ type: 'success', message: this.$t('common.createSuccess') });
        await this.querySuitePage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    async editSuiteItem(row) {
      const res = await suiteService.updateSuite(row);
      if (res.result) {
        ElMessage({ type: 'success', message: this.$t('common.editSuccess') });
        await this.querySuitePage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    async deleteSuiteItem(row) {
      const res = await suiteService.deleteSuite(row.id);
      if (res.result) {
        ElMessage({ type: 'success', message: this.$t('common.deleteSuccess') });
        await this.querySuitePage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
  },
};
</script>
<template>
  <div class="page-interface-suite">
    <div class="search-bar">
      <SuiteFilter @search="onSearch" />
    </div>
    <el-container class="table-container">
      <el-header class="page-header">
        <el-button :icon="Plus" type="primary" @click="openAdd">{{ $t('common.create') }}</el-button>
      </el-header>
      <el-main class="page-body">
        <SuiteTable
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
    <SuiteForm ref="formRef" @add="addSuiteItem" @edit="editSuiteItem" />
  </div>
</template>
