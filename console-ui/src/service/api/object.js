import { request } from '@/service/base';
import {NEXUS_API_PREFIX} from "@/const/application";

export async function addObject(params) {
  return request.post(NEXUS_API_PREFIX+`/object/add`, params);
}

export async function deleteObjectById(objectId) {
  return request.delete(NEXUS_API_PREFIX+`/object/delete/${objectId}`);
}

export async function updateObjectById(params) {
  return request.put(NEXUS_API_PREFIX+`/object/update`, params);
}

export async function queryObjectInfo(objectId) {
  return request.get(NEXUS_API_PREFIX+`/object/info/${objectId}`);
}

export async function isExistObjectKey(params) {
  return request.post(NEXUS_API_PREFIX+`/object/exist/key`,params);
}

export async function objectPage(params) {
  return request.post(NEXUS_API_PREFIX+'/object/page', params);
}
