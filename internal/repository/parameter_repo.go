package repository

import (
	"github.com/nexus-flow/nexus/db"
	"github.com/nexus-flow/nexus/internal/model"
)

func CreateParameter(p *model.Parameter) error {
	return db.DB.Create(p).Error
}

func BatchCreateParameters(params []model.Parameter) error {
	if len(params) == 0 {
		return nil
	}
	return db.DB.Create(&params).Error
}

func DeleteParametersBySource(sourceType string, sourceID int64) error {
	return db.DB.Where("source_type = ? AND source_id = ?", sourceType, sourceID).Delete(&model.Parameter{}).Error
}

func ListParametersBySource(sourceType string, sourceID int64) ([]model.Parameter, error) {
	var list []model.Parameter
	err := db.DB.Where("source_type = ? AND source_id = ?", sourceType, sourceID).Find(&list).Error
	return list, err
}

func ListParametersBySourceAndType(sourceType string, sourceID int64, paramType int) ([]model.Parameter, error) {
	var list []model.Parameter
	err := db.DB.Where("source_type = ? AND source_id = ? AND param_type = ?", sourceType, sourceID, paramType).Find(&list).Error
	return list, err
}
