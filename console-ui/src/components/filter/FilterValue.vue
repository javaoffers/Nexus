<script>
export default {
  props: {
    modelValue: {
      type: [String, Number, Boolean, Array, Object],
      default: '',
    },
    dataType: Object,
    size: {
      type: String,
      default: 'default',
    },
    showNumberControls: {
      type: Boolean,
      default: true,
    },
  },
  emits: ['update:modelValue'],
  computed: {
    currentType() {
      return this.dataType?.type;
    },
    innerValue: {
      get() {
        if (this.currentType === 'Integer' || this.currentType === 'Double') {
          if (this.modelValue === '') {
            return null;
          }
          return Number(this.modelValue);
        }
        return this.modelValue;
      },
      set(value) {
        this.$emit('update:modelValue', value);
      },
    },
    switchValue: {
      get() {
        return this.innerValue === true;
      },
      set(newValue) {
        this.innerValue = newValue;
      },
    },
  },
};
</script>

<template>
  <div class="filter-value">
    <el-input-number
      v-if="currentType === 'Integer'"
      v-model="innerValue"
      :controls="showNumberControls"
      controls-position="right"
      :max="9999999999999"
      :precision="0"
      :size="size"
      :placeholder="$t('common.pleaseInput')"
    />
    <el-input-number
      v-else-if="currentType === 'Double'"
      v-model="innerValue"
      :controls="showNumberControls"
      controls-position="right"
      :max="9999999999999"
      :precision="2"
      :size="size"
      :placeholder="$t('common.pleaseInput')"
    />
    <el-date-picker
        v-else-if="currentType === 'Date'"
        v-model="innerValue"
        :size="size"
        type="date"
        value-format="YYYY-MM-DD" />
    <el-date-picker
        v-else-if="currentType === 'Time'"
        v-model="innerValue"
        :size="size"
        type="datetime"
        value-format="YYYY-MM-DD HH:mm:ss"/>
    <el-switch
      v-else-if="currentType === 'Boolean'"
      v-model="switchValue"
      inline-prompt
      :active-text="$t('common.yes')"
      :inactive-text="$t('common.no')"
      :size="size"
      :width="48"
    />
    <el-input v-else v-model="innerValue" :size="size" :placeholder="$t('common.pleaseInput')" />
  </div>
</template>

<style scoped>
.filter-value {
  display: flex;
}
.filter-value :deep(.el-input-number) {
  width: 100%;
}
.filter-value :deep(.el-input-number) .el-input__inner {
  text-align: left;
}
.filter-value :deep(.el-date-editor) {
  width: 100%;
}
</style>
