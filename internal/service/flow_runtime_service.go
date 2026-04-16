package service

import (
	"encoding/json"
	"errors"
	"fmt"
	"math/rand"

	"github.com/nexus-flow/nexus/internal/cache"
	"github.com/nexus-flow/nexus/internal/engine"
	"github.com/nexus-flow/nexus/internal/repository"
)

type FlowResult struct {
	FlowInstanceID string                 `json:"flowInstanceId"`
	Status         string                 `json:"status"`
	Data           map[string]interface{} `json:"data,omitempty"`
}

func TriggerFlow(flowKey, flowVersion string, flowData map[string]interface{}) (*FlowResult, error) {
	fv, err := repository.GetEnabledFlowVersion(flowKey, flowVersion)
	if err != nil {
		return nil, errors.New("流程版本不存在或未启用")
	}

	flowInfo, err := repository.GetFlowInfoByKey(flowKey)
	if err != nil {
		return nil, errors.New("流程不存在")
	}

	instanceID := fmt.Sprintf("%s_%s", flowInfo.FlowType, randomString(16))

	// Parse inputs/outputs/variables
	var inputs, outputs []engine.ParameterDef
	var variables []engine.VariableDef
	json.Unmarshal([]byte(fv.Inputs), &inputs)
	json.Unmarshal([]byte(fv.Outputs), &outputs)
	json.Unmarshal([]byte(fv.Variables), &variables)

	flowDef := &engine.FlowDef{
		InstanceID:  instanceID,
		FlowKey:     flowKey,
		FlowType:    flowInfo.FlowType,
		FlowContent: fv.FlowContent,
		Inputs:      inputs,
		Outputs:     outputs,
		Variables:   variables,
	}

	if flowInfo.FlowType == "async" {
		go func() {
			result := engine.Execute(flowDef, flowData)
			cache.GetCache().Set(instanceID, result)
		}()
		return &FlowResult{
			FlowInstanceID: instanceID,
			Status:         "RUNNING",
		}, nil
	}

	// sync
	result := engine.Execute(flowDef, flowData)
	return &FlowResult{
		FlowInstanceID: instanceID,
		Status:         result.Status,
		Data:           result.Data,
	}, nil
}

func TriggerFlowByDefinition(flowKey string, flowData map[string]interface{}) (*FlowResult, error) {
	fd, err := repository.GetFlowDefinitionByKey(flowKey)
	if err != nil {
		return nil, errors.New("流程定义不存在")
	}

	instanceID := fmt.Sprintf("debug_%s", randomString(16))

	// Load params from flow definition
	inputParams, _ := repository.ListParametersBySourceAndType("flow", fd.ID, 1)
	outputParams, _ := repository.ListParametersBySourceAndType("flow", fd.ID, 2)
	variableInfos, _ := repository.ListVariableInfosByFlowDefID(fd.ID)

	var inputs []engine.ParameterDef
	for _, p := range inputParams {
		inputs = append(inputs, engine.ParameterDef{Key: p.ParamKey, Name: p.ParamName, DataType: p.DataType})
	}
	var outputs []engine.ParameterDef
	for _, p := range outputParams {
		outputs = append(outputs, engine.ParameterDef{Key: p.ParamKey, Name: p.ParamName, DataType: p.DataType})
	}
	var variables []engine.VariableDef
	for _, v := range variableInfos {
		variables = append(variables, engine.VariableDef{Key: v.VariableKey, Name: v.VariableName, VariableType: v.VariableType, DataType: v.DataType})
	}

	flowDef := &engine.FlowDef{
		InstanceID:  instanceID,
		FlowKey:     flowKey,
		FlowType:    fd.FlowType,
		FlowContent: fd.FlowContent,
		Inputs:      inputs,
		Outputs:     outputs,
		Variables:   variables,
	}

	result := engine.Execute(flowDef, flowData)
	return &FlowResult{
		FlowInstanceID: instanceID,
		Status:         result.Status,
		Data:           result.Data,
	}, nil
}

func GetAsyncFlowResult(instanceID string) (map[string]interface{}, error) {
	result, ok := cache.GetCache().Get(instanceID)
	if !ok {
		return nil, errors.New("流程执行结果不存在或尚未完成")
	}
	if r, ok := result.(*engine.ExecutionResult); ok {
		return r.Data, nil
	}
	return nil, errors.New("结果格式异常")
}

func randomString(n int) string {
	const letters = "abcdefghijklmnopqrstuvwxyz0123456789"
	b := make([]byte, n)
	for i := range b {
		b[i] = letters[rand.Intn(len(letters))]
	}
	return string(b)
}
