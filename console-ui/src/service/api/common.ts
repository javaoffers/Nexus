import { request, ResponseResult } from '@/service/base';
import {NEXUS_API_PREFIX} from "@/const/application.ts";

export function listDataType(): ResponseResult {
  return request.get(NEXUS_API_PREFIX+'/dataType/list');
}
