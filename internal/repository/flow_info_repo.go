package repository

import (
	"github.com/nexus-flow/nexus/db"
	"github.com/nexus-flow/nexus/internal/model"
)

func CreateFlowInfo(f *model.FlowInfo) error {
	return db.DB.Create(f).Error
}

func DeleteFlowInfo(id int64) error {
	return db.DB.Model(&model.FlowInfo{}).Where("id = ?", id).Update("deleted", 1).Error
}

func GetFlowInfoByKey(key string) (*model.FlowInfo, error) {
	var f model.FlowInfo
	err := db.DB.Where("flow_key = ? AND deleted = 0", key).First(&f).Error
	return &f, err
}

func PageFlowInfos(p *model.PageParam, flowName, flowType string) ([]model.FlowInfo, int64, error) {
	q := db.DB.Model(&model.FlowInfo{}).Where("deleted = 0")
	if flowName != "" {
		q = q.Where("flow_name LIKE ?", "%"+flowName+"%")
	}
	if flowType != "" {
		q = q.Where("flow_type = ?", flowType)
	}
	var total int64
	q.Count(&total)
	var list []model.FlowInfo
	err := q.Offset(p.Offset()).Limit(p.Limit()).Order("id DESC").Find(&list).Error
	return list, total, err
}
