package service

import (
	"encoding/base64"
	"encoding/json"
	"time"

	"github.com/nexus-flow/nexus/core/model"
	"github.com/nexus-flow/nexus/core/repository"
)

type TokenDTO struct {
	ID        int64      `json:"id"`
	TokenDesc string     `json:"tokenDesc"`
	CreatedAt *time.Time `json:"createdAt,omitempty"`
}

func CreateToken(tokenDesc string, userID int64) (string, error) {
	vo := map[string]interface{}{
		"userId":    userID,
		"timestamp": time.Now().UnixMilli(),
	}
	data, _ := json.Marshal(vo)
	tokenValue := base64.URLEncoding.EncodeToString(data)

	t := &model.Token{
		TokenValue: tokenValue,
		TokenDesc:  tokenDesc,
	}
	t.CreatedBy = &userID
	if err := repository.CreateToken(t); err != nil {
		return "", err
	}
	return tokenValue, nil
}

func DeleteToken(id int64) error {
	return repository.DeleteToken(id)
}

func PageTokens(p *model.PageParam) ([]TokenDTO, int64, error) {
	list, total, err := repository.PageTokens(p)
	if err != nil {
		return nil, 0, err
	}
	dtos := make([]TokenDTO, 0, len(list))
	for _, t := range list {
		dtos = append(dtos, TokenDTO{
			ID:        t.ID,
			TokenDesc: t.TokenDesc,
			CreatedAt: t.CreatedAt,
		})
	}
	return dtos, total, nil
}
