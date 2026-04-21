<script>
import { VueFlow } from '@vue-flow/core';
import { Background } from '@vue-flow/background';
import '@vue-flow/core/dist/style.css';
import '@vue-flow/core/dist/theme-default.css';
import { markRaw } from 'vue';
import StartNode from './design/components/flow-nodes/StartNode.vue';
import EndNode from './design/components/flow-nodes/EndNode.vue';
import NormalNode from './design/components/flow-nodes/NormalNode.vue';
import ConditionNode from './design/components/flow-nodes/ConditionNode.vue';
import BranchLabelNode from './design/components/flow-nodes/BranchLabelNode.vue';
import { computeFlowLayout } from './design/layout/flowLayoutAdapter';
import { generateDataTree, rebuildCondition } from './design/data/generate';//cmj
import { addNode, deleteNode } from './design/operate';
import { ElementType } from './design/types';
import {
  ZoomTool,
  AddNodeModal,
  EditNodeDrawer,
  LeftMenu,
  ConditionFilterModal,
} from './design';
import { flowDefineService } from '@/service';
import { ElMessage } from 'element-plus';

// Invisible merge-point component
const MergePoint = markRaw({
  template: '<div style="width:2px;height:2px;"></div>',
});

export default {
  components: {
    VueFlow,
    Background,
    StartNode,
    EndNode,
    NormalNode,
    ConditionNode,
    BranchLabelNode,
    ZoomTool,
    AddNodeModal,
    EditNodeDrawer,
    LeftMenu,
    ConditionFilterModal,
  },
  data() {
    return {
      flowName: '',
      scale: 1,
      nodes: [],
      edges: [],
      nodeTypes: {
        startNode: markRaw(StartNode),
        endNode: markRaw(EndNode),
        normalNode: markRaw(NormalNode),
        conditionNode: markRaw(ConditionNode),
        branchLabel: markRaw(BranchLabelNode),
        mergePoint: MergePoint,
      },
      dataRoot: null,
    };
  },
  computed: {
    flowContext() {
      var self = this;
      return {
        data: { value: this.$store.state.flow },
        update: function (updater) { self.$store.commit('flow/UPDATE_FLOW_CONTENT', updater); },
        getFlowNode: function (key) { return self.$store.getters['flow/getFlowNode'](key); },
        getFlowNodes: function () { return self.$store.getters['flow/getFlowNodes']; },
        updateFlowNode: function (key, data) { self.$store.commit('flow/UPDATE_FLOW_NODE', { key: key, data: data }); },
      };
    },
  },
  mounted() {
    this.init();
  },
  methods: {
    async init() {
      try {
        await this.queryFlowDefineInfo();
      } catch (error) {
        ElMessage({ type: 'error', message: this.$t('flow.parseError') });
        return;
      }
      this.refreshFlow();
    },
    refreshFlow() {
      this.dataRoot = generateDataTree(this.flowContext);
      const layout = computeFlowLayout(this.dataRoot);
      // Inject callbacks into each node's data
      var self = this;
      layout.nodes.forEach(function (node) {
        node.data.onAdd = function (dataNode) { self.onAdd(dataNode); };
        node.data.onEdit = function (dataNode) { self.onEdit(dataNode); };
        node.data.onDelete = function (dataNode) { self.onDelete(dataNode); };
        node.data.onEditBranch = function (dataNode) { self.onEditBranch(dataNode); };
      });
      this.nodes = layout.nodes;
      this.edges = layout.edges;
    },
    async queryFlowDefineInfo() {
      var self = this;
      var res = await flowDefineService.getDefineInfo(this.$route.params.flowDefinitionId);
      if (res.success) {
        self.flowName = res.result.flowName;
        self.$store.commit('flow/UPDATE_FLOW_CONTENT', function (state) {
          Object.assign(state, res.result);
          state.flowContent = JSON.parse(res.result.flowContent);
        });
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    onAdd(dataNode) {
      var self = this;
      this.$refs.addNodeModal.open({
        afterSelect: function (info) {
          addNode({ info: info, prev: dataNode });
          self.refreshFlow();
        },
      });
    },
    onEdit(dataNode) {
      var self = this;
      if (dataNode.type === ElementType.CONDITION) {
        this.$refs.editNodeModal.open({
          data: dataNode.raw,
          afterEdit: function (oldData) {
            rebuildCondition(self.flowContext, dataNode, oldData);
            self.refreshFlow();
          },
        });
      } else {
        this.$refs.editNodeModal.open({
          data: dataNode.raw,
          afterEdit: function () {
            self.refreshFlow();
          },
        });
      }
    },
    onEditBranch(dataNode) {
      var self = this;
      var parent = dataNode.getParent();
      this.$refs.conditionFilterModalRef.open({
        data: parent.raw,
        index: dataNode.branchIndex,
        afterEdit: function (val) {
          if (val) {
            self.$store.commit('flow/UPDATE_FLOW_CONTENT', function (state) {
              var parentRaw = state.flowContent.find(function (item) { return item.key === parent.key; });
              var branch = parentRaw && parentRaw.conditions ? parentRaw.conditions[dataNode.branchIndex] : null;
              if (branch) {
                branch.conditionName = val.conditionName;
                branch.conditionExpressions = val.conditionExpressions;
              }
            });
            self.refreshFlow();
          }
        },
      });
    },
    onDelete(dataNode) {
      deleteNode({ current: dataNode });
      this.refreshFlow();
    },
    onViewportChange(event) {
      if (event && event.zoom !== undefined) {
        this.scale = event.zoom;
      }
    },
    onZoomToolChange(value) {
      const vf = this.$refs.vueFlowRef;
      if (vf && vf.zoomTo) {
        vf.zoomTo(value);
        this.scale = value;
      }
    },
    flowSubmit() {
      var flowContent = this.$store.state.flow.flowContent;
      var flowVariables = this.$store.state.flow.flowVariables;
      this.saveFlowDefineContent(JSON.stringify(flowContent), flowVariables);
    },
    async saveFlowDefineContent(flowContent, flowVariables) {
      var res = await flowDefineService.saveFlowContent({
        id: this.$route.params.flowDefinitionId,
        flowContent: flowContent,
        flowVariables: flowVariables,
      });
      if (res.success) {
        ElMessage({ type: 'success', message: this.$t('common.saveSuccess') });
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
  },
};
</script>

<template>
  <div class="page-flow-design">
    <div class="flow-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="'/flow/define'">{{ $t('flow.flowDefinition') }}</el-breadcrumb-item>
        <el-breadcrumb-item>{{ flowName }}</el-breadcrumb-item>
      </el-breadcrumb>
      <el-button class="flow-submit" type="primary" @click="flowSubmit">{{ $t('common.save') }}</el-button>
    </div>
    <div class="flow-canvas">
      <VueFlow
        ref="vueFlowRef"
        :nodes="nodes"
        :edges="edges"
        :node-types="nodeTypes"
        :default-viewport="{ x: 0, y: 0, zoom: 1 }"
        :min-zoom="0.5"
        :max-zoom="2"
        :nodes-draggable="false"
        :nodes-connectable="false"
        :edges-updatable="false"
        :fit-view-on-init="true"
        @viewport-change="onViewportChange"
      >
        <Background :gap="20" :size="1" pattern-color="#e0e0e0" />
      </VueFlow>
      <ZoomTool :scale="scale" @change="onZoomToolChange" />
    </div>
    <LeftMenu />
    <AddNodeModal ref="addNodeModal" />
    <EditNodeDrawer ref="editNodeModal" />
    <ConditionFilterModal ref="conditionFilterModalRef" />
  </div>
</template>

<style>
.layout-view .layout-router-view.page-flow-design {
  background-color: #fff;
  overflow: hidden !important;
}

.flow-header {
  border-bottom: 1px solid var(--nexus-border-light);
  position: relative;
  height: 50px;
  align-items: center;
  display: flex;
  z-index: 10;
}
.flow-header .el-breadcrumb {
  margin-left: 10px;
}
.flow-header .flow-submit {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translate(-50%, -50%);
}

.page-flow-design .flow-canvas {
  width: 100%;
  height: calc(100% - 50px);
  position: absolute;
  top: 50px;
  left: 0;
}

/* Vue Flow container must have explicit dimensions */
.page-flow-design .flow-canvas .vue-flow {
  width: 100%;
  height: 100%;
}

/* Remove default Vue Flow node styling for our custom nodes */
.page-flow-design .vue-flow__node {
  cursor: default;
  border: none !important;
  box-shadow: none !important;
  background: transparent !important;
  padding: 0 !important;
  border-radius: 0 !important;
}

/* Edge styling */
.page-flow-design .vue-flow__edge-path {
  stroke: #b1b5bb;
  stroke-width: 1.5;
}
</style>
