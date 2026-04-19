import { orderAPI } from '@/service/api';

export async function createOrder(params) {
  return orderAPI.createOrder(params);
}

export async function getOrderPayStatus(orderNo) {
  return orderAPI.getOrderPayStatus(orderNo);
}
