import { objectAPI } from '@/service/api';

export async function addObject(params) {
  return objectAPI.addObject(params);
}

export async function deleteObject(objectId) {
  return objectAPI.deleteObjectById(objectId);
}

export async function updateObject(params) {
  return objectAPI.updateObjectById(params);
}

export async function queryObjectInfo(objectId) {
  return objectAPI.queryObjectInfo(objectId);
}

export async function isExistObjectKey(params) {
  return objectAPI.isExistObjectKey(params);
}

export async function queryObjectPage(params) {
  return objectAPI.objectPage(params);
}
