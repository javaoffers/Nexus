package handler

import (
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/nexus-flow/nexus/internal/middleware"
	"github.com/nexus-flow/nexus/internal/model"
	"github.com/nexus-flow/nexus/internal/service"
	"github.com/nexus-flow/nexus/pkg/response"
)

type tokenAddParam struct {
	TokenDesc string `json:"tokenDesc" binding:"required"`
}

func AddToken(c *gin.Context) {
	var param tokenAddParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	userID := c.GetInt64(middleware.ContextUserID)
	tokenValue, err := service.CreateToken(param.TokenDesc, userID)
	if err != nil {
		response.Error(c, 9001, err.Error())
		return
	}
	response.OK(c, tokenValue)
}

func DeleteToken(c *gin.Context) {
	id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
	if err := service.DeleteToken(id); err != nil {
		response.Error(c, 9002, err.Error())
		return
	}
	response.OK(c, true)
}

func PageTokens(c *gin.Context) {
	var param model.PageParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	list, total, err := service.PageTokens(&param)
	if err != nil {
		response.Error(c, 9003, err.Error())
		return
	}
	response.OKPage(c, total, list)
}
