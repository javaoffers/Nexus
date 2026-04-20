package model

type Object struct {
	BaseModel
	ObjectKey  string `gorm:"column:object_key;size:30" json:"objectKey"`
	ObjectName string `gorm:"column:object_name;size:50" json:"objectName"`
	ObjectDesc string `gorm:"column:object_desc;size:200" json:"objectDesc,omitempty"`
}

func (Object) TableName() string { return "t_object" }
