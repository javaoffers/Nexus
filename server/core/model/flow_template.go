package model

type FlowTemplate struct {
	BaseModel
	TemplateName    string `gorm:"column:template_name;size:60" json:"templateName"`
	TemplateRemark  string `gorm:"column:template_remark;size:200" json:"templateRemark,omitempty"`
	TemplateContent string `gorm:"column:template_content;type:mediumtext" json:"templateContent,omitempty"`
	FlowType        string `gorm:"column:flow_type;size:8" json:"flowType,omitempty"`
	Inputs          string `gorm:"column:inputs;type:text" json:"inputs,omitempty"`
	Outputs         string `gorm:"column:outputs;type:text" json:"outputs,omitempty"`
	Variables       string `gorm:"column:variables;type:text" json:"variables,omitempty"`
}

func (FlowTemplate) TableName() string { return "t_flow_template" }
