package model

import "time"

type BaseModel struct {
	ID        int64      `gorm:"primaryKey;autoIncrement" json:"id"`
	Deleted   int        `gorm:"default:0" json:"-"`
	CreatedAt *time.Time `gorm:"autoCreateTime" json:"createdAt,omitempty"`
	CreatedBy *int64     `json:"createdBy,omitempty"`
	UpdatedAt *time.Time `gorm:"autoUpdateTime" json:"updatedAt,omitempty"`
	UpdatedBy *int64     `json:"updatedBy,omitempty"`
}

// PageParam is the base pagination request.
type PageParam struct {
	PageNum  int `json:"pageNum" form:"pageNum"`
	PageSize int `json:"pageSize" form:"pageSize"`
}

func (p *PageParam) Offset() int {
	if p.PageNum <= 0 {
		p.PageNum = 1
	}
	if p.PageSize <= 0 {
		p.PageSize = 10
	}
	return (p.PageNum - 1) * p.PageSize
}

func (p *PageParam) Limit() int {
	if p.PageSize <= 0 {
		p.PageSize = 10
	}
	return p.PageSize
}
