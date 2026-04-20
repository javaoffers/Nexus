<template>
  <div class="app-logo">
    <svg class="logo-icon" viewBox="0 0 32 32" xmlns="http://www.w3.org/2000/svg">
      <rect width="32" height="32" rx="6" fill="#5b5fe6"/>
      <path d="M8 24V8h3.5l9 12V8H24v16h-3.5l-9-12v12H8z" fill="#fff"/>
    </svg>
    NEXUS
  </div>
</template>
<style scoped>
.app-logo {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 2px;
  color: var(--nexus-primary);
  padding: 0 24px;
  width: var(--nexus-sidebar-width);
  border-right: 1px solid var(--nexus-border-light);
}
.app-logo .logo-icon {
  width: 36px;
  height: 36px;
  margin-right: 8px;
}
</style>
