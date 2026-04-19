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
  },
};
</script>

<template>
  <el-table v-loading="loading" :data="dataRows" size="large" header-cell-class-name="table-header">
    <el-table-column prop="tokenDesc" label="令牌描述" />
    <el-table-column prop="createdAt" label="创建时间" width="180" />
    <el-table-column label="操作" width="180">
      <template #default="scope">
        <el-button link type="primary" size="small" @click.prevent="editRow(scope.row)"> 编辑 </el-button>
        <el-button link type="primary" size="small" @click.prevent="deleteRow(scope.row, scope.$index)"> 删除 </el-button>
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
