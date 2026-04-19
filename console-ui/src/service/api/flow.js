import { request } from '@/service/base';
import {NEXUS_API_PREFIX} from "@/const/application";

export function updateFlowStatus(flowId, flowStatus) {
  return request.put(NEXUS_API_PREFIX+'/flow/status', { flowId: flowId, flowStatus: flowStatus });
}

export async function flowPage(params) {
  return request.post(NEXUS_API_PREFIX+'/flow/page', params);
}

export async function deleteFlow(id) {
  return request.delete(NEXUS_API_PREFIX+`/flow/delete/${id}`);
}
