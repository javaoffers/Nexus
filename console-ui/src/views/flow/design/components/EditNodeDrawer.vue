<script>
import { getNodeForm } from './node-form';
import ResizableDrawer from '@/components/common/ResizableDrawer.vue';

export default {
  components: {
    ResizableDrawer,
  },
  data() {
    return {
      visible: false,
      openParams: null,
      currentNodeForm: null,
      currentData: null,
    };
  },
  methods: {
    open(params) {
      this.visible = true;
      this.openParams = params;
      this.currentNodeForm = getNodeForm(params.data.elementType);
      this.currentData = params.data;
    },
    onUpdate(val) {
      this.$store.commit('flow/UPDATE_FLOW_CONTENT', function (state) {
        var index = state.flowContent.findIndex(function (item) { return item.key === val.key; });
        if (index > -1) {
          Object.assign(state.flowContent[index], val);
        }
      });
      this.visible = false;
      this.openParams.afterEdit(this.currentData);
    },
    onClosed() {
      this.currentNodeForm = null;
    },
  },
};
</script>

<template>
  <ResizableDrawer v-model="visible" :size="560" :title="currentData?.name" @closed="onClosed" drawer-key="FLOW_NODE">
    <component :is="currentNodeForm" :data="currentData" @update="onUpdate" @cancel="visible = false" />
  </ResizableDrawer>
</template>
