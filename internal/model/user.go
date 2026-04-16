package model

type User struct {
	BaseModel
	UserName string `gorm:"column:user_name;size:30" json:"userName"`
	Password string `gorm:"column:password;size:60" json:"-"`
}

func (User) TableName() string { return "t_user" }
