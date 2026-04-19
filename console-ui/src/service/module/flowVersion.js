import { flowVersionAPI } from '@/service/api';

export async function getLatestDeployVersion(flowId) {
  return flowVersionAPI.getLatestDeployVersion(flowId);
}

export async function updateFlowVersionStatus(flowVersionId, flowVersionStatus) {
  return flowVersionAPI.updateFlowVersionStatus(flowVersionId, flowVersionStatus);
}

export async function queryFlowVersionPage(params) {
  return flowVersionAPI.flowVersionPage(params);
}

export async function deleteFlowVersionById(params) {
  return flowVersionAPI.deleteFlowVersion(params);
}

export async function getAsyncFlowResult(flowInstanceId) {
  return flowVersionAPI.getAsyncFlowResult(flowInstanceId);
}
