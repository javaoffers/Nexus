import { request, ResponsePageResult, ResponseResult } from '@/service/base';
import {NEXUS_API_PREFIX} from "@/const/application.ts";

export function updateFlowStatus(flowId: number, flowStatus: number): ResponsePageResult {
  return request.put(NEXUS_API_PREFIX+'/flow/status', { flowId: flowId, flowStatus: flowStatus });
}

export async function flowPage(params: { pageNum: number; pageSize: number; flowName?: string; flowType?: string }): ResponsePageResult {
  return request.post(NEXUS_API_PREFIX+'/flow/page', params);
}

export async function deleteFlow(id: number): ResponseResult<boolean> {
  return request.delete(NEXUS_API_PREFIX+`/flow/delete/${id}`);
}
