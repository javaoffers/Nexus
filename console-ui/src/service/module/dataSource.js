import { datasourceAPI } from '@/service/api';

export async function addDataSource(params) {
  return datasourceAPI.addDataSource(params);
}

export async function deleteDataSource(dataSourceId) {
  return datasourceAPI.deleteDataSourceById(dataSourceId);
}

export async function updateDataSource(params) {
  return datasourceAPI.updateDataSourceById(params);
}

export async function queryDataSourceInfo(dataSourceId) {
  return datasourceAPI.queryDataSourceInfo(dataSourceId);
}

export async function queryDataSourceList() {
  return datasourceAPI.dataSourceList();
}

export async function queryDataSourcePage(params) {
  return datasourceAPI.dataSourcePage(params);
}

export async function connectDataSource(dataSourceId) {
  return datasourceAPI.connectDataSource(dataSourceId);
}
