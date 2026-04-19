<script>
import { Plus } from '@element-plus/icons-vue';
import { commonService, objectService } from '@/service';
import { ElMessage, ElMessageBox } from 'element-plus';
import ObjectFilter from '@/views/object/ObjectFilter.vue';
import ObjectTable from '@/views/object/ObjectTable.vue';
import ObjectDrawer from '@/views/object/ObjectDrawer.vue';

export default {
  components: {
    ObjectFilter,
    ObjectTable,
    ObjectDrawer,
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
    this.queryObjectPage();
  },
  methods: {
    async queryObjectPage() {
      this.loading = true;
      const params = {
        pageSize: this.pageSize,
        pageNum: this.pageNum,
        ...this.filter,
      };
      const res = await objectService.queryObjectPage(params);
      if (res.success) {
        this.dataTotal = res.total;
        this.dataRows = res.result;
      }
      this.loading = false;
    },
    onPageChange(page) {
      this.pageNum = page;
      this.queryObjectPage();
    },
    onSearch(val) {
      this.filter = val;
      this.onPageChange(1);
    },
    openObjectAdd() {
      this.$refs.formRef.open();
    },
    openEdit(row) {
      this.$refs.formRef.open(row);
    },
    openDelete(row) {
      ElMessageBox.confirm(`确定删除'${row.objectName}'吗?`, '操作确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          this.deleteItem(row);
        })
        .catch(() => {});
    },
    async addObjectItem(row) {
      const paramArray = row.props;
      if (Array.isArray(paramArray) && paramArray.length !== 0) {
        const propArray = paramArray.map((item) => {
          return {
            propKey: item.paramKey,
            propName: item.paramName,
            dataType: item.dataType,
          };
        });
        row.props = propArray;
      }
      const res = await objectService.addObject(row);
      if (res.success) {
        ElMessage({ type: 'success', message: '新建成功' });
        await this.queryObjectPage();
        commonService.dataType.clearList();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    async editObjectItem(row) {
      const paramArray = row.props;
      if (Array.isArray(paramArray) && paramArray.length !== 0) {
        const propArray = paramArray.map((item) => {
          return {
            propKey: item.paramKey,
            propName: item.paramName,
            dataType: item.dataType,
          };
        });
        row.props = propArray;
      }
      const res = await objectService.updateObject(row);
      if (res.success) {
        ElMessage({ type: 'success', message: '编辑成功' });
        await this.queryObjectPage();
        commonService.dataType.clearList();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    async deleteItem(row) {
      const res = await objectService.deleteObject(row.id);
      if (res.success) {
        ElMessage({ type: 'success', message: '删除成功' });
        await this.queryObjectPage();
        commonService.dataType.clearList();
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
  },
};
</script>
<template>
  <div class="page-object-list">
    <div class="search-bar">
      <ObjectFilter @search="onSearch" />
    </div>
    <el-container class="table-container">
      <el-header class="page-header">
        <el-button :icon="Plus" type="primary" @click="openObjectAdd">新建</el-button>
      </el-header>
      <el-main class="page-body">
        <ObjectTable
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
    <ObjectDrawer ref="formRef" @add="addObjectItem" @edit="editObjectItem" />
  </div>
</template>
