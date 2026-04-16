package handler

import (
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/nexus-flow/nexus/internal/model"
	"github.com/nexus-flow/nexus/internal/service"
	"github.com/nexus-flow/nexus/pkg/response"
)

type dsQueryParam struct {
	model.PageParam
	DataSourceName string `json:"dataSourceName"`
	DataSourceType string `json:"dataSourceType"`
}

func AddDataSource(c *gin.Context) {
	var ds model.DataSource
	if err := c.ShouldBindJSON(&ds); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	if err := service.CreateDataSource(&ds); err != nil {
		response.Error(c, 10001, err.Error())
		return
	}
	response.OK(c, true)
}

func UpdateDataSource(c *gin.Context) {
	var ds model.DataSource
	if err := c.ShouldBindJSON(&ds); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	if err := service.UpdateDataSource(&ds); err != nil {
		response.Error(c, 10002, err.Error())
		return
	}
	response.OK(c, true)
}

func DeleteDataSource(c *gin.Context) {
	id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
	if err := service.DeleteDataSource(id); err != nil {
		response.Error(c, 10003, err.Error())
		return
	}
	response.OK(c, true)
}

func GetDataSourceInfo(c *gin.Context) {
	id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
	info, err := service.GetDataSourceByID(id)
	if err != nil {
		response.Error(c, 10004, err.Error())
		return
	}
	response.OK(c, info)
}

func ListDataSources(c *gin.Context) {
	list, err := service.ListDataSources()
	if err != nil {
		response.Error(c, 10005, err.Error())
		return
	}
	response.OK(c, list)
}

func PageDataSources(c *gin.Context) {
	var param dsQueryParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	list, total, err := service.PageDataSources(&param.PageParam, param.DataSourceName, param.DataSourceType)
	if err != nil {
		response.Error(c, 10006, err.Error())
		return
	}
	response.OKPage(c, total, list)
}

func TestDataSourceConnection(c *gin.Context) {
	id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
	if err := service.TestDataSourceConnection(id); err != nil {
		response.Error(c, 10007, err.Error())
		return
	}
	response.OK(c, true)
}
