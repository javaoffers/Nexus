import { ElementType } from '../types';
import { nodeMap } from '../config';
import { MarkerType } from '@vue-flow/core';

const box = {
  width: 220,
  height: 56,
  marginRight: 100,
  marginBottom: 80,
  startSize: 56,
  branchLabelWidth: 100,
};

const widthAndMargin = box.width + box.marginRight;
const branchLeft = box.width + box.marginRight * 0.6;

/**
 * Convert a DataBranch tree into Vue Flow nodes[] and edges[].
 * @param {DataBranch} dataRoot - root branch from generateDataTree()
 * @returns {{ nodes: Array, edges: Array }}
 */
export function computeFlowLayout(dataRoot) {
  const nodes = [];
  const edges = [];
  // Layout the tree starting from root, centered at y=0
  layoutBranch(dataRoot, 0, 0, nodes, edges);
  // Center all nodes vertically: find min y and shift everything so the topmost node is at y=40
  if (nodes.length > 0) {
    const minY = Math.min(...nodes.map(n => n.position.y));
    const offsetY = -minY + 40;
    nodes.forEach(n => {
      n.position.y += offsetY;
    });
  }
  return { nodes, edges };
}

function layoutBranch(branch, startX, startY, nodes, edges) {
  const children = branch.getChildren();
  let prevKey = null;
  let prevRight = startX;
  let branchHeight = box.height;

  children.forEach((child) => {
    if (child.type === ElementType.CONDITION) {
      const result = layoutCondition(child, prevRight, startY, nodes, edges);
      if (prevKey) {
        addEdge(edges, prevKey, child.key);
      }
      prevKey = '__merge_' + child.key;
      prevRight = result.right;
      branchHeight = Math.max(branchHeight, result.height);
    } else {
      const nodeResult = layoutNormalNode(child, prevRight, startY, nodes);
      if (prevKey) {
        addEdge(edges, prevKey, child.key);
      }
      prevKey = child.key;
      prevRight = nodeResult.right;
    }
  });

  return { right: prevRight, height: branchHeight, lastKey: prevKey };
}

function layoutNormalNode(dataNode, leftX, centerY, nodes) {
  const isTerminal = [ElementType.START, ElementType.END].includes(dataNode.type);
  const w = isTerminal ? box.startSize : box.width;
  const h = isTerminal ? box.startSize : box.height;

  let vfType = 'normalNode';
  if (dataNode.type === ElementType.START) vfType = 'startNode';
  else if (dataNode.type === ElementType.END) vfType = 'endNode';

  nodes.push({
    id: dataNode.key,
    type: vfType,
    position: { x: leftX, y: centerY - h / 2 },
    data: {
      dataNode,
      nodeConfig: nodeMap[dataNode.type] || {},
    },
  });

  return { right: leftX + w + box.marginRight };
}

function layoutCondition(conditionNode, leftX, centerY, nodes, edges) {
  // Place the condition node itself
  nodes.push({
    id: conditionNode.key,
    type: 'conditionNode',
    position: { x: leftX, y: centerY - box.height / 2 },
    data: {
      dataNode: conditionNode,
      nodeConfig: nodeMap[ElementType.CONDITION] || {},
    },
  });

  const branches = conditionNode.getChildren();
  const branchCount = branches.length;

  // First pass: compute each branch's content to get heights
  const branchLayouts = [];
  branches.forEach((branch) => {
    const tempNodes = [];
    const tempEdges = [];
    const result = layoutBranch(branch, 0, 0, tempNodes, tempEdges);
    branchLayouts.push({
      branch,
      tempNodes,
      tempEdges,
      height: Math.max(result.height, box.height),
      right: result.right,
      lastKey: result.lastKey,
    });
  });

  // Compute total height and position each branch vertically
  const totalHeight = branchLayouts.reduce((sum, bl) => sum + bl.height, 0) + (branchCount - 1) * box.marginBottom;
  let currentY = centerY - totalHeight / 2;

  const branchLabelX = leftX + branchLeft;
  const branchContentX = branchLabelX + box.branchLabelWidth + box.marginRight * 0.5;

  let maxRight = branchContentX;

  branchLayouts.forEach((bl) => {
    const branchCenterY = currentY + bl.height / 2;
    const branchLabelId = bl.branch.key;

    // Place branch label node
    nodes.push({
      id: branchLabelId,
      type: 'branchLabel',
      position: { x: branchLabelX, y: branchCenterY - 18 },
      data: {
        dataNode: bl.branch,
      },
    });

    // Edge: condition -> branch label
    addEdge(edges, conditionNode.key, branchLabelId);

    // Offset temp nodes to correct position
    bl.tempNodes.forEach(n => {
      n.position.x += branchContentX;
      n.position.y += branchCenterY;
      nodes.push(n);
    });
    bl.tempEdges.forEach(e => edges.push(e));

    // Edge: branch label -> first child (or merge point if empty)
    const branchChildren = bl.branch.getChildren();
    if (branchChildren.length > 0) {
      addEdge(edges, branchLabelId, branchChildren[0].key);
    }

    const adjustedRight = bl.right + branchContentX;
    if (adjustedRight > maxRight) {
      maxRight = adjustedRight;
    }

    currentY += bl.height + box.marginBottom;
  });

  // Place invisible merge-point node
  const mergeId = '__merge_' + conditionNode.key;
  nodes.push({
    id: mergeId,
    type: 'mergePoint',
    position: { x: maxRight, y: centerY - 1 },
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
    right: maxRight + box.marginRight * 0.5,
    height: totalHeight,
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