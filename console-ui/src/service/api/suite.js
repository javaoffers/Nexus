import { request } from '../base';
import {NEXUS_API_PREFIX} from "@/const/application";

export async function addSuite(params) {
  return request.post(NEXUS_API_PREFIX+'/suite/add', params);
}

export async function deleteSuite(id) {
  return request.delete(NEXUS_API_PREFIX+`/suite/delete/${id}`);
}

export async function updateSuite(params) {
  return request.put(NEXUS_API_PREFIX+'/suite/update', params);
}

export async function suitePage(params) {
  return request.post(NEXUS_API_PREFIX+'/suite/page', params);
}

export async function suiteList() {
  return request.get(NEXUS_API_PREFIX+'/suite/list');
}
