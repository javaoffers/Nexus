import { suiteAPI } from '../api';

export async function addSuite(params) {
  return suiteAPI.addSuite(params);
}

export async function deleteSuite(suiteId) {
  return suiteAPI.deleteSuite(suiteId);
}

export async function updateSuite(params) {
  return suiteAPI.updateSuite(params);
}

export async function querySuitePage(params) {
  return suiteAPI.suitePage(params);
}

export async function querySuiteList() {
  return suiteAPI.suiteList();
}
