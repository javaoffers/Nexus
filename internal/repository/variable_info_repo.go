package repository

import (
	"github.com/nexus-flow/nexus/db"
	"github.com/nexus-flow/nexus/internal/model"
)

func BatchCreateVariableInfos(vars []model.VariableInfo) error {
	if len(vars) == 0 {
		return nil
	}
	return db.DB.Create(&vars).Error
}

func DeleteVariableInfosByFlowDefID(flowDefID int64) error {
	return db.DB.Where("flow_definition_id = ?", flowDefID).Delete(&model.VariableInfo{}).Error
}

func ListVariableInfosByFlowDefID(flowDefID int64) ([]model.VariableInfo, error) {
	var list []model.VariableInfo
	err := db.DB.Where("flow_definition_id = ?", flowDefID).Find(&list).Error
	return list, err
}
