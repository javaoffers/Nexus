<script>
import { User, Lock } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { userService } from '@/service';

export default {
  data() {
    return {
      userName: '',
      password: '',
      loading: false,
      User: User,
      Lock: Lock,
    };
  },
  methods: {
    async submit() {
      if (!this.userName || !this.password) {
        ElMessage.error(this.$t('login.emptyError'));
        return;
      }
      this.loading = true;
      const result = await userService.login({
        userName: this.userName,
        password: this.password,
      });
      if (result.success) {
        await this.$router.push({ name: 'flow' });
      } else {
        ElMessage.error(result.errorMsg || this.$t('login.failed'));
      }
      this.loading = false;
    },
  },
};
</script>

<template>
  <div class="login-form">
    <div class="login-form-item">
      <el-input v-model="userName" type="text" :placeholder="$t('login.username')" size="large" :prefix-icon="User" @keyup.enter="submit" />
    </div>
    <div class="login-form-item">
      <el-input v-model="password" type="password" :placeholder="$t('login.password')" show-password size="large" :prefix-icon="Lock" @keyup.enter="submit" />
    </div>
    <div class="login-form-item">
      <el-button type="primary" size="large" :style="{ width: '100%', marginTop: '24px' }" :loading="loading" @click="submit"> {{ $t('login.submit') }} </el-button>
    </div>
  </div>
</template>

<style scoped>
.login-form {
  margin-top: 0;
}
.login-form .login-form-item {
  margin-bottom: 20px;
}
</style>
