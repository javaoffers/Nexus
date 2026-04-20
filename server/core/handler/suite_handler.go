package handler

import (
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/nexus-flow/nexus/core/model"
	"github.com/nexus-flow/nexus/core/service"
	"github.com/nexus-flow/nexus/pkg/response"
)

type suiteAddParam struct {
	SuiteCode  string `json:"suiteCode"`
	SuiteName  string `json:"suiteName" binding:"required"`
	SuiteImage string `json:"suiteImage"`
	SuiteDesc  string `json:"suiteDesc"`
}

type suiteUpdateParam struct {
	ID         int64  `json:"id" binding:"required"`
	SuiteCode  string `json:"suiteCode"`
	SuiteName  string `json:"suiteName"`
	SuiteImage string `json:"suiteImage"`
	SuiteDesc  string `json:"suiteDesc"`
}

type suiteQueryParam struct {
	model.PageParam
	SuiteName string `json:"suiteName"`
	SuiteFlag *int   `json:"suiteFlag"`
}

func AddSuite(c *gin.Context) {
	var param suiteAddParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	s := &model.Suite{
		SuiteCode:  param.SuiteCode,
		SuiteName:  param.SuiteName,
		SuiteImage: param.SuiteImage,
		SuiteDesc:  param.SuiteDesc,
	}
	if err := service.CreateSuite(s); err != nil {
		response.Error(c, 2001, err.Error())
		return
	}
	response.OK(c, true)
}

func UpdateSuite(c *gin.Context) {
	var param suiteUpdateParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	s := &model.Suite{
		SuiteCode:  param.SuiteCode,
		SuiteName:  param.SuiteName,
		SuiteImage: param.SuiteImage,
		SuiteDesc:  param.SuiteDesc,
	}
	s.ID = param.ID
	if err := service.UpdateSuite(s); err != nil {
		response.Error(c, 2002, err.Error())
		return
	}
	response.OK(c, true)
}

func DeleteSuite(c *gin.Context) {
	id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
	if err := service.DeleteSuite(id); err != nil {
		response.Error(c, 2003, err.Error())
		return
	}
	response.OK(c, true)
}

func ListSuites(c *gin.Context) {
	list, err := service.ListSuites()
	if err != nil {
		response.Error(c, 2004, err.Error())
		return
	}
	response.OK(c, list)
}

func PageSuites(c *gin.Context) {
	var param suiteQueryParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	list, total, err := service.PageSuites(&param.PageParam, param.SuiteName, param.SuiteFlag)
	if err != nil {
		response.Error(c, 2005, err.Error())
		return
	}
	response.OKPage(c, total, list)
}

func SuiteMarketPage(c *gin.Context) {
	var param suiteQueryParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	list, total, err := service.PageSuites(&param.PageParam, param.SuiteName, param.SuiteFlag)
	if err != nil {
		response.Error(c, 2006, err.Error())
		return
	}
	response.OKPage(c, total, list)
}
