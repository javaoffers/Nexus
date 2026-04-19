import { request } from '@/service/base';
import {NEXUS_API_PREFIX} from "@/const/application";

export function login(data) {
  return request.post(NEXUS_API_PREFIX+'/user/login', data);
}

export function logout() {}

export function check() {}

export function getProductInfo()  {
  return request.get(NEXUS_API_PREFIX+'/user/product/info');
}
