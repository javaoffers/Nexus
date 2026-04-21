<script>
export default {
  computed: {
    currentLang() {
      return this.$i18n.locale;
    },
  },
  methods: {
    toggleLang() {
      const newLang = this.$i18n.locale === 'zh-CN' ? 'en' : 'zh-CN';
      this.$i18n.locale = newLang;
      localStorage.setItem('nexus-lang', newLang);
    },
  },
};
</script>

<template>
  <div class="lang-switch" @click="toggleLang" title="Switch Language">
    <span class="lang-label">{{ currentLang === 'zh-CN' ? 'EN' : '中' }}</span>
  </div>
</template>

<style scoped>
.lang-switch {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 6px;
  cursor: pointer;
  margin-right: 8px;
  transition: background-color 0.2s;
  user-select: none;
}

.lang-switch:hover {
  background-color: var(--nexus-primary-light-9);
}

.lang-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--nexus-text-secondary);
}

.lang-switch:hover .lang-label {
  color: var(--nexus-primary);
}
</style>
