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
        ElMessage.error(this.$t('design.nodeNameRequired'));
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
      <el-form-item :label="$t('design.nodeCode')">
        <span>{{ nodeData.key }}</span>
      </el-form-item>
      <el-form-item :label="$t('design.nodeName')" required>
        <el-input v-model="nodeData.name" maxlength="16" :placeholder="$t('common.pleaseInput')"></el-input>
      </el-form-item>
      <el-form-item :label="$t('design.nodeDesc')">
        <el-input v-model="nodeData.desc" maxlength="60" :placeholder="$t('common.pleaseInput')" :rows="2" type="textarea"></el-input>
      </el-form-item>
      <el-form-item :label="$t('design.conditionExpr')">
        <div class="condition-list">
          <div class="condition-item" v-for="(item, index) in nodeData.conditions" :key="index">
            <div class="condition-head">
              <div class="condition-title">{{ item.conditionName }}</div>
              <div class="condition-action">
                <template v-if="item.conditionType === 'CUSTOM'">
                  <el-button link :icon="Top" v-if="index > 0" @click="sortUp(index)" :title="$t('design.ascending')"></el-button>
                  <el-button link :icon="Bottom" v-if="index < nodeData.conditions.length - 2" @click="sortDown(index)" :title="$t('design.descending')"></el-button>
                  <el-button link :icon="Edit" @click="edit(index)" :title="$t('common.edit')"></el-button>
                  <el-button link :icon="Delete" @click="remove(index)" :title="$t('common.delete')"></el-button>
                </template>
              </div>
            </div>
            <div class="condition-body" v-if="item.expression">{{ item.expression }}</div>
          </div>
          <div class="condition-add">
            <el-button size="small" type="info" @click="addCondition">{{ $t('design.addBranch') }}</el-button>
          </div>
        </div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">{{ $t('common.confirm') }}</el-button>
        <el-button @click="onCancel">{{ $t('common.cancel') }}</el-button>
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
