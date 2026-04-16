package repository

import (
	"github.com/nexus-flow/nexus/core/model"
	"github.com/nexus-flow/nexus/db"
)

func CreateDataSource(d *model.DataSource) error {
	return db.DB.Create(d).Error
}

func UpdateDataSource(d *model.DataSource) error {
	return db.DB.Save(d).Error
}

func DeleteDataSource(id int64) error {
	return db.DB.Model(&model.DataSource{}).Where("id = ?", id).Update("deleted", 1).Error
}

func GetDataSourceByID(id int64) (*model.DataSource, error) {
	var d model.DataSource
	err := db.DB.Where("id = ? AND deleted = 0", id).First(&d).Error
	return &d, err
}

func ListDataSources() ([]model.DataSource, error) {
	var list []model.DataSource
	err := db.DB.Where("deleted = 0").Find(&list).Error
	return list, err
}

func PageDataSources(p *model.PageParam, name, dsType string) ([]model.DataSource, int64, error) {
	q := db.DB.Model(&model.DataSource{}).Where("deleted = 0")
	if name != "" {
		q = q.Where("data_source_name LIKE ?", "%"+name+"%")
	}
	if dsType != "" {
		q = q.Where("data_source_type = ?", dsType)
	}
	var total int64
	q.Count(&total)
	var list []model.DataSource
	err := q.Offset(p.Offset()).Limit(p.Limit()).Order("id DESC").Find(&list).Error
	return list, total, err
}
