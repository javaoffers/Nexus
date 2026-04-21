<script>
export default {
  props: ['dataRows', 'pageNum', 'pageSize', 'dataTotal', 'loading'],
  emits: ['pageChange', 'flowVersionStatusChange', 'delete'],
  methods: {
    deleteRow(row, index) {
      this.$emit('delete', row, index);
    },
    updateFlowVersionStatus(row) {
      this.$emit('flowVersionStatusChange', row);
    },
    flowVersionStatusOptFormat(flowVersionStatus) {
      if (flowVersionStatus == 0) {
        return this.$t('flow.enabled');
      } else {
        return this.$t('flow.disabled');
      }
    },
    buildFullTriggerUrl(triggerUrl) {
      const origin = window.location.origin;
      return origin + triggerUrl;
    },
  },
};
</script>

<template>
  <el-table v-loading="loading" :data="dataRows" size="large" header-cell-class-name="table-header">
    <el-table-column prop="flowName" :label="$t('flow.flowName')" width="120" show-overflow-tooltip />
    <el-table-column prop="flowVersion" :label="$t('flow.version')" width="60" />
    <el-table-column prop="flowVersion" :label="$t('flow.flowStatus')" width="100">
      <template #default="scope">
        <el-tag v-if="scope.row.flowVersionStatus == 1" type="success">{{ $t('flow.enabled') }}</el-tag>
        <el-tag v-else type="danger">{{ $t('flow.disabled') }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="triggerUrl" :label="$t('flow.accessUrl')" width="480">
      <template #default="scope">
        {{ buildFullTriggerUrl(scope.row.triggerUrl) }}
      </template>
    </el-table-column>
    <el-table-column prop="flowVersionRemark" :label="$t('flow.versionDesc')" show-overflow-tooltip />
    <el-table-column :label="$t('common.operation')" width="260">
      <template #default="scope">
        <el-button link type="primary" size="small" @click.prevent="updateFlowVersionStatus(scope.row)">
          {{ flowVersionStatusOptFormat(scope.row.flowVersionStatus) }}
        </el-button>
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
