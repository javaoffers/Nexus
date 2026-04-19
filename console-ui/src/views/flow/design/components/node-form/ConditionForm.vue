<script>
import { Delete, Edit, Top, Bottom } from '@element-plus/icons-vue';
import ConditionFilterModal from '../ConditionFilterModal.vue';
import { ElementType } from '../../types';
import { cloneDeep } from 'lodash-es';
import { ElMessage } from 'element-plus';

function getDefaultData() {
  return {
    key: '',
    name: '',
    outgoings: [],
    incomings: [],
    elementType: ElementType.CONDITION,
    desc: '',
    conditions: [],
  };
}

export default {
  components: {
    Delete,
    Edit,
    Top,
    Bottom,
    ConditionFilterModal,
  },
  props: {
    data: {
      type: Object,
      required: true,
    },
  },
  emits: ['update', 'cancel'],
  data() {
    return {
      nodeData: getDefaultData(),
    };
  },
  watch: {
    data: {
      handler(val) {
        if (val !== this.nodeData) {
          this.nodeData = Object.assign(getDefaultData(), cloneDeep(val));
        }
        var elseItemIndex = this.nodeData.conditions ? this.nodeData.conditions.findIndex(function (item) { return item.conditionType === 'DEFAULT'; }) : -1;
        var elseItem = this.nodeData.conditions ? this.nodeData.conditions[elseItemIndex] : null;
        if (elseItemIndex > -1 && elseItemIndex !== this.nodeData.conditions.length - 1) {
          this.nodeData.conditions.splice(elseItemIndex, 1);
          this.nodeData.conditions.push(elseItem);
        }
      },
      immediate: true,
    },
  },
  methods: {
    validate() {
      if (!this.nodeData.name) {
        ElMessage.error('节点名称不能为空');
        return false;
      }
      return true;
    },
    edit(index) {
      var self = this;
      this.$refs.conditionFilterModalRef.open({
        data: self.nodeData,
        index: index,
        afterEdit: function (val) {
          if (val) {
            if (!val.outgoing) {
              val.outgoing = self.data.outgoings[0];
            }
            self.nodeData.conditions.splice(index, 1, val);
          }
        },
      });
    },
    sortUp(index) {
      var current = this.nodeData.conditions[index];
      if (index === 0) {
        return;
      }
      this.nodeData.conditions.splice(index, 1);
      this.nodeData.conditions.splice(index - 1, 0, current);
    },
    sortDown(index) {
      if (index === this.nodeData.conditions.length - 1) {
        return;
      }
      var current = this.nodeData.conditions[index];
      this.nodeData.conditions.splice(index, 1);
      this.nodeData.conditions.splice(index + 1, 0, current);
    },
    remove(index) {
      this.nodeData.conditions.splice(index, 1);
    },
    addCondition() {
      var self = this;
      this.$refs.conditionFilterModalRef.open({
        data: self.nodeData,
        afterEdit: function (val) {
          if (val) {
            if (!val.outgoing) {
              val.outgoing = self.data.outgoings[0];
            }
            var conditionSize = self.nodeData.conditions.length;
            self.nodeData.conditions.splice(conditionSize - 1, 0, val);
          }
        },
      });
    },
    onSubmit() {
      if (!this.validate()) {
        return;
      }
      this.$emit('update', cloneDeep(this.nodeData));
    },
    onCancel() {
      this.$emit('cancel');
    },
  },
};
</script>

<template>
  <div class="node-condition-form">
    <el-form label-position="top">
      <el-form-item label="节点编码">
        <span>{{ nodeData.key }}</span>
      </el-form-item>
      <el-form-item label="节点名称" required>
        <el-input v-model="nodeData.name" maxlength="16" placeholder="请输入"></el-input>
      </el-form-item>
      <el-form-item label="节点描述">
        <el-input v-model="nodeData.desc" maxlength="60" placeholder="请输入" :rows="2" type="textarea"></el-input>
      </el-form-item>
      <el-form-item label="条件表达式">
        <div class="condition-list">
          <div class="condition-item" v-for="(item, index) in nodeData.conditions" :key="index">
            <div class="condition-head">
              <div class="condition-title">{{ item.conditionName }}</div>
              <div class="condition-action">
                <template v-if="item.conditionType === 'CUSTOM'">
                  <el-button link :icon="Top" v-if="index > 0" @click="sortUp(index)" title="升序"></el-button>
                  <el-button link :icon="Bottom" v-if="index < nodeData.conditions.length - 2" @click="sortDown(index)" title="降序"></el-button>
                  <el-button link :icon="Edit" @click="edit(index)" title="编辑"></el-button>
                  <el-button link :icon="Delete" @click="remove(index)" title="删除"></el-button>
                </template>
              </div>
            </div>
            <div class="condition-body" v-if="item.expression">{{ item.expression }}</div>
          </div>
          <div class="condition-add">
            <el-button size="small" type="info" @click="addCondition">新增分支</el-button>
          </div>
        </div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">确定</el-button>
        <el-button @click="onCancel">取消</el-button>
      </el-form-item>
    </el-form>
    <ConditionFilterModal ref="conditionFilterModalRef" />
  </div>
</template>

<style scoped>
.node-condition-form .condition-list {
  width: 100%;
}
.node-condition-form .condition-list .condition-item {
  padding: 5px 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin-bottom: 10px;
}
.node-condition-form .condition-list .condition-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.node-condition-form .condition-list .condition-head .condition-title {
  font-size: 14px;
}
.node-condition-form .condition-list .condition-head .condition-action .el-button {
  padding: 0;
  width: 30px;
  height: 30px;
  line-height: 30px;
  border-radius: 50%;
  font-size: 14px;
}
.node-condition-form .condition-list .condition-head .condition-action .el-button + .el-button {
  margin-left: 4px;
}
.node-condition-form .condition-list .condition-body {
  border-top: 1px solid #ccc;
  margin-top: 4px;
}
.node-condition-form .condition-list .condition-add {
  text-align: center;
}
</style>
