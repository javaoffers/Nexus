<script>
export default {
  data() {
    return {
      matched: this.$router.currentRoute.value.matched,
    };
  },
  created() {
    this.$router.afterEach((to) => {
      this.matched = to.matched;
    });
  },
};
</script>
<template>
  <el-breadcrumb separator="/">
    <template v-for="item in matched" :key="item.path">
      <el-breadcrumb-item :to="{ path: item.path }" v-if="item.name !== 'home'">
        {{ $t(item.meta.name) }}
      </el-breadcrumb-item>
    </template>
  </el-breadcrumb>
</template>
