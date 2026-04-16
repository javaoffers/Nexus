package model

type FlowInfo struct {
	BaseModel
	FlowKey  string `gorm:"column:flow_key;size:20" json:"flowKey"`
	FlowName string `gorm:"column:flow_name;size:60" json:"flowName"`
	FlowType string `gorm:"column:flow_type;size:8" json:"flowType"`
	Remark   string `gorm:"column:remark;size:200" json:"remark,omitempty"`
}

func (FlowInfo) TableName() string { return "t_flow_info" }
