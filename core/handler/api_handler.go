package handler

import (
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/nexus-flow/nexus/core/model"
	"github.com/nexus-flow/nexus/core/service"
	"github.com/nexus-flow/nexus/pkg/response"
)

type apiAddParam struct {
	SuiteID               int64       `json:"suiteId" binding:"required"`
	ApiCode               string      `json:"apiCode"`
	ApiURL                string      `json:"apiUrl" binding:"required"`
	ApiName               string      `json:"apiName" binding:"required"`
	ApiDesc               string      `json:"apiDesc"`
	ApiRequestType        string      `json:"apiRequestType" binding:"required"`
	ApiRequestContentType string      `json:"apiRequestContentType"`
	ApiHeaders            interface{} `json:"apiHeaders"`
	ApiInputParams        interface{} `json:"apiInputParams"`
	ApiOutputParams       interface{} `json:"apiOutputParams"`
}

type apiUpdateParam struct {
	ID                    int64       `json:"id" binding:"required"`
	SuiteID               int64       `json:"suiteId"`
	ApiURL                string      `json:"apiUrl"`
	ApiName               string      `json:"apiName"`
	ApiDesc               string      `json:"apiDesc"`
	ApiRequestType        string      `json:"apiRequestType"`
	ApiRequestContentType string      `json:"apiRequestContentType"`
	ApiHeaders            interface{} `json:"apiHeaders"`
	ApiInputParams        interface{} `json:"apiInputParams"`
	ApiOutputParams       interface{} `json:"apiOutputParams"`
}

type apiQueryParam struct {
	model.PageParam
	SuiteID *int64 `json:"suiteId"`
	ApiName string `json:"apiName"`
	ApiURL  string `json:"apiUrl"`
}

type apiDebugParam struct {
	HeaderData     map[string]interface{} `json:"headerData"`
	InputParamData map[string]interface{} `json:"inputParamData"`
}

func AddAPI(c *gin.Context) {
	var param apiAddParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	a := &model.API{
		SuiteID:               param.SuiteID,
		ApiCode:               param.ApiCode,
		ApiURL:                param.ApiURL,
		ApiName:               param.ApiName,
		ApiDesc:               param.ApiDesc,
		ApiRequestType:        param.ApiRequestType,
		ApiRequestContentType: param.ApiRequestContentType,
	}
	if err := service.CreateAPI(a); err != nil {
		response.Error(c, 3001, err.Error())
		return
	}
	// Save parameters
	service.SaveAPIParameters(a.ID, "api", toParamSlice(param.ApiHeaders), toParamSlice(param.ApiInputParams), toParamSlice(param.ApiOutputParams))
	response.OK(c, true)
}

func UpdateAPI(c *gin.Context) {
	var param apiUpdateParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	a := &model.API{
		SuiteID:               param.SuiteID,
		ApiURL:                param.ApiURL,
		ApiName:               param.ApiName,
		ApiDesc:               param.ApiDesc,
		ApiRequestType:        param.ApiRequestType,
		ApiRequestContentType: param.ApiRequestContentType,
	}
	a.ID = param.ID
	if err := service.UpdateAPI(a); err != nil {
		response.Error(c, 3002, err.Error())
		return
	}
	service.SaveAPIParameters(a.ID, "api", toParamSlice(param.ApiHeaders), toParamSlice(param.ApiInputParams), toParamSlice(param.ApiOutputParams))
	response.OK(c, true)
}

func DeleteAPI(c *gin.Context) {
	id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
	if err := service.DeleteAPI(id); err != nil {
		response.Error(c, 3003, err.Error())
		return
	}
	response.OK(c, true)
}

func GetAPIInfo(c *gin.Context) {
	id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
	info, err := service.GetAPIInfo(id)
	if err != nil {
		response.Error(c, 3004, err.Error())
		return
	}
	response.OK(c, info)
}

func GetAPIInfoByCode(c *gin.Context) {
	code := c.Param("code")
	info, err := service.GetAPIInfoByCode(code)
	if err != nil {
		response.Error(c, 3005, err.Error())
		return
	}
	response.OK(c, info)
}

func GetAPIListBySuiteID(c *gin.Context) {
	id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
	list, err := service.ListAPIsBySuiteID(id)
	if err != nil {
		response.Error(c, 3006, err.Error())
		return
	}
	response.OK(c, list)
}

func GetAPIListBySuiteCode(c *gin.Context) {
	code := c.Param("code")
	list, err := service.ListAPIsBySuiteCode(code)
	if err != nil {
		response.Error(c, 3007, err.Error())
		return
	}
	response.OK(c, list)
}

func PageAPIs(c *gin.Context) {
	var param apiQueryParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	list, total, err := service.PageAPIs(&param.PageParam, param.SuiteID, param.ApiName, param.ApiURL)
	if err != nil {
		response.Error(c, 3008, err.Error())
		return
	}
	response.OKPage(c, total, list)
}

func DebugAPI(c *gin.Context) {
	id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
	var param apiDebugParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	result, err := service.DebugAPI(id, param.HeaderData, param.InputParamData)
	if err != nil {
		response.Error(c, 3009, err.Error())
		return
	}
	response.OK(c, result)
}

func toParamSlice(data interface{}) []model.Parameter {
	if data == nil {
		return nil
	}
	// Type assertion for slice of maps
	if arr, ok := data.([]interface{}); ok {
		params := make([]model.Parameter, 0, len(arr))
		for _, item := range arr {
			if m, ok := item.(map[string]interface{}); ok {
				p := model.Parameter{}
				if v, ok := m["paramKey"].(string); ok {
					p.ParamKey = v
				}
				if v, ok := m["paramName"].(string); ok {
					p.ParamName = v
				}
				if v, ok := m["paramPosition"].(string); ok {
					p.ParamPosition = v
				}
				if v, ok := m["paramDesc"].(string); ok {
					p.ParamDesc = v
				}
				params = append(params, p)
			}
		}
		return params
	}
	return nil
}
