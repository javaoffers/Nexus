package model

type Parameter struct {
	BaseModel
	ParamType     *int   `gorm:"column:param_type" json:"paramType,omitempty"`
	ParamKey      string `gorm:"column:param_key;size:40" json:"paramKey"`
	ParamName     string `gorm:"column:param_name;size:40" json:"paramName"`
	ParamPosition string `gorm:"column:param_position;size:20" json:"paramPosition,omitempty"`
	ParamDesc     string `gorm:"column:param_desc;size:200" json:"paramDesc,omitempty"`
	DataType      string `gorm:"column:data_type;type:text" json:"dataType,omitempty"`
	Required      *int   `gorm:"column:required" json:"required,omitempty"`
	SourceType    string `gorm:"column:source_type;size:8" json:"sourceType,omitempty"`
	SourceID      *int64 `gorm:"column:source_id" json:"sourceId,omitempty"`
}

func (Parameter) TableName() string { return "t_parameter" }
