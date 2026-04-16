package handler

import (
	"github.com/gin-gonic/gin"
	"github.com/nexus-flow/nexus/core/service"
	"github.com/nexus-flow/nexus/pkg/response"
)

// Open API trigger handlers

func OpenTriggerFlow(c *gin.Context) {
	flowVersion := c.Param("flowVersion")
	flowKey := c.Param("flowKey")

	var flowData map[string]interface{}

	if c.Request.Method == "GET" {
		flowData = make(map[string]interface{})
		for k, v := range c.Request.URL.Query() {
			if len(v) == 1 {
				flowData[k] = v[0]
			} else {
				flowData[k] = v
			}
		}
	} else {
		var param triggerDataParam
		if err := c.ShouldBindJSON(&param); err != nil {
			response.Error(c, 400, "参数错误")
			return
		}
		flowData = param.FlowData
	}

	result, err := service.TriggerFlow(flowKey, flowVersion, flowData)
	if err != nil {
		response.Error(c, 8001, err.Error())
		return
	}
	response.OK(c, result)
}

func OpenGetAsyncFlowResult(c *gin.Context) {
	instanceID := c.Param("flowInstanceId")
	data, err := service.GetAsyncFlowResult(instanceID)
	if err != nil {
		response.Error(c, 8002, err.Error())
		return
	}
	response.OK(c, data)
}
