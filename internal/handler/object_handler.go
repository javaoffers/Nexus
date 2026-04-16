package handler

import (
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/nexus-flow/nexus/internal/model"
	"github.com/nexus-flow/nexus/internal/service"
	"github.com/nexus-flow/nexus/pkg/response"
)

type objectAddParam struct {
	ObjectKey  string      `json:"objectKey" binding:"required"`
	ObjectName string      `json:"objectName" binding:"required"`
	ObjectDesc string      `json:"objectDesc"`
	Props      interface{} `json:"props"`
}

type objectUpdateParam struct {
	ID         int64       `json:"id" binding:"required"`
	ObjectKey  string      `json:"objectKey"`
	ObjectName string      `json:"objectName"`
	ObjectDesc string      `json:"objectDesc"`
	Props      interface{} `json:"props"`
}

type objectQueryParam struct {
	model.PageParam
	ObjectName string `json:"objectName"`
}

type objectKeyParam struct {
	ID        *int64 `json:"id"`
	ObjectKey string `json:"objectKey" binding:"required"`
}

func AddObject(c *gin.Context) {
	var param objectAddParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	o := &model.Object{
		ObjectKey:  param.ObjectKey,
		ObjectName: param.ObjectName,
		ObjectDesc: param.ObjectDesc,
	}
	if err := service.CreateObject(o, param.Props); err != nil {
		response.Error(c, 4001, err.Error())
		return
	}
	response.OK(c, true)
}

func UpdateObject(c *gin.Context) {
	var param objectUpdateParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	o := &model.Object{
		ObjectKey:  param.ObjectKey,
		ObjectName: param.ObjectName,
		ObjectDesc: param.ObjectDesc,
	}
	o.ID = param.ID
	if err := service.UpdateObject(o, param.Props); err != nil {
		response.Error(c, 4002, err.Error())
		return
	}
	response.OK(c, true)
}

func DeleteObject(c *gin.Context) {
	id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
	if err := service.DeleteObject(id); err != nil {
		response.Error(c, 4003, err.Error())
		return
	}
	response.OK(c, true)
}

func GetObjectInfo(c *gin.Context) {
	id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
	info, err := service.GetObjectInfo(id)
	if err != nil {
		response.Error(c, 4004, err.Error())
		return
	}
	response.OK(c, info)
}

func ListObjects(c *gin.Context) {
	list, err := service.ListObjects()
	if err != nil {
		response.Error(c, 4005, err.Error())
		return
	}
	response.OK(c, list)
}

func PageObjects(c *gin.Context) {
	var param objectQueryParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	list, total, err := service.PageObjects(&param.PageParam, param.ObjectName)
	if err != nil {
		response.Error(c, 4006, err.Error())
		return
	}
	response.OKPage(c, total, list)
}

func CheckObjectKeyExists(c *gin.Context) {
	var param objectKeyParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	exists, _ := service.CheckObjectKeyExists(param.ObjectKey, param.ID)
	response.OK(c, exists)
}

func GetDataTypeList(c *gin.Context) {
	list, err := service.GetDataTypeOptions()
	if err != nil {
		response.Error(c, 4007, err.Error())
		return
	}
	response.OK(c, list)
}
