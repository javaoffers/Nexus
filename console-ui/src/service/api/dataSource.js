import { request } from '@/service/base';
import {NEXUS_API_PREFIX} from "@/const/application";

export async function addDataSource(params) {
  return request.post(NEXUS_API_PREFIX+`/datasource/add`, params);
}

export async function deleteDataSourceById(datasourceId) {
  return request.delete(NEXUS_API_PREFIX+`/datasource/delete/${datasourceId}`);
}

export async function updateDataSourceById(params) {
  return request.put(NEXUS_API_PREFIX+`/datasource/update`, params);
}

export async function queryDataSourceInfo(dataSourceId) {
  return request.get(NEXUS_API_PREFIX+'/datasource/info/' + dataSourceId);
}

export async function dataSourceList() {
  return request.get(NEXUS_API_PREFIX+'/datasource/list');
}

export async function dataSourcePage(params) {
  return request.post(NEXUS_API_PREFIX+'/datasource/page', params);
}

export async function connectDataSource(dataSourceId) {
  return request.get(NEXUS_API_PREFIX+'/datasource/connect/' + dataSourceId);
}
