package repository

import (
	"github.com/nexus-flow/nexus/core/model"
	"github.com/nexus-flow/nexus/db"
)

func CreateFlowDefinition(f *model.FlowDefinition) error {
	return db.DB.Create(f).Error
}

func UpdateFlowDefinition(f *model.FlowDefinition) error {
	return db.DB.Model(f).Updates(map[string]interface{}{
		"flow_name": f.FlowName,
		"flow_type": f.FlowType,
		"remark":    f.Remark,
	}).Error
}

func UpdateFlowDefinitionContent(id int64, content string) error {
	return db.DB.Model(&model.FlowDefinition{}).Where("id = ?", id).Update("flow_content", content).Error
}

func DeleteFlowDefinition(id int64) error {
	return db.DB.Model(&model.FlowDefinition{}).Where("id = ?", id).Update("deleted", 1).Error
}

func GetFlowDefinitionByID(id int64) (*model.FlowDefinition, error) {
	var f model.FlowDefinition
	err := db.DB.Where("id = ? AND deleted = 0", id).First(&f).Error
	return &f, err
}

func GetFlowDefinitionByKey(key string) (*model.FlowDefinition, error) {
	var f model.FlowDefinition
	err := db.DB.Where("flow_key = ? AND deleted = 0", key).First(&f).Error
	return &f, err
}

func PageFlowDefinitions(p *model.PageParam, flowName, flowType string) ([]model.FlowDefinition, int64, error) {
	q := db.DB.Model(&model.FlowDefinition{}).Where("deleted = 0")
	if flowName != "" {
		q = q.Where("flow_name LIKE ?", "%"+flowName+"%")
	}
	if flowType != "" {
		q = q.Where("flow_type = ?", flowType)
	}
	var total int64
	q.Count(&total)
	var list []model.FlowDefinition
	err := q.Offset(p.Offset()).Limit(p.Limit()).Order("id DESC").Find(&list).Error
	return list, total, err
}
