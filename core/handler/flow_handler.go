package handler

import (
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/nexus-flow/nexus/core/model"
	"github.com/nexus-flow/nexus/core/service"
	"github.com/nexus-flow/nexus/pkg/response"
)

type flowInfoPageParam struct {
	model.PageParam
	FlowName string `json:"flowName"`
	FlowType string `json:"flowType"`
}

func DeleteFlow(c *gin.Context) {
	id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
	if err := service.DeleteFlowInfo(id); err != nil {
		response.Error(c, 6001, err.Error())
		return
	}
	response.OK(c, true)
}

func PageFlows(c *gin.Context) {
	var param flowInfoPageParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	list, total, err := service.PageFlowInfos(&param.PageParam, param.FlowName, param.FlowType)
	if err != nil {
		response.Error(c, 6002, err.Error())
		return
	}
	response.OKPage(c, total, list)
}
