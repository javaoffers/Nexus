package service

import (
	"github.com/nexus-flow/nexus/internal/model"
	"github.com/nexus-flow/nexus/internal/repository"
)

type FlowInfoDTO struct {
	ID       int64  `json:"id"`
	FlowKey  string `json:"flowKey"`
	FlowName string `json:"flowName"`
	FlowType string `json:"flowType"`
	Remark   string `json:"remark,omitempty"`
}

func DeleteFlowInfo(id int64) error {
	return repository.DeleteFlowInfo(id)
}

func PageFlowInfos(p *model.PageParam, flowName, flowType string) ([]FlowInfoDTO, int64, error) {
	list, total, err := repository.PageFlowInfos(p, flowName, flowType)
	if err != nil {
		return nil, 0, err
	}
	dtos := make([]FlowInfoDTO, 0, len(list))
	for _, f := range list {
		dtos = append(dtos, FlowInfoDTO{
			ID:       f.ID,
			FlowKey:  f.FlowKey,
			FlowName: f.FlowName,
			FlowType: f.FlowType,
			Remark:   f.Remark,
		})
	}
	return dtos, total, nil
}
