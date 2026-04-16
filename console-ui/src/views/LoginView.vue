<script setup lang="ts">
import LoginForm from '@/components/entry/LoginForm.vue';
</script>

<template>
  <div class="entry-view">
    <div class="entry-view-left">
      <div class="brand-content">
        <img src="@/assets/logo.png" alt="Nexus Logo" class="brand-logo" />
        <h1 class="brand-title">NEXUS</h1>
        <p class="brand-subtitle">零码 + 低码 + AI 的微服务编排与系统集成平台</p>
      </div>
      <div class="brand-decoration">
        <div class="deco-circle deco-circle-1"></div>
        <div class="deco-circle deco-circle-2"></div>
        <div class="deco-circle deco-circle-3"></div>
      </div>
    </div>
    <div class="entry-view-right">
      <div class="entry-view-form-wrapper">
        <h2 class="form-title">登录</h2>
        <p class="form-subtitle">欢迎回来，请登录您的账号</p>
        <LoginForm />
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.entry-view {
  height: 100%;
  display: flex;

  .entry-view-left {
    flex: 0 0 55%;
    background: linear-gradient(135deg, #5B5FE6 0%, #7C3AED 50%, #5B5FE6 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    overflow: hidden;

    .brand-content {
      position: relative;
      z-index: 1;
      text-align: center;
      color: #fff;
    }

    .brand-logo {
      width: 72px;
      height: 72px;
      margin-bottom: 24px;
    }

    .brand-title {
      font-size: 42px;
      font-weight: 700;
      letter-spacing: 4px;
      margin-bottom: 16px;
    }

    .brand-subtitle {
      font-size: 16px;
      opacity: 0.85;
      max-width: 360px;
    }

    .brand-decoration {
      position: absolute;
      inset: 0;
      pointer-events: none;
    }

    .deco-circle {
      position: absolute;
      border-radius: 50%;
      border: 1px solid rgba(255, 255, 255, 0.1);
    }

    .deco-circle-1 {
      width: 400px;
      height: 400px;
      top: -100px;
      right: -100px;
      background: rgba(255, 255, 255, 0.03);
    }

    .deco-circle-2 {
      width: 300px;
      height: 300px;
      bottom: -80px;
      left: -60px;
      background: rgba(255, 255, 255, 0.04);
    }

    .deco-circle-3 {
      width: 200px;
      height: 200px;
      top: 50%;
      right: 20%;
      background: rgba(255, 255, 255, 0.02);
    }
  }

  .entry-view-right {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--nexus-bg-card);
  }

  .entry-view-form-wrapper {
    width: 360px;

    .form-title {
      font-size: 28px;
      font-weight: 600;
      color: var(--nexus-text-primary);
      margin-bottom: 8px;
    }

    .form-subtitle {
      font-size: 14px;
      color: var(--nexus-text-tertiary);
      margin-bottom: 40px;
    }
  }
}
</style>
