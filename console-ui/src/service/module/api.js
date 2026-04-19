import { apiAPI } from '../api';

export async function listQuery(params) {
  return apiAPI.listQuery(params);
}

export async function listAdd(params) {
  return apiAPI.listAdd(params);
}

export async function listUpdate(params) {
  return apiAPI.listUpdate(params);
}

export async function listDelete(params) {
  return apiAPI.listDelete(params);
}

export async function queryApiInfo(apiId) {
  return apiAPI.queryApiInfo(apiId);
}

export async function queryApiInfoByCode(apiCode) {
  return apiAPI.queryApiInfoByCode(apiCode);
}

export async function debugApi(apiId, params) {
  return apiAPI.debugApi(apiId, params);
}

export async function getApiListBySuiteId(params) {
  return apiAPI.getApiListBySuiteId(params);
}

export async function getApiListBySuiteCode(suiteCode) {
  return apiAPI.getApiListBySuiteCode(suiteCode);
}
