import { request } from '@/service/base';
import {NEXUS_API_PREFIX} from "@/const/application";

export function listDataType() {
  return request.get(NEXUS_API_PREFIX+'/dataType/list');
}
