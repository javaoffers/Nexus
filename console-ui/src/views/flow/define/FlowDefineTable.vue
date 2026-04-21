<script>
export default {
  props: ['dataRows', 'pageNum', 'pageSize', 'dataTotal', 'loading'],
  emits: ['pageChange', 'deploy', 'edit', 'delete'],
  methods: {
    deployFlow(row) {
      this.$emit('deploy', row);
    },
    deleteRow(row, index) {
      this.$emit('delete', row, index);
    },
    editRow(row) {
      this.$emit('edit', row);
    },
    goDebugPage(flowDefinitionId, flowKey) {
      this.$router.push({
        name: 'flow-debug',
        params: {
          flowDefinitionId: flowDefinitionId,
          flowKey: flowKey,
        },
      });
    },
    goDesignPage(flowDefinitionId, flowKey) {
      const path = "/design/" + flowDefinitionId + "/" + flowKey;
      const { href } = this.$router.resolve({ path });
      window.open(href, '_blank');
    },
  },
};
</script>

<template>
  <el-table v-loading="loading" :data="dataRows" size="large" header-cell-class-name="table-header">
    <el-table-column prop="flowKey" :label="$t('flow.flowCode')" width="180" />
    <el-table-column prop="flowName" :label="$t('flow.flowName')" width="220" show-overflow-tooltip />
    <el-table-column prop="flowType" :label="$t('flow.flowType')" width="140">
      <template #default="scope">
        <el-tag v-if="scope.row.flowType == 'sync'" type="success">{{ $t('flow.sync') }}</el-tag>
        <el-tag v-else type="warning">{{ $t('flow.async') }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="remark" :label="$t('flow.flowDesc')" show-overflow-tooltip />
    <el-table-column prop="createdAt" :label="$t('common.createTime')" width="140" />
    <el-table-column :label="$t('common.operation')" width="250">
      <template #default="scope">
        <el-button link type="primary" size="small" @click="goDesignPage(scope.row.id, scope.row.flowKey)"> {{ $t('flow.design') }} </el-button>
        <el-button link type="primary" size="small" @click="goDebugPage(scope.row.id, scope.row.flowKey)"> {{ $t('flow.debug') }} </el-button>
        <el-button link type="primary" size="small" @click="deployFlow(scope.row)"> {{ $t('flow.deploy') }} </el-button>
        <el-button link type="primary" size="small" @click="editRow(scope.row)"> {{ $t('common.edit') }} </el-button>
        <el-button link type="primary" size="small" @click="deleteRow(scope.row, scope.$index)"> {{ $t('common.delete') }} </el-button>
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
