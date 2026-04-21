<script>
export default {
  props: {
    data: {
      type: Array,
      default: () => [],
    },
    defaultValues: {
      type: Object,
      default: () => ({}),
    },
  },
  emits: ['change'],
  data() {
    return {
      selected: {},
    };
  },
  watch: {
    defaultValues: {
      handler() {
        this.data.forEach(item => {
          if (item.multiple) this.selected[item.key] = this.defaultValues[item.key] ?? [''];
          else this.selected[item.key] = this.defaultValues[item.key] ?? '';
        });
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    select(item, option) {
      if (!item.multiple) {
        if (this.selected[item.key] !== option.value) this.selected[item.key] = option.value;
      } else {
        if (item.options[0].value === option.value) this.selected[item.key] = [option.value];
        if (this.selected[item.key].includes(option.value)) {
          let currentIndex = this.selected[item.key].findIndex((s) => s === option.value);
          this.selected[item.key].splice(currentIndex, 1);
          if (this.selected[item.key].length == 0) this.selected[item.key] = [item.options[0].value];
        } else {
          this.selected[item.key].push(option.value);
          if (this.selected[item.key].includes(item.options[0].value)) this.selected[item.key].splice(0, 1);
        }
      }
      this.$emit('change', this.selected);
    },
  },
};
</script>

<template>
  <div class="classify-filter">
    <div v-for="item in data" :key="item.key" class="classify-filter-item">
      <div class="classify-filter-item-title">
        <span>{{ item.title }} :</span>
      </div>
      <span v-if="!item.options.length" class="classify-filter-notData">{{ $t('common.noData') }}</span>
      <el-scrollbar>
        <ul class="classify-filter-list">
          <li
              v-for="option in item.options"
              :key="option.value"
              :class="{
              active:
                option.value === selected[item.key] ||
                (Array.isArray(selected[item.key]) && selected[item.key].includes(option.value))
            }"
              @click="select(item, option)"
          >
            <slot :row="option">
              <el-icon v-if="option.icon">
                <component :is="option.icon" />
              </el-icon>
              <span>{{ option.label }}</span>
            </slot>
          </li>
        </ul>
      </el-scrollbar>
    </div>
  </div>
</template>

<style scoped>
.classify-filter {
  width: 100%;
}
.classify-filter .classify-filter-item {
  display: flex;
  align-items: center;
  border-bottom: 1px dashed var(--el-border-color-light);
}
.classify-filter .classify-filter-item:last-child {
  border-bottom: none;
}
.classify-filter .classify-filter-item .classify-filter-item-title {
  margin-top: -2px;
}
.classify-filter .classify-filter-item .classify-filter-item-title span {
  font-size: 14px;
  color: var(--el-text-color-regular);
  white-space: nowrap;
}
.classify-filter .classify-filter-item .classify-filter-notData {
  margin: 18px 0;
  font-size: 14px;
  color: var(--el-text-color-regular);
}
.classify-filter .classify-filter-item .classify-filter-list {
  display: flex;
  flex: 1;
  padding: 0;
  margin: 13px 0;
}
.classify-filter .classify-filter-item .classify-filter-list li {
  display: flex;
  align-items: center;
  padding: 5px 15px;
  margin-right: 16px;
  font-size: 13px;
  color: var(--el-color-primary);
  list-style: none;
  cursor: pointer;
  background: var(--el-color-primary-light-9);
  border: 1px solid var(--el-color-primary-light-5);
  border-radius: 32px;
}
.classify-filter .classify-filter-item .classify-filter-list li:hover {
  color: #ffffff;
  background: var(--el-color-primary);
  border-color: var(--el-color-primary);
  transition: 0.1s;
}
.classify-filter .classify-filter-item .classify-filter-list li.active {
  font-weight: bold;
  color: #ffffff;
  background: var(--el-color-primary);
  border-color: var(--el-color-primary);
}
.classify-filter .classify-filter-item .classify-filter-list li .el-icon {
  margin-right: 4px;
  font-size: 16px;
  font-weight: bold;
}
.classify-filter .classify-filter-item .classify-filter-list li span {
  white-space: nowrap;
}
</style>
