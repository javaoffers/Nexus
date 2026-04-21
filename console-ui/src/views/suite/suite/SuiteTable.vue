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
    goApiListPage(suiteCode, suiteId) {
      this.$router.push({
        name: 'api-list',
        params: {
          suiteCode: suiteCode,
          suiteId: suiteId,
        },
      });
    },
  },
};
</script>

<template>
  <el-table v-loading="loading" :data="dataRows" size="large" header-cell-class-name="table-header">
    <el-table-column prop="suiteCode" :label="$t('suite.suiteImage')" width="100" >
      <template #default="scope">
        <img v-if="scope.row.suiteImage" :src="scope.row.suiteImage" @error="e => { e.target.src = '/suite/default.svg' }" class="suite-image" alt="suite image" />
        <img v-else class="suite-image" >
      </template>
    </el-table-column>
    <el-table-column prop="suiteCode" :label="$t('suite.suiteCode')" width="180" />
    <el-table-column prop="suiteName" :label="$t('suite.suiteName')" width="180" />
    <el-table-column prop="flowType" :label="$t('suite.suiteType')" width="140">
      <template #default="scope">
        <el-tag v-if="scope.row.suiteFlag == 0" type="info">{{ $t('suite.builtinTag') }}</el-tag>
        <el-tag v-else-if="scope.row.suiteFlag == 1" type="success">{{ $t('suite.officialTag') }}</el-tag>
        <el-tag v-else type="primary">{{ $t('suite.personalTag') }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="suiteDesc" :label="$t('suite.suiteDesc')" show-overflow-tooltip />
    <el-table-column :label="$t('common.operation')" width="180">
      <template #default="scope">
        <el-button link type="primary" size="small" @click.prevent="editRow(scope.row)"> {{ $t('common.edit') }} </el-button>
        <el-button link type="primary" size="small" @click.prevent="goApiListPage(scope.row.suiteCode,scope.row.id)"> {{ $t('suite.apiList') }} </el-button>
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
.suite-image {
  width: 40px;
  height: 40px;
}
</style>
