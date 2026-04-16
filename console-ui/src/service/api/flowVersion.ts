import { request, ResponsePageResult, ResponseResult } from '@/service/base';
import {NEXUS_API_PREFIX} from "@/const/application.ts";

export function getLatestDeployVersion(flowId: number): ResponsePageResult {
  return request.get(NEXUS_API_PREFIX+'/flow/version/latest/' + flowId);
}

export function updateFlowVersionStatus(flowVersionId: number, flowVersionStatus: number): ResponsePageResult {
  return request.put(NEXUS_API_PREFIX+'/flow/version/status', { flowVersionId: flowVersionId, flowVersionStatus: flowVersionStatus });
}

export async function flowVersionPage(params: { pageNum: number; pageSize: number; flowId: number; flowVersionStatus?: number }): ResponsePageResult {
  return request.post(NEXUS_API_PREFIX+'/flow/version/page', params);
}

export async function deleteFlowVersion(id: number): ResponseResult<boolean> {
  return request.delete(NEXUS_API_PREFIX+`/flow/version/delete/${id}`);
}

export async function getAsyncFlowResult(flowInstanceId: string): ResponseResult<boolean> {
  return request.get(NEXUS_API_PREFIX+`/flow/version/getAsyncFlowResult/${flowInstanceId}`);
}
