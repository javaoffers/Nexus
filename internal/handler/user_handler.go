package handler

import (
	"github.com/gin-gonic/gin"
	"github.com/nexus-flow/nexus/internal/middleware"
	"github.com/nexus-flow/nexus/internal/service"
	"github.com/nexus-flow/nexus/pkg/response"
)

type loginParam struct {
	UserName string `json:"userName" binding:"required"`
	Password string `json:"password" binding:"required"`
}

type updatePasswordParam struct {
	UserID      int64  `json:"userId"`
	OldPassword string `json:"oldPassword" binding:"required"`
	NewPassword string `json:"newPassword" binding:"required"`
}

func Login(c *gin.Context) {
	var param loginParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	result, err := service.Login(param.UserName, param.Password)
	if err != nil {
		response.Error(c, 1001, err.Error())
		return
	}
	response.OK(c, result)
}

func GetUserInfo(c *gin.Context) {
	userID := c.GetInt64(middleware.ContextUserID)
	info, err := service.GetUserInfo(userID)
	if err != nil {
		response.Error(c, 1002, err.Error())
		return
	}
	response.OK(c, info)
}

func UpdatePassword(c *gin.Context) {
	var param updatePasswordParam
	if err := c.ShouldBindJSON(&param); err != nil {
		response.Error(c, 400, "参数错误")
		return
	}
	userID := c.GetInt64(middleware.ContextUserID)
	if param.UserID > 0 {
		userID = param.UserID
	}
	if err := service.UpdatePassword(userID, param.OldPassword, param.NewPassword); err != nil {
		response.Error(c, 1003, err.Error())
		return
	}
	response.OK(c, true)
}

func GetProductInfo(c *gin.Context) {
	response.OK(c, "v1.5.0")
}
