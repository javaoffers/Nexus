<script>
import { RouterView } from 'vue-router';
import zhCn from 'element-plus/dist/locale/zh-cn.mjs';

export default {
  components: {
    RouterView,
  },
  data() {
    return {
      zhCn,
    };
  },
};
</script>

<template>
  <el-config-provider :locale="zhCn">
    <RouterView />
  </el-config-provider>
</template>
