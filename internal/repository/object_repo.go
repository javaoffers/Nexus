package repository

import (
	"github.com/nexus-flow/nexus/db"
	"github.com/nexus-flow/nexus/internal/model"
)

func CreateObject(o *model.Object) error {
	return db.DB.Create(o).Error
}

func UpdateObject(o *model.Object) error {
	return db.DB.Model(o).Updates(map[string]interface{}{
		"object_key":  o.ObjectKey,
		"object_name": o.ObjectName,
		"object_desc": o.ObjectDesc,
	}).Error
}

func DeleteObject(id int64) error {
	return db.DB.Model(&model.Object{}).Where("id = ?", id).Update("deleted", 1).Error
}

func GetObjectByID(id int64) (*model.Object, error) {
	var o model.Object
	err := db.DB.Where("id = ? AND deleted = 0", id).First(&o).Error
	return &o, err
}

func ListObjects() ([]model.Object, error) {
	var list []model.Object
	err := db.DB.Where("deleted = 0").Find(&list).Error
	return list, err
}

func PageObjects(p *model.PageParam, objectName string) ([]model.Object, int64, error) {
	q := db.DB.Model(&model.Object{}).Where("deleted = 0")
	if objectName != "" {
		q = q.Where("object_name LIKE ?", "%"+objectName+"%")
	}
	var total int64
	q.Count(&total)
	var list []model.Object
	err := q.Offset(p.Offset()).Limit(p.Limit()).Order("id DESC").Find(&list).Error
	return list, total, err
}

func ObjectKeyExists(objectKey string, excludeID *int64) (bool, error) {
	q := db.DB.Model(&model.Object{}).Where("object_key = ? AND deleted = 0", objectKey)
	if excludeID != nil {
		q = q.Where("id != ?", *excludeID)
	}
	var count int64
	err := q.Count(&count).Error
	return count > 0, err
}
