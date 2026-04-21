<script>
import LoginForm from '@/components/entry/LoginForm.vue';

export default {
  components: {
    LoginForm,
  },
};
</script>

<template>
  <div class="entry-view">
    <div class="entry-view-left">
      <div class="brand-content">
        <svg class="brand-logo" viewBox="0 0 32 32" xmlns="http://www.w3.org/2000/svg">
          <rect width="32" height="32" rx="6" fill="rgba(255,255,255,0.15)"/>
          <path d="M8 24V8h3.5l9 12V8H24v16h-3.5l-9-12v12H8z" fill="#fff"/>
        </svg>
        <h1 class="brand-title">NEXUS</h1>
        <p class="brand-subtitle">{{ $t('brand.subtitle') }}</p>
      </div>
      <div class="brand-decoration">
        <div class="deco-circle deco-circle-1"></div>
        <div class="deco-circle deco-circle-2"></div>
        <div class="deco-circle deco-circle-3"></div>
      </div>
    </div>
    <div class="entry-view-right">
      <div class="entry-view-form-wrapper">
        <h2 class="form-title">{{ $t('login.title') }}</h2>
        <p class="form-subtitle">{{ $t('login.subtitle') }}</p>
        <LoginForm />
      </div>
    </div>
  </div>
</template>

<style scoped>
.entry-view {
  height: 100%;
  display: flex;
}

.entry-view .entry-view-left {
  flex: 0 0 55%;
  background: linear-gradient(135deg, #5B5FE6 0%, #7C3AED 50%, #5B5FE6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.entry-view .entry-view-left .brand-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #fff;
}

.entry-view .entry-view-left .brand-logo {
  width: 72px;
  height: 72px;
  margin-bottom: 24px;
}

.entry-view .entry-view-left .brand-title {
  font-size: 42px;
  font-weight: 700;
  letter-spacing: 4px;
  margin-bottom: 16px;
}

.entry-view .entry-view-left .brand-subtitle {
  font-size: 16px;
  opacity: 0.85;
  max-width: 360px;
}

.entry-view .entry-view-left .brand-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.entry-view .entry-view-left .deco-circle {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.entry-view .entry-view-left .deco-circle-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  right: -100px;
  background: rgba(255, 255, 255, 0.03);
}

.entry-view .entry-view-left .deco-circle-2 {
  width: 300px;
  height: 300px;
  bottom: -80px;
  left: -60px;
  background: rgba(255, 255, 255, 0.04);
}

.entry-view .entry-view-left .deco-circle-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  right: 20%;
  background: rgba(255, 255, 255, 0.02);
}

.entry-view .entry-view-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--nexus-bg-card);
}

.entry-view .entry-view-form-wrapper {
  width: 360px;
}

.entry-view .entry-view-form-wrapper .form-title {
  font-size: 28px;
  font-weight: 600;
  color: var(--nexus-text-primary);
  margin-bottom: 8px;
}

.entry-view .entry-view-form-wrapper .form-subtitle {
  font-size: 14px;
  color: var(--nexus-text-tertiary);
  margin-bottom: 40px;
}
</style>
