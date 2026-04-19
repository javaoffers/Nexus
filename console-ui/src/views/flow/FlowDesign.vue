<script>
import {
  ZoomTool,
  FlowRenderer,
  ElementType,
  AddNodeModal,
  EditNodeDrawer,
  LeftMenu,
  ConditionFilterModal,
} from './design';
import { flowDefineService } from '@/service';
import { ElMessage } from 'element-plus';
import { addNode, deleteNode } from './design/operate';
import { rebuildCondition } from './design/data/generate';

export default {
  components: {
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
      flowRenderer: null,
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
        ElMessage({ type: 'error', message: '流程定义内容解析失败' });
        return;
      }
      var self = this;
      this.flowRenderer = new FlowRenderer(this.$refs.flowCanvas, {
        flowContext: this.flowContext,
        onZoom: function (event) {
          self.scale = event.transform.k;
        },
        onAdd: function (d) {
          console.log(d);
          self.$refs.addNodeModal.open({
            afterSelect: function (info) {
              addNode({ info: info, prev: d.data });
              self.flowRenderer.refresh();
            },
          });
        },
        onEdit: function (d) {
          console.log(d);
          if (d.data.type === ElementType.BRANCH) {
            var data = d.data;
            var parent = data.getParent();
            self.$refs.conditionFilterModalRef.open({
              data: parent.raw,
              index: data.branchIndex,
              afterEdit: function (val) {
                if (val) {
                  self.$store.commit('flow/UPDATE_FLOW_CONTENT', function (state) {
                    var parentRaw = state.flowContent.find(function (item) { return item.key === parent.key; });
                    var branch = parentRaw && parentRaw.conditions ? parentRaw.conditions[data.branchIndex] : null;
                    if (branch) {
                      branch.conditionName = val.conditionName;
                      branch.conditionExpressions = val.conditionExpressions;
                    }
                  });
                  self.flowRenderer.refresh();
                }
              },
            });
          } else if (d.data.type === ElementType.CONDITION) {
            self.$refs.editNodeModal.open({
              data: d.data.raw,
              afterEdit: function (oldData) {
                rebuildCondition(self.flowContext, d.data, oldData);
                self.flowRenderer.refresh();
              },
            });
          } else {
            self.$refs.editNodeModal.open({
              data: d.data.raw,
              afterEdit: function () {
                self.flowRenderer.refresh();
              },
            });
          }
        },
        onDelete: function (d) {
          console.log(d);
          deleteNode({ current: d.data });
          self.flowRenderer.refresh();
        },
      });
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
        ElMessage({ type: 'success', message: '保存成功' });
      } else {
        ElMessage({ type: 'error', message: res.errorMsg });
      }
    },
    onZoomToolChange(value) {
      this.scale = this.flowRenderer.scaleFromTop(value);
    },
  },
};
</script>

<template>
  <div class="page-flow-design">
    <div class="flow-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="'/flow/define'">流程定义</el-breadcrumb-item>
        <el-breadcrumb-item>{{ flowName }}</el-breadcrumb-item>
      </el-breadcrumb>
      <el-button class="flow-submit" type="primary" @click="flowSubmit">保存</el-button>
    </div>
    <div class="flow-canvas" ref="flowCanvas">
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
  height: calc(100% - 40px);
  overflow: hidden;
  position: absolute;
  font-size: 14px;
}
.page-flow-design .flow-canvas .flow-btn {
  cursor: pointer;
  transition: opacity 0.2s;
}
.page-flow-design .flow-canvas .flow-btn:hover {
  opacity: 0.85;
}
.page-flow-design .flow-canvas .flow-btn-edit,
.page-flow-design .flow-canvas .flow-btn-delete {
  display: none;
}
.page-flow-design .flow-canvas .flow-node:hover .flow-btn-edit,
.page-flow-design .flow-canvas .flow-node:hover .flow-btn-delete {
  display: block;
}

.errorMsg {
  position: absolute;
  background-color: white;
  border: 1px solid #d9d9d9;
  border-radius: 5px;
  padding: 5px;
  font-size: 12px;
  z-index: 100;
  display: none;
}
</style>
