import { request } from '@/service/base';
import {NEXUS_API_PREFIX} from "@/const/application";

export async function createOrder(params) {
  return request.post(NEXUS_API_PREFIX+`/order/createOrder`, params);
}

export async function getOrderPayStatus(orderNo) {
  return request.get(NEXUS_API_PREFIX+`/order/getOrderPayStatus/${orderNo}`);
}
