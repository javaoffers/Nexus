package handler

import (
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/nexus-flow/nexus/core/model"
	"github.com/nexus-flow/nexus/core/service"
	"github.com/nexus-flow/nexus/pkg/response"
)

type flowVersionPageParam struct {
	model.PageParam
	FlowID            *int64 `json:"flowId"`
	FlowVersionStatus *int   `json:"flowVersionStatus"`
}

type flowVersionStatusParam struct {
	FlowVersionID     int64 `json:"flowVersionId" binding:"required"`
	FlowVersionStatus int   `json:"flowVersionStatus"`
}

func UpdateFlowVersionStatus(c *gin.Context) {
	var param flowVersionStatusParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	if err := service.UpdateFlowVersionStatus(param.FlowVersionID, param.FlowVersionStatus); err != nil {
		response.Error(c, 7001, err.Error())
		return
	}
	response.OK(c, true)
}

func DeleteFlowVersion(c *gin.Context) {
	id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
	if err := service.DeleteFlowVersion(id); err != nil {
		response.Error(c, 7002, err.Error())
		return
	}
	response.OK(c, true)
}

func PageFlowVersions(c *gin.Context) {
	var param flowVersionPageParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	list, total, err := service.PageFlowVersions(&param.PageParam, param.FlowID, param.FlowVersionStatus)
	if err != nil {
		response.Error(c, 7003, err.Error())
		return
	}
	response.OKPage(c, total, list)
}

func GetLatestVersion(c *gin.Context) {
	flowKey := c.Param("flowKey")
	version, _ := service.GetLatestVersion(flowKey)
	response.OK(c, version)
}

func TriggerFlowVersion(c *gin.Context) {
	flowVersion := c.Param("flowVersion")
	flowKey := c.Param("flowKey")
	var param triggerDataParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	result, err := service.TriggerFlow(flowKey, flowVersion, param.FlowData)
	if err != nil {
		response.Error(c, 7004, err.Error())
		return
	}
	response.OK(c, result)
}

// 获取异步流程结果
func GetAsyncFlowResult(c *gin.Context) {
	instanceID := c.Param("flowInstanceId")
	data, err := service.GetAsyncFlowResult(instanceID)
	if err != nil {
		response.Error(c, 7005, err.Error())
		return
	}
	response.OK(c, data)
}
