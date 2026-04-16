package engine

import (
	"encoding/json"
	"fmt"
	"log"
)

// FlowDef is the complete flow definition for execution.
type FlowDef struct {
	InstanceID  string
	FlowKey     string
	FlowType    string
	FlowContent string // JSON array of flow elements
	Inputs      []ParameterDef
	Outputs     []ParameterDef
	Variables   []VariableDef
}

type ParameterDef struct {
	Key       string `json:"key,omitempty"`
	ParamKey  string `json:"paramKey,omitempty"`
	Name      string `json:"name,omitempty"`
	ParamName string `json:"paramName,omitempty"`
	DataType  string `json:"dataType,omitempty"`
}

func (p ParameterDef) GetKey() string {
	if p.Key != "" {
		return p.Key
	}
	return p.ParamKey
}

type VariableDef struct {
	Key          string `json:"key,omitempty"`
	VariableKey  string `json:"variableKey,omitempty"`
	Name         string `json:"name,omitempty"`
	VariableName string `json:"variableName,omitempty"`
	VariableType int    `json:"variableType"`
	DataType     string `json:"dataType,omitempty"`
}

func (v VariableDef) GetKey() string {
	if v.Key != "" {
		return v.Key
	}
	return v.VariableKey
}

// ExecutionResult is returned after flow execution.
type ExecutionResult struct {
	Status string                 `json:"status"`
	Data   map[string]interface{} `json:"data,omitempty"`
}

// Execute runs the flow and returns the result.
func Execute(flowDef *FlowDef, flowData map[string]interface{}) *ExecutionResult {
	// 1. Parse flow content into elements
	var elements []FlowElement
	if err := json.Unmarshal([]byte(flowDef.FlowContent), &elements); err != nil {
		log.Printf("[Engine] Failed to parse flow content: %v", err)
		return &ExecutionResult{Status: "ABORT", Data: map[string]interface{}{"error": fmt.Sprintf("解析流程失败: %v", err)}}
	}

	// 2. Build element map
	elementMap := make(map[string]*FlowElement)
	var startNode *FlowElement
	for i := range elements {
		e := &elements[i]
		elementMap[e.Key] = e
		if e.ElementType == "START" {
			startNode = e
		}
	}

	if startNode == nil {
		return &ExecutionResult{Status: "ABORT", Data: map[string]interface{}{"error": "流程中没有开始节点"}}
	}

	// 3. Initialize variable manager
	vm := NewVariableManager()

	// Initialize input variables
	for _, input := range flowDef.Inputs {
		key := "input_" + input.GetKey()
		if val, ok := flowData[input.GetKey()]; ok {
			vm.Set(key, val)
		}
	}

	// Initialize output variables
	for _, output := range flowDef.Outputs {
		key := "output_" + output.GetKey()
		vm.Set(key, nil)
	}

	// Initialize flow variables
	for _, v := range flowDef.Variables {
		vm.Set(v.GetKey(), nil)
	}

	// 4. Create execution context
	ctx := &ExecutionContext{
		InstanceID: flowDef.InstanceID,
		ElementMap: elementMap,
		Variables:  vm,
		Status:     "RUNNING",
	}

	// 5. Execute from start node
	executeFromNode(ctx, startNode)

	// 6. Collect output variables
	outputData := make(map[string]interface{})
	for _, output := range flowDef.Outputs {
		key := "output_" + output.GetKey()
		if val, ok := vm.Get(key); ok {
			outputData[output.GetKey()] = val
		}
	}

	if ctx.Status == "ABORT" {
		return &ExecutionResult{Status: "ABORT", Data: ctx.ErrorData}
	}

	return &ExecutionResult{Status: "FINISH", Data: outputData}
}

func executeFromNode(ctx *ExecutionContext, node *FlowElement) {
	if node == nil || ctx.Status == "ABORT" {
		return
	}

	maxIterations := 1000
	current := node
	for i := 0; i < maxIterations && current != nil && ctx.Status != "ABORT"; i++ {
		nextKey := executeNode(ctx, current)
		if nextKey == "" || current.ElementType == "END" {
			break
		}
		next, ok := ctx.ElementMap[nextKey]
		if !ok {
			ctx.Status = "ABORT"
			ctx.ErrorData = map[string]interface{}{"error": fmt.Sprintf("找不到节点: %s", nextKey)}
			break
		}
		current = next
	}
}

func executeNode(ctx *ExecutionContext, node *FlowElement) string {
	switch node.ElementType {
	case "START":
		return executeStartNode(ctx, node)
	case "END":
		return executeEndNode(ctx, node)
	case "METHOD":
		return executeMethodNode(ctx, node)
	case "CONDITION":
		return executeConditionNode(ctx, node)
	case "ASSIGN":
		return executeAssignNode(ctx, node)
	case "CODE":
		return executeCodeNode(ctx, node)
	case "MYSQL":
		return executeMysqlNode(ctx, node)
	default:
		log.Printf("[Engine] Unknown node type: %s", node.ElementType)
		return ""
	}
}
