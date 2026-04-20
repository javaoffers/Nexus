import { ElementType } from '../types';
import { nodeMap } from '../config';
import { MarkerType } from '@vue-flow/core';

const box = {
  width: 220,
  height: 56,
  marginBottom: 100,
  marginRight: 80,
  startSize: 56,
  branchLabelHeight: 36,
};

const heightAndMargin = box.height + box.marginBottom;
const branchTop = box.height + box.marginBottom * 0.6;

/**
 * Convert a DataBranch tree into Vue Flow nodes[] and edges[].
 * @param {DataBranch} dataRoot - root branch from generateDataTree()
 * @returns {{ nodes: Array, edges: Array }}
 */
export function computeFlowLayout(dataRoot) {
  const nodes = [];
  const edges = [];
  // Layout the tree starting from root, centered at x=0
  layoutBranch(dataRoot, 0, 0, nodes, edges);
  // Center all nodes: find min x and shift everything so the leftmost node is at x=40
  if (nodes.length > 0) {
    const minX = Math.min(...nodes.map(n => n.position.x));
    const offsetX = -minX + 40;
    nodes.forEach(n => {
      n.position.x += offsetX;
    });
  }
  return { nodes, edges };
}

function layoutBranch(branch, startX, startY, nodes, edges) {
  const children = branch.getChildren();
  let prevKey = null;
  let prevBottom = startY;
  let branchWidth = box.width;

  children.forEach((child) => {
    if (child.type === ElementType.CONDITION) {
      const result = layoutCondition(child, startX, prevBottom, nodes, edges);
      if (prevKey) {
        addEdge(edges, prevKey, child.key);
      }
      prevKey = '__merge_' + child.key;
      prevBottom = result.bottom;
      branchWidth = Math.max(branchWidth, result.width);
    } else {
      const nodeResult = layoutNormalNode(child, startX, prevBottom, nodes);
      if (prevKey) {
        addEdge(edges, prevKey, child.key);
      }
      prevKey = child.key;
      prevBottom = nodeResult.bottom;
    }
  });

  return { bottom: prevBottom, width: branchWidth, lastKey: prevKey };
}

function layoutNormalNode(dataNode, centerX, topY, nodes) {
  const isTerminal = [ElementType.START, ElementType.END].includes(dataNode.type);
  const w = isTerminal ? box.startSize : box.width;
  const h = isTerminal ? box.startSize : box.height;

  let vfType = 'normalNode';
  if (dataNode.type === ElementType.START) vfType = 'startNode';
  else if (dataNode.type === ElementType.END) vfType = 'endNode';

  nodes.push({
    id: dataNode.key,
    type: vfType,
    position: { x: centerX - w / 2, y: topY },
    data: {
      dataNode,
      nodeConfig: nodeMap[dataNode.type] || {},
    },
  });

  return { bottom: topY + h + box.marginBottom };
}

function layoutCondition(conditionNode, centerX, topY, nodes, edges) {
  // Place the condition node itself
  nodes.push({
    id: conditionNode.key,
    type: 'conditionNode',
    position: { x: centerX - box.width / 2, y: topY },
    data: {
      dataNode: conditionNode,
      nodeConfig: nodeMap[ElementType.CONDITION] || {},
    },
  });

  const branches = conditionNode.getChildren();
  const branchCount = branches.length;

  // First pass: compute each branch's content to get widths
  const branchLayouts = [];
  branches.forEach((branch) => {
    const tempNodes = [];
    const tempEdges = [];
    const result = layoutBranch(branch, 0, 0, tempNodes, tempEdges);
    branchLayouts.push({
      branch,
      tempNodes,
      tempEdges,
      width: Math.max(result.width, box.width),
      bottom: result.bottom,
      lastKey: result.lastKey,
    });
  });

  // Compute total width and position each branch
  const totalWidth = branchLayouts.reduce((sum, bl) => sum + bl.width, 0) + (branchCount - 1) * box.marginRight;
  let currentX = centerX - totalWidth / 2;

  const branchLabelY = topY + branchTop;
  const branchContentY = branchLabelY + box.branchLabelHeight + box.marginBottom * 0.5;

  let maxBottom = branchContentY;

  branchLayouts.forEach((bl) => {
    const branchCenterX = currentX + bl.width / 2;
    const branchLabelId = bl.branch.key;

    // Place branch label node
    nodes.push({
      id: branchLabelId,
      type: 'branchLabel',
      position: { x: branchCenterX - 70, y: branchLabelY },
      data: {
        dataNode: bl.branch,
      },
    });

    // Edge: condition -> branch label
    addEdge(edges, conditionNode.key, branchLabelId);

    // Offset temp nodes to correct position
    bl.tempNodes.forEach(n => {
      n.position.x += branchCenterX;
      n.position.y += branchContentY;
      nodes.push(n);
    });
    bl.tempEdges.forEach(e => edges.push(e));

    // Edge: branch label -> first child (or merge point if empty)
    const branchChildren = bl.branch.getChildren();
    if (branchChildren.length > 0) {
      addEdge(edges, branchLabelId, branchChildren[0].key);
    }

    const adjustedBottom = bl.bottom + branchContentY;
    if (adjustedBottom > maxBottom) {
      maxBottom = adjustedBottom;
    }

    currentX += bl.width + box.marginRight;
  });

  // Place invisible merge-point node
  const mergeId = '__merge_' + conditionNode.key;
  nodes.push({
    id: mergeId,
    type: 'mergePoint',
    position: { x: centerX - 1, y: maxBottom },
    data: {},
  });

  // Connect last node of each branch to merge point
  branchLayouts.forEach((bl) => {
    const branchChildren = bl.branch.getChildren();
    if (bl.lastKey) {
      addEdge(edges, bl.lastKey, mergeId);
    } else {
      // Empty branch: connect branch label directly to merge
      addEdge(edges, bl.branch.key, mergeId);
    }
  });

  return {
    bottom: maxBottom + box.marginBottom * 0.5,
    width: totalWidth,
  };
}

function addEdge(edges, sourceId, targetId) {
  if (!sourceId || !targetId) return;
  const id = `e-${sourceId}-${targetId}`;
  // Avoid duplicates
  if (edges.some(e => e.id === id)) return;
  edges.push({
    id,
    source: sourceId,
    target: targetId,
    type: 'smoothstep',
    style: { stroke: '#b1b5bb', strokeWidth: 1.5 },
    markerEnd: { type: MarkerType.ArrowClosed, color: '#b1b5bb', width: 16, height: 16 },
  });
}
