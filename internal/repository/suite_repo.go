package repository

import (
	"github.com/nexus-flow/nexus/db"
	"github.com/nexus-flow/nexus/internal/model"
)

func CreateSuite(s *model.Suite) error {
	return db.DB.Create(s).Error
}

func UpdateSuite(s *model.Suite) error {
	return db.DB.Model(s).Updates(map[string]interface{}{
		"suite_code":  s.SuiteCode,
		"suite_name":  s.SuiteName,
		"suite_image": s.SuiteImage,
		"suite_desc":  s.SuiteDesc,
	}).Error
}

func DeleteSuite(id int64) error {
	return db.DB.Model(&model.Suite{}).Where("id = ?", id).Update("deleted", 1).Error
}

func GetSuiteByID(id int64) (*model.Suite, error) {
	var s model.Suite
	err := db.DB.Where("id = ? AND deleted = 0", id).First(&s).Error
	return &s, err
}

func ListSuites() ([]model.Suite, error) {
	var list []model.Suite
	err := db.DB.Where("deleted = 0").Find(&list).Error
	return list, err
}

func PageSuites(p *model.PageParam, suiteName string, suiteFlag *int) ([]model.Suite, int64, error) {
	q := db.DB.Model(&model.Suite{}).Where("deleted = 0")
	if suiteName != "" {
		q = q.Where("suite_name LIKE ?", "%"+suiteName+"%")
	}
	if suiteFlag != nil {
		q = q.Where("suite_flag = ?", *suiteFlag)
	}
	var total int64
	q.Count(&total)
	var list []model.Suite
	err := q.Offset(p.Offset()).Limit(p.Limit()).Order("id DESC").Find(&list).Error
	return list, total, err
}
