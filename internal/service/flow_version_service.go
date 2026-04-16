package service

import (
	"github.com/nexus-flow/nexus/internal/model"
	"github.com/nexus-flow/nexus/internal/repository"
)

type FlowVersionDTO struct {
	ID                int64  `json:"id"`
	FlowName          string `json:"flowName,omitempty"`
	FlowVersion       string `json:"flowVersion"`
	TriggerURL        string `json:"triggerUrl,omitempty"`
	FlowVersionStatus int    `json:"flowVersionStatus"`
	FlowVersionRemark string `json:"flowVersionRemark,omitempty"`
}

func UpdateFlowVersionStatus(id int64, status int) error {
	return repository.UpdateFlowVersionStatus(id, status)
}

func DeleteFlowVersion(id int64) error {
	return repository.DeleteFlowVersion(id)
}

func GetLatestVersion(flowKey string) (string, error) {
	return repository.GetLatestVersion(flowKey)
}

func PageFlowVersions(p *model.PageParam, flowID *int64, status *int) ([]FlowVersionDTO, int64, error) {
	list, total, err := repository.PageFlowVersions(p, flowID, status)
	if err != nil {
		return nil, 0, err
	}
	dtos := make([]FlowVersionDTO, 0, len(list))
	for _, fv := range list {
		dtos = append(dtos, FlowVersionDTO{
			ID:                fv.ID,
			FlowVersion:       fv.FlowVersion,
			FlowVersionStatus: fv.FlowVersionStatus,
			FlowVersionRemark: fv.FlowVersionRemark,
		})
	}
	return dtos, total, nil
}
