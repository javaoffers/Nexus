<script>
import { DataTypeMap } from '@/const';
import { commonService } from '@/service';

export default {
  props: {
    dataType: Object,
  },
  data() {
    return {
      objectDataTypeMap: {},
    };
  },
  computed: {
    displayName() {
      return this.getDataTypeDisplayName(this.dataType);
    },
  },
  mounted() {
    this.loadDataTypeList();
  },
  methods: {
    getDataTypeDisplayName(dataType) {
      let displayName;
      if (!dataType) {
        return;
      }
      if ('Object' == dataType.type) {
        displayName = this.objectDataTypeMap[dataType.objectKey];
      } else if ('List' == dataType.type) {
        const start = this.$t('dataTypeLabel.listPrefix');
        let itemDisplayName;
        if (dataType.itemType) {
          itemDisplayName = this.getDataTypeDisplayName({
            type: dataType.itemType,
            objectKey: dataType.objectKey,
            objectStructure: dataType.objectStructure,
          });
        }
        displayName = start + itemDisplayName + '>';
      } else {
        displayName = DataTypeMap[dataType.type];
      }
      return displayName;
    },
    async loadDataTypeList() {
      const res = await commonService.dataType.getList();
      const dataTypeList = res || [];
      this.objectDataTypeMap = dataTypeList.filter(item => item.dataTypeClassify === 3).reduce((acc, item) => {
        if (item.objectKey) {
          acc[item.objectKey] = item.displayName;
        }
        return acc;
      }, {});
    },
  },
};
</script>

<template>
<div :title="displayName" class="dataTypeName">{{displayName}}</div>
</template>

<style scoped>
.dataTypeName {
  width: 110px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
