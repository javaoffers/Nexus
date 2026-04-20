import StartNode from './StartNode.vue';
import EndNode from './EndNode.vue';
import NormalNode from './NormalNode.vue';
import ConditionNode from './ConditionNode.vue';
import BranchLabelNode from './BranchLabelNode.vue';

export const nodeTypes = {
  startNode: StartNode,
  endNode: EndNode,
  normalNode: NormalNode,
  conditionNode: ConditionNode,
  branchLabel: BranchLabelNode,
};

export { StartNode, EndNode, NormalNode, ConditionNode, BranchLabelNode };
