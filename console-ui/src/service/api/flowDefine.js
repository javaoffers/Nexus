import { request } from '../base';
import {NEXUS_API_PREFIX} from "@/const/application";

export async function addDefineInfo(params) {
  return request.post(NEXUS_API_PREFIX+`/flow/definition/add`, params);
}

export async function getDefineInfo(id) {
  return request.get(NEXUS_API_PREFIX+`/flow/definition/info/${id}`);
}

export async function flowDefinePage(params) {
  return request.post(NEXUS_API_PREFIX+'/flow/definition/page', params);
}

export async function deleteFlowDefine(id) {
  return request.delete(NEXUS_API_PREFIX+`/flow/definition/delete/${id}`);
}

export async function updateDefineInfo(params) {
  return request.put(NEXUS_API_PREFIX+`/flow/definition/update`, params);
}

export async function saveFlowContent(params) {
  return request.put(NEXUS_API_PREFIX+'/flow/definition/save', params);
}

export function deployFlowDefine(params) {
  return request.post(NEXUS_API_PREFIX+'/flow/definition/deploy', params);
}

export async function debugFlow(flowKey, triggerData) {
  return request.post(NEXUS_API_PREFIX+`/flow/definition/debug/${flowKey}`, triggerData);
}
