<script>
import { RouterView } from 'vue-router';
import zhCn from 'element-plus/dist/locale/zh-cn.mjs';
import en from 'element-plus/dist/locale/en.mjs';

export default {
  components: {
    RouterView,
  },
  computed: {
    elLocale() {
      return this.$i18n.locale === 'en' ? en : zhCn;
    },
  },
};
</script>

<template>
  <el-config-provider :locale="elLocale">
    <RouterView />
  </el-config-provider>
</template>
