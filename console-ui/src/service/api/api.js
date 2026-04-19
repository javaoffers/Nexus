import { request } from '../base';
import {NEXUS_API_PREFIX} from "@/const/application";

export async function listQuery(params) {
  return request.post(NEXUS_API_PREFIX+'/api/page', params);
}

export async function listAdd(params) {
  return request.post(NEXUS_API_PREFIX+'/api/add', params);
}

export async function listUpdate(params) {
  return request.put(NEXUS_API_PREFIX+'/api/update', params);
}

export async function listDelete(id) {
  return request.delete(NEXUS_API_PREFIX+`/api/delete/${id}`);
}

export async function queryApiInfo(id) {
  return request.get(NEXUS_API_PREFIX+`/api/info/${id}`);
}

export async function queryApiInfoByCode(apiCode) {
  return request.get(NEXUS_API_PREFIX+`/api/info/code/${apiCode}`);
}

export function debugApi(apiId, params) {
  return request.post(NEXUS_API_PREFIX+`/api/debug/${apiId}`, params);
}

export async function getApiListBySuiteId(suiteId) {
  return request.post(NEXUS_API_PREFIX+`/api/getApiListBySuiteId/${suiteId}`);
}

export async function getApiListBySuiteCode(suiteCode) {
  return request.post(NEXUS_API_PREFIX+`/api/getApiListBySuiteCode/${suiteCode}`);
}
