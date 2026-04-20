package repository

import (
	"github.com/nexus-flow/nexus/core/model"
	"github.com/nexus-flow/nexus/db"
)

func CreateToken(t *model.Token) error {
	return db.DB.Create(t).Error
}

func DeleteToken(id int64) error {
	return db.DB.Model(&model.Token{}).Where("id = ?", id).Update("deleted", 1).Error
}

func PageTokens(p *model.PageParam) ([]model.Token, int64, error) {
	q := db.DB.Model(&model.Token{}).Where("deleted = 0")
	var total int64
	q.Count(&total)
	var list []model.Token
	err := q.Offset(p.Offset()).Limit(p.Limit()).Order("id DESC").Find(&list).Error
	return list, total, err
}

func TokenValueExists(tokenValue string) (bool, error) {
	var count int64
	err := db.DB.Model(&model.Token{}).Where("token_value = ? AND deleted = 0", tokenValue).Count(&count).Error
	return count > 0, err
}
