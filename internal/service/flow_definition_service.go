package service

import (
	"encoding/json"
	"errors"
	"fmt"
	"math/rand"
	"time"

	"github.com/nexus-flow/nexus/internal/model"
	"github.com/nexus-flow/nexus/internal/repository"
)

type FlowDefinitionInfoDTO struct {
	ID              int64       `json:"id"`
	FlowKey         string      `json:"flowKey"`
	FlowName        string      `json:"flowName"`
	FlowType        string      `json:"flowType"`
	FlowContent     string      `json:"flowContent"`
	Remark          string      `json:"remark,omitempty"`
	CreatedAt       *time.Time  `json:"createdAt,omitempty"`
	FlowInputParams interface{} `json:"flowInputParams,omitempty"`
	FlowOutputParams interface{} `json:"flowOutputParams,omitempty"`
	FlowVariables   interface{} `json:"flowVariables,omitempty"`
}

const defaultFlowContent = `[{"key":"start_1","name":"开始","outgoings":["end_1"],"incomings":[],"elementType":"START"},{"key":"end_1","name":"结束","outgoings":[],"incomings":["start_1"],"elementType":"END"}]`

func CreateFlowDefinition(flowName, flowType, remark string, inputParams, outputParams interface{}) error {
	flowKey := generateFlowKey()
	fd := &model.FlowDefinition{
		FlowKey:     flowKey,
		FlowName:    flowName,
		FlowType:    flowType,
		FlowContent: defaultFlowContent,
		Remark:      remark,
	}
	if err := repository.CreateFlowDefinition(fd); err != nil {
		return err
	}

	saveFlowDefinitionParams(fd.ID, inputParams, outputParams)
	return nil
}

func UpdateFlowDefinition(id int64, flowName, flowType, remark string, inputParams, outputParams interface{}) error {
	fd := &model.FlowDefinition{
		FlowName: flowName,
		FlowType: flowType,
		Remark:   remark,
	}
	fd.ID = id
	if err := repository.UpdateFlowDefinition(fd); err != nil {
		return err
	}

	saveFlowDefinitionParams(id, inputParams, outputParams)
	return nil
}

func SaveFlowDefinitionContent(id int64, content string, variables interface{}) error {
	if err := repository.UpdateFlowDefinitionContent(id, content); err != nil {
		return err
	}

	// Save variables
	repository.DeleteVariableInfosByFlowDefID(id)
	if variables != nil {
		data, _ := json.Marshal(variables)
		var vars []model.VariableInfo
		json.Unmarshal(data, &vars)
		for i := range vars {
			vars[i].ID = 0
			vars[i].FlowDefinitionID = id
		}
		repository.BatchCreateVariableInfos(vars)
	}
	return nil
}

func DeleteFlowDefinition(id int64) error {
	repository.DeleteParametersBySource("flow", id)
	repository.DeleteVariableInfosByFlowDefID(id)
	return repository.DeleteFlowDefinition(id)
}

func GetFlowDefinitionInfo(id int64) (*FlowDefinitionInfoDTO, error) {
	fd, err := repository.GetFlowDefinitionByID(id)
	if err != nil {
		return nil, errors.New("流程定义不存在")
	}
	return buildFlowDefinitionInfoDTO(fd)
}

func PageFlowDefinitions(p *model.PageParam, flowName, flowType string) ([]FlowDefinitionInfoDTO, int64, error) {
	list, total, err := repository.PageFlowDefinitions(p, flowName, flowType)
	if err != nil {
		return nil, 0, err
	}
	dtos := make([]FlowDefinitionInfoDTO, 0, len(list))
	for _, fd := range list {
		dto, _ := buildFlowDefinitionInfoDTO(&fd)
		if dto != nil {
			dtos = append(dtos, *dto)
		}
	}
	return dtos, total, nil
}

func DeployFlowDefinition(flowDefID int64, version, remark string) error {
	fd, err := repository.GetFlowDefinitionByID(flowDefID)
	if err != nil {
		return errors.New("流程定义不存在")
	}

	// Ensure flow_info exists
	flowInfo, err := repository.GetFlowInfoByKey(fd.FlowKey)
	if err != nil {
		flowInfo = &model.FlowInfo{
			FlowKey:  fd.FlowKey,
			FlowName: fd.FlowName,
			FlowType: fd.FlowType,
			Remark:   fd.Remark,
		}
		repository.CreateFlowInfo(flowInfo)
	}

	inputs, _ := repository.ListParametersBySourceAndType("flow", fd.ID, 1)
	outputs, _ := repository.ListParametersBySourceAndType("flow", fd.ID, 2)
	variables, _ := repository.ListVariableInfosByFlowDefID(fd.ID)

	inputsJSON, _ := json.Marshal(inputs)
	outputsJSON, _ := json.Marshal(outputs)
	varsJSON, _ := json.Marshal(variables)

	fv := &model.FlowVersion{
		FlowID:            flowInfo.ID,
		FlowVersion:       version,
		FlowVersionStatus: 0,
		FlowVersionRemark: remark,
		FlowContent:       fd.FlowContent,
		Inputs:            string(inputsJSON),
		Outputs:           string(outputsJSON),
		Variables:         string(varsJSON),
	}
	return repository.CreateFlowVersion(fv)
}

func buildFlowDefinitionInfoDTO(fd *model.FlowDefinition) (*FlowDefinitionInfoDTO, error) {
	inputParams, _ := repository.ListParametersBySourceAndType("flow", fd.ID, 1)
	outputParams, _ := repository.ListParametersBySourceAndType("flow", fd.ID, 2)
	variables, _ := repository.ListVariableInfosByFlowDefID(fd.ID)

	flowContent := fd.FlowContent
	if flowContent == "" {
		flowContent = defaultFlowContent
	}

	return &FlowDefinitionInfoDTO{
		ID:               fd.ID,
		FlowKey:          fd.FlowKey,
		FlowName:         fd.FlowName,
		FlowType:         fd.FlowType,
		FlowContent:      flowContent,
		Remark:           fd.Remark,
		CreatedAt:        fd.CreatedAt,
		FlowInputParams:  inputParams,
		FlowOutputParams: outputParams,
		FlowVariables:    variables,
	}, nil
}

func saveFlowDefinitionParams(fdID int64, inputParams, outputParams interface{}) {
	repository.DeleteParametersBySource("flow", fdID)

	var allParams []model.Parameter
	if inputParams != nil {
		data, _ := json.Marshal(inputParams)
		var params []model.Parameter
		json.Unmarshal(data, &params)
		for i := range params {
			params[i].SourceType = "flow"
			params[i].SourceID = &fdID
			pt := 1
			params[i].ParamType = &pt
		}
		allParams = append(allParams, params...)
	}
	if outputParams != nil {
		data, _ := json.Marshal(outputParams)
		var params []model.Parameter
		json.Unmarshal(data, &params)
		for i := range params {
			params[i].SourceType = "flow"
			params[i].SourceID = &fdID
			pt := 2
			params[i].ParamType = &pt
		}
		allParams = append(allParams, params...)
	}
	if len(allParams) > 0 {
		repository.BatchCreateParameters(allParams)
	}
}

func generateFlowKey() string {
	const chars = "abcdefghijklmnopqrstuvwxyz0123456789"
	b := make([]byte, 8)
	for i := range b {
		b[i] = chars[rand.Intn(len(chars))]
	}
	return fmt.Sprintf("flow_%s", string(b))
}
