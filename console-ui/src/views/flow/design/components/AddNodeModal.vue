<script>
import { ElementType } from '../types';
import IconMethod from '@/components/icons/IconMethod.vue';
import IconCondition from '@/components/icons/IconCondition.vue';
import IconCode from '@/components/icons/IconCode.vue';
import IconMysql from '@/components/icons/IconMysql.vue';
import IconAssign from '@/components/icons/IconAssign.vue';

export default {
  components: {
    IconMethod,
    IconCondition,
    IconCode,
    IconMysql,
    IconAssign,
  },
  data() {
    return {
      visible: false,
      openParams: null,
      flowDataNodes: [],
      flowOtherNodes: [],
    };
  },
  computed: {
    flowNodes() {
      return [
        {
          name: this.$t('design.methodNode'),
          type: ElementType.METHOD,
          icon: IconMethod,
        },
        {
          name: this.$t('design.conditionNode'),
          type: ElementType.CONDITION,
          icon: IconCondition,
        },
        {
          name: this.$t('design.assignNode'),
          type: ElementType.ASSIGN,
          icon: IconAssign,
        },
        {
          name: this.$t('design.codeNode'),
          type: ElementType.CODE,
          icon: IconCode,
        },
      ];
    },
  },
  methods: {
    open(params) {
      this.visible = true;
      this.openParams = params;
    },
    handleClick(e) {
      e.preventDefault();
    },
    addNode(item) {
      var info = {
        name: item.name,
        elementType: item.type,
      };
      if (typeof this.openParams.afterSelect === 'function') {
        this.openParams.afterSelect(info);
      }
      this.visible = false;
    },
  },
};
</script>

<template>
  <el-dialog v-model="visible" title="" class="design-add-node-modal" :width="500" :show-close="false" align-center>
    <el-row>
      <el-col>
        <div style="height: 300px; overflow-y: auto; padding: 16px;">
          <div class="node-types">
            <div class="node-type" v-for="item in flowNodes" :key="item.type" @click="addNode(item)">
              <span class="node-icon"><el-icon :size="25"><component :is="item.icon"></component></el-icon></span>
              <span class="node-text">{{ item.name }}</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </el-dialog>
</template>

<style>
.design-add-node-modal .el-dialog__header {
  padding: 0;
  width: 100%;
  position: absolute;
  z-index: 1;
}
.design-add-node-modal .el-dialog__headerbtn {
  height: 40px;
  width: 40px;
  top: 0;
}
.design-add-node-modal .el-dialog__body {
  padding: 0;
}
.design-add-node-modal .el-anchor__link {
  font-size: 15px;
}
.design-add-node-modal .node-types {
  padding: 8px 0;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.design-add-node-modal .node-type-name {
  font-size: 18px;
  margin-bottom: 10px;
}
.design-add-node-modal .node-type {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 448px;
  margin-bottom: 8px;
  font-size: 14px;
  cursor: pointer;
  border: 1px solid var(--nexus-border-light);
  border-radius: var(--nexus-border-radius-sm);
  transition: all 0.2s;
}
.design-add-node-modal .node-type:hover {
  background-color: var(--nexus-primary-light-9);
  border-color: var(--nexus-primary-light-5);
}
.design-add-node-modal .node-type .node-icon {
  width: 25px;
  height: 25px;
}
.design-add-node-modal .node-text {
  margin: 10px 5px;
}
</style>
