const DATA_TYPE_TEMP = new Map();

export function getDataTypeObject(dataType) {
  //console.log("getDataTypeObject",dataType)
  let result = null;
  if (!dataType) {
    return result;
  }
  const key = JSON.stringify(dataType);
  if (DATA_TYPE_TEMP.has(key)) {
    return DATA_TYPE_TEMP.get(key);
  }
  try {
    if (key) {
      //const dataType = JSON.parse(json);
      DATA_TYPE_TEMP.set(key, dataType);
      if (dataType.objectStructure && typeof dataType.objectStructure === 'string') {
        dataType.objectStructure = JSON.parse(dataType.objectStructure);
      }
      result = dataType;
    }
  } catch (error) {
    const message = `数据类型解析失败: 类型数据结构为"${key}"'`;
    console.error(new Error(message));
  }
  return result;
}

export function isDataTypeEqual(typeA, typeB) {
  if (typeA === typeB) {
    return true;
  }
  const dataTypeA = getDataTypeObject(typeA);
  const dataTypeB = getDataTypeObject(typeB);
  if (!dataTypeA || !dataTypeB) {
    return false;
  }
  if (dataTypeA.type !== dataTypeB.type) {
    return false;
  }
  if (dataTypeA.type === 'List') {
    if (dataTypeA.itemType !== dataTypeB.itemType) {
      return false;
    }
  }
  if (dataTypeA.type === 'Object' || dataTypeA.itemType === 'Object') {
    if (dataTypeA.objectKey !== dataTypeB.objectKey) {
      return false;
    }
  }
  return true;
}

/**
 *
 * @param sourceDataTypeItem
 * @param filterDataType 期望的数据类型
 */
export function isDataTypeMatch(sourceDataTypeItem, filterDataType) {
  if (!sourceDataTypeItem || !filterDataType) {
    return false;
  }
  if (sourceDataTypeItem === filterDataType) {
    return true;
  }

  if(filterDataType.type == "String" || filterDataType.type == "Integer" ||
      filterDataType.type == "Double" || filterDataType.type == "Boolean" ||
      filterDataType.type == "Date" || filterDataType.type == "Time"){
    if(sourceDataTypeItem.type == filterDataType.type || sourceDataTypeItem.type == 'Object'){
      return true;
    }
  }

  if(filterDataType.type == "Object"){
    //都是数据结构类型，且数据结构编码一致
    if(sourceDataTypeItem.type == 'Object' && sourceDataTypeItem.objectKey == filterDataType.objectKey){
      console.log("object", filterDataType,sourceDataTypeItem);
      return true;
    }
    // 数据结构属性中有匹配的数据结构
    if(sourceDataTypeItem.type == 'Object' && isMatchObjectStructure(filterDataType,sourceDataTypeItem?.objectStructure)){
       return true;
    }
  }
  return false;
}

/**
 * 数据结构字段是否有匹配属性
 * @param filterDataType
 * @param objectStructure
 */
function isMatchObjectStructure(filterDataType, objectStructure){
  if(objectStructure && Array.isArray(objectStructure)){
    return objectStructure.some(prop => prop.dataType.type === filterDataType.type && prop.dataType.objectKey === filterDataType.objectKey);
  }
  return false;
}


export function getVariableDataType(variableKey, variableList){
  let variable;
  if (variableKey.includes('.')) {
    variable = variableKey.split('.').reduce((acc, cur) => {
      if (acc) {
        return (acc?.dataType?.objectStructure).find((item) => item.propKey === cur);
      }
      return variableList.find(item => item.variableKey === cur);
    }, null);
  }else{
    variable = variableList.find(item => item.variableKey === variableKey);
  }
  return variable;
}
