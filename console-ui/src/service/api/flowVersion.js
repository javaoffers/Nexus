import { request } from '@/service/base';
import {NEXUS_API_PREFIX} from "@/const/application";

export function getLatestDeployVersion(flowId) {
  return request.get(NEXUS_API_PREFIX+'/flow/version/latest/' + flowId);
}

export function updateFlowVersionStatus(flowVersionId, flowVersionStatus) {
  return request.put(NEXUS_API_PREFIX+'/flow/version/status', { flowVersionId: flowVersionId, flowVersionStatus: flowVersionStatus });
}

export async function flowVersionPage(params) {
  return request.post(NEXUS_API_PREFIX+'/flow/version/page', params);
}

export async function deleteFlowVersion(id) {
  return request.delete(NEXUS_API_PREFIX+`/flow/version/delete/${id}`);
}

export async function getAsyncFlowResult(flowInstanceId) {
  return request.get(NEXUS_API_PREFIX+`/flow/version/getAsyncFlowResult/${flowInstanceId}`);
}
