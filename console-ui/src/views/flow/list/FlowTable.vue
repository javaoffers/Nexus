<script>
export default {
  props: ['dataRows', 'pageNum', 'pageSize', 'dataTotal', 'loading'],
  emits: ['pageChange', 'flowStatusChange', 'delete'],
  methods: {
    deleteRow(row, index) {
      this.$emit('delete', row, index);
    },
    goFlowVersionListPage(flowId) {
      this.$router.push({
        name: 'flow-version',
        params: {
          flowId: flowId,
        },
      });
    },
  },
};
</script>

<template>
  <el-table v-loading="loading" :data="dataRows" size="large" header-cell-class-name="table-header">
    <el-table-column prop="flowKey" :label="$t('flow.flowCode')" width="180" />
    <el-table-column prop="flowName" :label="$t('flow.flowName')" width="220" />
    <el-table-column prop="flowType" :label="$t('flow.flowType')" width="140">
      <template #default="scope">
        <el-tag v-if="scope.row.flowType == 'sync'" type="success">{{ $t('flow.sync') }}</el-tag>
        <el-tag v-else type="warning">{{ $t('flow.async') }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="remark" :label="$t('flow.flowDesc')" show-overflow-tooltip />
    <el-table-column :label="$t('common.operation')" width="130">
      <template #default="scope">
        <el-button link type="primary" size="small" @click="goFlowVersionListPage(scope.row.id)"> {{ $t('flow.versionList') }} </el-button>
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
