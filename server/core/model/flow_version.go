package model

type FlowVersion struct {
	BaseModel
	FlowID            int64  `gorm:"column:flow_id" json:"flowId"`
	FlowVersion       string `gorm:"column:flow_version;size:8" json:"flowVersion"`
	FlowVersionStatus int    `gorm:"column:flow_version_status;default:0" json:"flowVersionStatus"`
	FlowVersionRemark string `gorm:"column:flow_version_remark;size:200" json:"flowVersionRemark,omitempty"`
	FlowContent       string `gorm:"column:flow_content;type:mediumtext" json:"flowContent,omitempty"`
	Inputs            string `gorm:"column:inputs;type:text" json:"inputs,omitempty"`
	Outputs           string `gorm:"column:outputs;type:text" json:"outputs,omitempty"`
	Variables         string `gorm:"column:variables;type:text" json:"variables,omitempty"`
}

func (FlowVersion) TableName() string { return "t_flow_version" }
