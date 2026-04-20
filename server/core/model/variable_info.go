package model

import "time"

type VariableInfo struct {
	ID               int64      `gorm:"primaryKey;autoIncrement" json:"id"`
	FlowDefinitionID int64      `gorm:"column:flow_definition_id" json:"flowDefinitionId"`
	VariableKey      string     `gorm:"column:variable_key;size:30" json:"variableKey"`
	VariableName     string     `gorm:"column:variable_name;size:30" json:"variableName"`
	VariableType     int        `gorm:"column:variable_type" json:"variableType"`
	DataType         string     `gorm:"column:data_type;type:text" json:"dataType,omitempty"`
	CreatedAt        *time.Time `gorm:"autoCreateTime" json:"createdAt,omitempty"`
	CreatedBy        *int64     `json:"createdBy,omitempty"`
	UpdatedAt        *time.Time `gorm:"autoUpdateTime" json:"updatedAt,omitempty"`
	UpdatedBy        *int64     `json:"updatedBy,omitempty"`
}

func (VariableInfo) TableName() string { return "t_variable_info" }
