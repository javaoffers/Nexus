<script>
export default {
  props: ['dataRows', 'pageNum', 'pageSize', 'dataTotal', 'loading'],
  emits: ['pageChange', 'connect', 'edit', 'delete'],
  methods: {
    connect(row) {
      this.$emit('connect', row);
    },
    editRow(row) {
      this.$emit('edit', row);
    },
    deleteRow(row, index) {
      this.$emit('delete', row, index);
    },
  },
};
</script>

<template>
  <el-table v-loading="loading" :data="dataRows" size="large" header-cell-class-name="table-header">
    <el-table-column prop="dataSourceName" :label="$t('system.dataSourceName')" />
    <el-table-column prop="dataSourceType" :label="$t('system.dataSourceType')" />
    <el-table-column prop="address" :label="$t('system.connAddress')" width="380" />
    <el-table-column :label="$t('common.operation')" width="180">
      <template #default="scope">
        <el-button link type="primary" size="small" @click.prevent="connect(scope.row)"> {{ $t('system.connect') }} </el-button>
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
