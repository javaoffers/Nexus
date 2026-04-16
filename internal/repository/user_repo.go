package repository

import (
	"github.com/nexus-flow/nexus/db"
	"github.com/nexus-flow/nexus/internal/model"
)

func GetUserByName(userName string) (*model.User, error) {
	var user model.User
	err := db.DB.Where("user_name = ? AND deleted = 0", userName).First(&user).Error
	if err != nil {
		return nil, err
	}
	return &user, nil
}

func GetUserByID(id int64) (*model.User, error) {
	var user model.User
	err := db.DB.Where("id = ? AND deleted = 0", id).First(&user).Error
	if err != nil {
		return nil, err
	}
	return &user, nil
}

func UpdateUserPassword(id int64, password string) error {
	return db.DB.Model(&model.User{}).Where("id = ?", id).Update("password", password).Error
}
