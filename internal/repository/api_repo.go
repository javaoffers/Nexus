package repository

import (
	"github.com/nexus-flow/nexus/db"
	"github.com/nexus-flow/nexus/internal/model"
)

func CreateAPI(a *model.API) error {
	return db.DB.Create(a).Error
}

func UpdateAPI(a *model.API) error {
	return db.DB.Model(a).Updates(map[string]interface{}{
		"suite_id":                   a.SuiteID,
		"api_url":                    a.ApiURL,
		"api_name":                   a.ApiName,
		"api_desc":                   a.ApiDesc,
		"api_request_type":           a.ApiRequestType,
		"api_request_content_type":   a.ApiRequestContentType,
	}).Error
}

func DeleteAPI(id int64) error {
	return db.DB.Model(&model.API{}).Where("id = ?", id).Update("deleted", 1).Error
}

func GetAPIByID(id int64) (*model.API, error) {
	var a model.API
	err := db.DB.Where("id = ? AND deleted = 0", id).First(&a).Error
	return &a, err
}

func GetAPIByCode(code string) (*model.API, error) {
	var a model.API
	err := db.DB.Where("api_code = ? AND deleted = 0", code).First(&a).Error
	return &a, err
}

func ListAPIsBySuiteID(suiteID int64) ([]model.API, error) {
	var list []model.API
	err := db.DB.Where("suite_id = ? AND deleted = 0", suiteID).Find(&list).Error
	return list, err
}

func ListAPIsBySuiteCode(suiteCode string) ([]model.API, error) {
	var suite model.Suite
	if err := db.DB.Where("suite_code = ? AND deleted = 0", suiteCode).First(&suite).Error; err != nil {
		return nil, err
	}
	return ListAPIsBySuiteID(suite.ID)
}

func PageAPIs(p *model.PageParam, suiteID *int64, apiName, apiURL string) ([]model.API, int64, error) {
	q := db.DB.Model(&model.API{}).Where("deleted = 0")
	if suiteID != nil {
		q = q.Where("suite_id = ?", *suiteID)
	}
	if apiName != "" {
		q = q.Where("api_name LIKE ?", "%"+apiName+"%")
	}
	if apiURL != "" {
		q = q.Where("api_url LIKE ?", "%"+apiURL+"%")
	}
	var total int64
	q.Count(&total)
	var list []model.API
	err := q.Offset(p.Offset()).Limit(p.Limit()).Order("id DESC").Find(&list).Error
	return list, total, err
}

func CountAPIsBySuiteID(suiteID int64) (int64, error) {
	var count int64
	err := db.DB.Model(&model.API{}).Where("suite_id = ? AND deleted = 0", suiteID).Count(&count).Error
	return count, err
}
