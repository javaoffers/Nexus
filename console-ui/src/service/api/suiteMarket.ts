import { request, type ResponseResult } from '../base';
import {NEXUS_API_PREFIX} from "@/const/application.ts";

export async function querySuiteMarketClassifyList(): ResponseResult {
  return request.post(NEXUS_API_PREFIX+'/suite/market/classify');
}

export async function querySuiteMarketList(params: {
  pageNum:number;
  pageSize:number;
  suiteName: string;
  suiteClassifyId: number|null;
  priceStatus: number|null;
}): ResponseResult {
  return request.post(NEXUS_API_PREFIX+'/suite/market',params);
}

export async function querySuiteMarketDetail(suiteId: number): ResponseResult {
  return request.get(NEXUS_API_PREFIX+'/suite/market/info/' + suiteId);
}

export async function installSuiteMarket(suiteId: number,bill?:string): ResponseResult {
  return request.post(NEXUS_API_PREFIX+'/suite/market/install', {
    suiteId: suiteId,
    bill: bill
  });
}
