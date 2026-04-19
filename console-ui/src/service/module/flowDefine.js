import { flowDefineAPI } from '@/service/api';

export async function addDefineInfo(params) {
  return flowDefineAPI.addDefineInfo(params);
}

export async function getDefineInfo(id) {
  return flowDefineAPI.getDefineInfo(id);
}

export async function queryFlowDefinePage(params) {
  return flowDefineAPI.flowDefinePage(params);
}

export async function deleteFlowDefineById(params) {
  return flowDefineAPI.deleteFlowDefine(params);
}

export async function updateDefineInfo(params) {
  return flowDefineAPI.updateDefineInfo(params);
}

export async function saveFlowContent(params) {
  return flowDefineAPI.saveFlowContent(params);
}

export async function deployFlowDefine(params) {
  return flowDefineAPI.deployFlowDefine(params);
}

export async function debugFlow(flowKey, params) {
  return flowDefineAPI.debugFlow(flowKey, params);
}
