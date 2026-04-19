<script>
import { userService } from '@/service';
import { ElMessage } from 'element-plus';

export default {
  data() {
    return {
      userName: '',
      userNameInitial: '',
      aboutDialogVisible: false,
      productVersion: undefined,
    };
  },
  created() {
    this.userName = window.localStorage.getItem('Nexus-userName');
    this.userNameInitial = this.userName.charAt(0);
    this.getProductInfo();
  },
  methods: {
    openAboutDialog() {
      this.aboutDialogVisible = true;
    },
    aboutMe() {
      window.open("https://nexus.plus/about_me.html", '_blank');
    },
    contactMe() {
      window.open("https://nexus.plus", '_blank');
    },
    async getProductInfo() {
      const res = await userService.getProductInfo();
      if (res.success) {
        this.productVersion = res.result;
      }
    },
    async logout() {
      const res = await userService.logout();
      if (res) {
        await this.$router.push('/login');
      } else {
        ElMessage.error('退出失败');
      }
    },
    extractColorByName(name) {
      const temp = [];
      temp.push('#');
      for (let index = 0; index < name.length; index++) {
        temp.push(parseInt(name[index].charCodeAt(0), 10).toString(16));
      }
      return temp.slice(0, 5).join('').slice(0, 4);
    },
  },
};
</script>
<template>
  <el-dropdown class="app-current-userPO-dropdown">
    <div class="app-current-userPO">
      <el-avatar :size="32" :style="`background:${extractColorByName(userName)}`">{{ userNameInitial }}</el-avatar>
      <span class="current-userPO-name">{{ userName }}</span>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
<!--        <el-dropdown-item @click="$router.push('/userPO')">用户信息</el-dropdown-item>-->
        <el-dropdown-item @click="openAboutDialog">关于</el-dropdown-item>
        <el-dropdown-item @click="logout" divided>退出</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>

  <el-dialog v-model="aboutDialogVisible" title="关于" width="500" center>
    <el-descriptions :column="1">
      <el-descriptions-item label="版本:">开源版</el-descriptions-item>
      <el-descriptions-item label="版本号:">{{productVersion}}</el-descriptions-item>
      <el-descriptions-item label="备注:"></el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" plain @click="aboutMe">关于我们</el-button>
        <el-button type="success" plain @click="contactMe">联系我们</el-button>
      </div>
    </template>
  </el-dialog>
</template>
<style scoped>
.app-current-userPO-dropdown {
  height: 100%;
  padding: 0 20px 0 0;
}
.app-current-userPO {
  height: 100%;
  display: flex;
  align-items: center;
  color: var(--nexus-text-primary);
  outline: none;
}
.app-current-userPO .el-avatar {
  font-size: 22px;
  font-weight: bold;
}
.app-current-userPO .current-userPO-name {
  font-size: 15px;
  margin-left: 8px;
}
</style>
