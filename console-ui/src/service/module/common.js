import { commonAPI } from '@/service/api';

export async function listDataType() {
  return commonAPI.listDataType();
}

export const dataType = (function () {
  let dataTypeList = [];
  let pedding = null;
  async function getList(useCache = true) {
    if (!useCache || dataTypeList.length === 0) {
      pedding = pedding || listDataType();
      const res = await pedding;
      if (res.success) {
        pedding = null;
        dataTypeList = res.result;
      }
    }
    return dataTypeList;
  }
  function clearList() {
    dataTypeList = [];
  }
  return {
    getList,
    clearList,
  };
})();
