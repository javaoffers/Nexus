package model

type Token struct {
	BaseModel
	TokenValue string `gorm:"column:token_value;size:150" json:"tokenValue,omitempty"`
	TokenDesc  string `gorm:"column:token_desc;size:200" json:"tokenDesc"`
}

func (Token) TableName() string { return "t_token" }
