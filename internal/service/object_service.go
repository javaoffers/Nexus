package service

import (
	"encoding/json"
	"errors"

	"github.com/nexus-flow/nexus/internal/model"
	"github.com/nexus-flow/nexus/internal/repository"
)

type ObjectInfoDTO struct {
	ID         int64       `json:"id"`
	ObjectKey  string      `json:"objectKey"`
	ObjectName string      `json:"objectName"`
	ObjectDesc string      `json:"objectDesc,omitempty"`
	Props      interface{} `json:"props,omitempty"`
}

func CreateObject(o *model.Object, props interface{}) error {
	if err := repository.CreateObject(o); err != nil {
		return err
	}
	if props != nil {
		saveObjectProps(o.ID, props)
	}
	return nil
}

func UpdateObject(o *model.Object, props interface{}) error {
	if err := repository.UpdateObject(o); err != nil {
		return err
	}
	if props != nil {
		saveObjectProps(o.ID, props)
	}
	return nil
}

func DeleteObject(id int64) error {
	return repository.DeleteObject(id)
}

func GetObjectInfo(id int64) (*ObjectInfoDTO, error) {
	o, err := repository.GetObjectByID(id)
	if err != nil {
		return nil, err
	}
	params, _ := repository.ListParametersBySource("object", id)
	return &ObjectInfoDTO{
		ID:         o.ID,
		ObjectKey:  o.ObjectKey,
		ObjectName: o.ObjectName,
		ObjectDesc: o.ObjectDesc,
		Props:      params,
	}, nil
}

func ListObjects() ([]model.Object, error) {
	return repository.ListObjects()
}

func PageObjects(p *model.PageParam, objectName string) ([]model.Object, int64, error) {
	return repository.PageObjects(p, objectName)
}

func CheckObjectKeyExists(objectKey string, excludeID *int64) (bool, error) {
	return repository.ObjectKeyExists(objectKey, excludeID)
}

func saveObjectProps(objectID int64, props interface{}) {
	repository.DeleteParametersBySource("object", objectID)
	data, err := json.Marshal(props)
	if err != nil {
		return
	}
	var params []model.Parameter
	if json.Unmarshal(data, &params) != nil {
		return
	}
	for i := range params {
		params[i].SourceType = "object"
		params[i].SourceID = &objectID
		pt := 3 // PROPERTY
		params[i].ParamType = &pt
	}
	repository.BatchCreateParameters(params)
}

func GetDataTypeOptions() (interface{}, error) {
	objects, err := repository.ListObjects()
	if err != nil {
		return nil, errors.New("获取数据类型失败")
	}

	basicTypes := []map[string]interface{}{
		{"type": "String", "displayName": "字符串"},
		{"type": "Integer", "displayName": "整数"},
		{"type": "Double", "displayName": "小数"},
		{"type": "Boolean", "displayName": "布尔"},
		{"type": "Date", "displayName": "日期"},
		{"type": "Time", "displayName": "时间"},
		{"type": "List", "displayName": "列表"},
	}

	objectTypes := make([]map[string]interface{}, 0, len(objects))
	for _, o := range objects {
		props, _ := repository.ListParametersBySource("object", o.ID)
		objectTypes = append(objectTypes, map[string]interface{}{
			"type":            "Object",
			"displayName":     o.ObjectName,
			"objectKey":       o.ObjectKey,
			"objectStructure": props,
		})
	}

	return append(basicTypes, objectTypes...), nil
}
