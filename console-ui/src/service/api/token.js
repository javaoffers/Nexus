import { request } from '@/service/base';
import {NEXUS_API_PREFIX} from "@/const/application";

export async function addToken(tokenDesc) {
  return request.post(NEXUS_API_PREFIX+`/token/add`, tokenDesc);
}

export async function deleteTokenById(tokenId) {
  return request.delete(NEXUS_API_PREFIX+`/token/delete/${tokenId}`);
}

export async function updateTokenById(params) {
  return request.put(NEXUS_API_PREFIX+`/token/update`, params);
}

export async function tokenPage(params) {
  return request.post(NEXUS_API_PREFIX+'/token/page', params);
}
