package model

type FlowDefinition struct {
	BaseModel
	FlowKey     string `gorm:"column:flow_key;size:20" json:"flowKey"`
	FlowName    string `gorm:"column:flow_name;size:60" json:"flowName"`
	FlowType    string `gorm:"column:flow_type;size:8" json:"flowType"`
	FlowContent string `gorm:"column:flow_content;type:mediumtext" json:"flowContent"`
	Remark      string `gorm:"column:remark;size:200" json:"remark,omitempty"`
}

func (FlowDefinition) TableName() string { return "t_flow_definition" }
