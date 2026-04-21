<script>
import { RouterLink } from 'vue-router';

export default {
  components: {
    RouterLink,
  },
};
</script>
<template>
  <div>{{ $t('notFound.message') }}<RouterLink to="/">{{ $t('notFound.home') }}</RouterLink></div>
</template>
