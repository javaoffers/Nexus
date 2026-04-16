package engine

import (
	"encoding/json"
	"errors"
)

// ValidateFlowContent validates the flow JSON content.
func ValidateFlowContent(content string) error {
	if content == "" {
		return errors.New("流程内容为空")
	}

	var elements []FlowElement
	if err := json.Unmarshal([]byte(content), &elements); err != nil {
		return errors.New("流程内容JSON格式错误")
	}

	hasStart := false
	hasEnd := false
	for _, e := range elements {
		if e.ElementType == "START" {
			hasStart = true
		}
		if e.ElementType == "END" {
			hasEnd = true
		}
	}

	if !hasStart {
		return errors.New("流程缺少开始节点")
	}
	if !hasEnd {
		return errors.New("流程缺少结束节点")
	}

	return nil
}
