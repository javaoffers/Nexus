<script>
import { Plus } from '@element-plus/icons-vue';
import { TokenTable, TokenForm } from './token';
import { tokenService } from '@/service';
import { ElMessage, ElMessageBox } from 'element-plus';
import TokenSuccessForm from '@/views/system/token/TokenSuccessForm.vue';

export default {
  components: {
    TokenTable,
    TokenForm,
    TokenSuccessForm,
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
      const res = await tokenService.queryTokenPage({
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
      ElMessageBox.confirm(this.$t('system.confirmDeleteToken', { name: row.tokenDesc }), this.$t('common.operationConfirm'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning',
      })
        .then(() => {
          this.deleteTokenItem(row);
        })
        .catch(() => {});
    },
    async addTokenItem(row) {
      const res = await tokenService.addToken(row);
      if (res.result) {
        this.$refs.successFormRef.open(res.result);
        await this.queryPage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    async editTokenItem(row) {
      const res = await tokenService.updateToken({
        id: row.id,
        tokenDesc: row.tokenDesc,
      });
      if (res.result) {
        ElMessage({ type: 'success', message: this.$t('common.editSuccess') });
        await this.queryPage();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    async deleteTokenItem(row) {
      const res = await tokenService.deleteToken(row.id);
      if (res.result) {
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
  <div class="page-interface-token">
    <el-container class="table-container">
      <el-header class="page-header">
        <el-button :icon="Plus" type="primary" @click="openAdd">{{ $t('common.create') }}</el-button>
      </el-header>
      <el-main class="page-body">
        <TokenTable
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
    <TokenForm ref="formRef" @add="addTokenItem" @edit="editTokenItem" />
    <TokenSuccessForm ref="successFormRef" />
  </div>
</template>
