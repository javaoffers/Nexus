package repository

import (
	"github.com/nexus-flow/nexus/core/model"
	"github.com/nexus-flow/nexus/db"
)

func CreateFlowVersion(f *model.FlowVersion) error {
	return db.DB.Create(f).Error
}

func DeleteFlowVersion(id int64) error {
	return db.DB.Model(&model.FlowVersion{}).Where("id = ?", id).Update("deleted", 1).Error
}

func UpdateFlowVersionStatus(id int64, status int) error {
	return db.DB.Model(&model.FlowVersion{}).Where("id = ?", id).Update("flow_version_status", status).Error
}

func GetFlowVersionByID(id int64) (*model.FlowVersion, error) {
	var f model.FlowVersion
	err := db.DB.Where("id = ? AND deleted = 0", id).First(&f).Error
	return &f, err
}

func GetFlowVersionByKeyAndVersion(flowKey, version string) (*model.FlowVersion, error) {
	var info model.FlowInfo
	if err := db.DB.Where("flow_key = ? AND deleted = 0", flowKey).First(&info).Error; err != nil {
		return nil, err
	}
	var fv model.FlowVersion
	err := db.DB.Where("flow_id = ? AND flow_version = ? AND deleted = 0", info.ID, version).First(&fv).Error
	return &fv, err
}

func GetLatestVersion(flowKey string) (string, error) {
	var info model.FlowInfo
	if err := db.DB.Where("flow_key = ? AND deleted = 0", flowKey).First(&info).Error; err != nil {
		return "v1", err
	}
	var count int64
	db.DB.Model(&model.FlowVersion{}).Where("flow_id = ? AND deleted = 0", info.ID).Count(&count)
	return "v" + intToStr(count+1), nil
}

func PageFlowVersions(p *model.PageParam, flowID *int64, status *int) ([]model.FlowVersion, int64, error) {
	q := db.DB.Model(&model.FlowVersion{}).Where("deleted = 0")
	if flowID != nil {
		q = q.Where("flow_id = ?", *flowID)
	}
	if status != nil {
		q = q.Where("flow_version_status = ?", *status)
	}
	var total int64
	q.Count(&total)
	var list []model.FlowVersion
	err := q.Offset(p.Offset()).Limit(p.Limit()).Order("id DESC").Find(&list).Error
	return list, total, err
}

func GetEnabledFlowVersion(flowKey, version string) (*model.FlowVersion, error) {
	var info model.FlowInfo
	if err := db.DB.Where("flow_key = ? AND deleted = 0", flowKey).First(&info).Error; err != nil {
		return nil, err
	}
	var fv model.FlowVersion
	err := db.DB.Where("flow_id = ? AND flow_version = ? AND flow_version_status = 1 AND deleted = 0", info.ID, version).First(&fv).Error
	return &fv, err
}

func intToStr(n int64) string {
	if n <= 0 {
		return "1"
	}
	s := ""
	for n > 0 {
		s = string(rune('0'+n%10)) + s
		n /= 10
	}
	return s
}
