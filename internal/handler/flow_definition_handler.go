package handler

import (
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/nexus-flow/nexus/internal/model"
	"github.com/nexus-flow/nexus/internal/service"
	"github.com/nexus-flow/nexus/pkg/response"
)

type flowDefAddParam struct {
	FlowName        string      `json:"flowName" binding:"required"`
	FlowType        string      `json:"flowType" binding:"required"`
	Remark          string      `json:"remark"`
	FlowInputParams interface{} `json:"flowInputParams"`
	FlowOutputParams interface{} `json:"flowOutputParams"`
}

type flowDefUpdateParam struct {
	ID               int64       `json:"id" binding:"required"`
	FlowName         string      `json:"flowName"`
	FlowType         string      `json:"flowType"`
	Remark           string      `json:"remark"`
	FlowInputParams  interface{} `json:"flowInputParams"`
	FlowOutputParams interface{} `json:"flowOutputParams"`
}

type flowDefContentParam struct {
	ID            int64       `json:"id" binding:"required"`
	FlowContent   string      `json:"flowContent"`
	FlowVariables interface{} `json:"flowVariables"`
}

type flowDefDeployParam struct {
	FlowDefinitionID int64  `json:"flowDefinitionId" binding:"required"`
	FlowDeployVersion string `json:"flowDeployVersion" binding:"required"`
	FlowVersionRemark string `json:"flowVersionRemark"`
}

type flowDefPageParam struct {
	model.PageParam
	FlowName string `json:"flowName"`
	FlowType string `json:"flowType"`
}

type triggerDataParam struct {
	FlowData map[string]interface{} `json:"flowData"`
}

func AddFlowDefinition(c *gin.Context) {
	var param flowDefAddParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	if err := service.CreateFlowDefinition(param.FlowName, param.FlowType, param.Remark, param.FlowInputParams, param.FlowOutputParams); err != nil {
		response.Error(c, 5001, err.Error())
		return
	}
	response.OK(c, true)
}

func UpdateFlowDefinition(c *gin.Context) {
	var param flowDefUpdateParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	if err := service.UpdateFlowDefinition(param.ID, param.FlowName, param.FlowType, param.Remark, param.FlowInputParams, param.FlowOutputParams); err != nil {
		response.Error(c, 5002, err.Error())
		return
	}
	response.OK(c, true)
}

func SaveFlowDefinitionContent(c *gin.Context) {
	var param flowDefContentParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	if err := service.SaveFlowDefinitionContent(param.ID, param.FlowContent, param.FlowVariables); err != nil {
		response.Error(c, 5003, err.Error())
		return
	}
	response.OK(c, true)
}

func DeleteFlowDefinition(c *gin.Context) {
	id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
	if err := service.DeleteFlowDefinition(id); err != nil {
		response.Error(c, 5004, err.Error())
		return
	}
	response.OK(c, true)
}

func GetFlowDefinitionInfo(c *gin.Context) {
	id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
	info, err := service.GetFlowDefinitionInfo(id)
	if err != nil {
		response.Error(c, 5005, err.Error())
		return
	}
	response.OK(c, info)
}

func PageFlowDefinitions(c *gin.Context) {
	var param flowDefPageParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	list, total, err := service.PageFlowDefinitions(&param.PageParam, param.FlowName, param.FlowType)
	if err != nil {
		response.Error(c, 5006, err.Error())
		return
	}
	response.OKPage(c, total, list)
}

func DeployFlowDefinition(c *gin.Context) {
	var param flowDefDeployParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	if err := service.DeployFlowDefinition(param.FlowDefinitionID, param.FlowDeployVersion, param.FlowVersionRemark); err != nil {
		response.Error(c, 5007, err.Error())
		return
	}
	response.OK(c, true)
}

func DebugFlowDefinition(c *gin.Context) {
	flowKey := c.Param("flowKey")
	var param triggerDataParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	result, err := service.TriggerFlowByDefinition(flowKey, param.FlowData)
	if err != nil {
		response.Error(c, 5008, err.Error())
		return
	}
	response.OK(c, result)
}

// Template market - simplified
func TemplateMarketPage(c *gin.Context) {
	var param flowDefPageParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	list, total, err := service.PageFlowDefinitions(&param.PageParam, param.FlowName, param.FlowType)
	if err != nil {
		response.Error(c, 5009, err.Error())
		return
	}
	response.OKPage(c, total, list)
}
