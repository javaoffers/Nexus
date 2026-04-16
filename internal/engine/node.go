package engine

import "encoding/json"

// FlowElement represents a node in the flow graph.
type FlowElement struct {
	Key         string          `json:"key"`
	Name        string          `json:"name"`
	ElementType string          `json:"elementType"`
	Desc        string          `json:"desc,omitempty"`
	Incomings   []string        `json:"incomings,omitempty"`
	Outgoings   []string        `json:"outgoings,omitempty"`
	Method      json.RawMessage `json:"method,omitempty"`
	Conditions  json.RawMessage `json:"conditions,omitempty"`
	AssignRules json.RawMessage `json:"assignRules,omitempty"`
	Language    string          `json:"language,omitempty"`
	Content     string          `json:"content,omitempty"`
	// MySQL node fields
	DataSourceID      *int64 `json:"dataSourceId,omitempty"`
	OperationType     string `json:"operationType,omitempty"`
	SQL               string `json:"sql,omitempty"`
	OutputVariableKey string `json:"outputVariableKey,omitempty"`
}

// MethodDef describes an HTTP call in a METHOD node.
type MethodDef struct {
	SuiteCode          string      `json:"suiteCode"`
	MethodCode         string      `json:"methodCode"`
	URL                string      `json:"url"`
	RequestType        string      `json:"requestType"`
	RequestContentType string      `json:"requestContentType"`
	HeaderFillRules    []FillRule  `json:"headerFillRules,omitempty"`
	InputFillRules     []FillRule  `json:"inputFillRules,omitempty"`
	OutputFillRules    []FillRule  `json:"outputFillRules,omitempty"`
}

// FillRule describes a mapping (source → target) for assignments and method I/O.
type FillRule struct {
	Source         string `json:"source"`
	SourceType     string `json:"sourceType"`
	SourceDataType string `json:"sourceDataType,omitempty"`
	Target         string `json:"target"`
	TargetType     string `json:"targetType"`
	TargetDataType string `json:"targetDataType,omitempty"`
}

// ConditionItem describes a branch in a CONDITION node.
type ConditionItem struct {
	ConditionName        string                `json:"conditionName"`
	ConditionType        string                `json:"conditionType"` // DEFAULT or CUSTOM
	Outgoing             string                `json:"outgoing"`
	Expression           string                `json:"expression,omitempty"`
	ConditionExpressions [][]ConditionExpr     `json:"conditionExpressions,omitempty"`
}

// ConditionExpr is a single comparison in a condition.
type ConditionExpr struct {
	VariableKey string      `json:"variableKey"`
	DataType    interface{} `json:"dataType,omitempty"`
	Operator    string      `json:"operator"`
	AssignType  string      `json:"assignType"` // CONSTANT or VARIABLE
	Value       interface{} `json:"value"`
}
