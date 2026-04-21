<script>
import { useClipboard } from '@vueuse/core';
import { ElMessage } from 'element-plus';

export default {
  data() {
    return {
      tokenSuccessVisible: false,
      tokenValue: '',
    };
  },
  methods: {
    copyToken() {
      const { copy, isSupported } = useClipboard({ legacy: true });
      if (!isSupported) {
        ElMessage({ type: 'error', message: this.$t('system.tokenCopyFail') });
        return;
      }
      copy(this.tokenValue);
      ElMessage({ type: 'success', message: this.$t('system.copySuccess') });
    },
    open(value) {
      this.tokenSuccessVisible = true;
      this.tokenValue = value;
    },
    close() {
      this.tokenSuccessVisible = false;
    },
  },
};
</script>
<template>
  <el-dialog v-model="tokenSuccessVisible" :title="$t('system.tokenGenTitle')" :close-on-click-modal="false" width="400">
    <el-result icon="success" :title="$t('system.tokenGenSuccess')"> </el-result>
    <el-input v-model="tokenValue">
      <template #append>
        <el-button @click.prevent="copyToken">{{ $t('common.copy') }}</el-button>
      </template>
    </el-input>
    <div class="token-tips">
      <el-text class="mx-1" type="danger">{{ $t('system.tokenWarning') }}</el-text>
    </div>
    <template #footer>
      <el-button type="primary" @click.prevent="close">{{ $t('system.confirmClose') }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.token-tips {
  margin-top: 10px;
}
</style>
