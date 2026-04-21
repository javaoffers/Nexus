<script>
export default {
  props: ['dataRows', 'pageNum', 'pageSize', 'dataTotal', 'loading'],
  emits: ['pageChange', 'edit', 'delete'],
  methods: {
    deleteRow(row, index) {
      this.$emit('delete', row, index);
    },
    editRow(row) {
      this.$emit('edit', row);
    },
    goApiDebugPage(apiId) {
      const routeData = this.$router.resolve({
        name: 'api-debug',
        params: {
          apiId: apiId,
        },
      });
      window.open(routeData.href, '_blank');
    },
  },
};
</script>
<template>
  <el-table v-loading="loading" :data="dataRows" size="large" header-cell-class-name="table-header">
    <el-table-column prop="apiName" :label="$t('suite.apiName')" width="150" />
    <el-table-column prop="apiUrl" :label="$t('suite.apiUrl')" width="270" show-overflow-tooltip />
    <el-table-column prop="suiteName" :label="$t('menu.suite')" width="150" />
    <el-table-column prop="apiRequestType" :label="$t('suite.requestType')" width="90" />
    <el-table-column prop="apiDesc" :label="$t('suite.apiDesc')" min-width="50" show-overflow-tooltip />
    <el-table-column prop="createdAt" :label="$t('common.createTime')" width="110" />
    <el-table-column fixed="right" :label="$t('common.operation')" width="150">
      <template #default="scope">
        <el-button link type="primary" size="small" @click.prevent="goApiDebugPage(scope.row.id)"> {{ $t('flow.debug') }} </el-button>
        <el-button link type="primary" size="small" @click.prevent="editRow(scope.row)"> {{ $t('common.edit') }} </el-button>
        <el-button link type="primary" size="small" @click.prevent="deleteRow(scope.row, scope.$index)"> {{ $t('common.delete') }} </el-button>
      </template>
    </el-table-column>
  </el-table>
  <div class="table-pagination">
    <el-pagination
      :currentPage="pageNum"
      :pageSize="pageSize"
      background
      layout="total, prev, pager, next"
      :total="dataTotal"
      @currentChange="(val) => $emit('pageChange', val)"
    />
  </div>
</template>
<style scoped>
</style>
