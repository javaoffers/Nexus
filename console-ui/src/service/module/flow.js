import { flowAPI } from '@/service/api';

export async function updateFlowStatus(flowId, flowStatus) {
  return flowAPI.updateFlowStatus(flowId, flowStatus);
}

export async function queryFlowPage(params) {
  return flowAPI.flowPage(params);
}

export async function deleteFlowById(params) {
  return flowAPI.deleteFlow(params);
}
