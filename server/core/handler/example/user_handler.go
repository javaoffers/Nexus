package example

import (
	"math/rand"

	"github.com/gin-gonic/gin"
	"github.com/nexus-flow/nexus/pkg/response"
)

func ExampleLogin(c *gin.Context) {
	var param map[string]interface{}
	c.ShouldBindJSON(&param)
	response.OK(c, map[string]interface{}{
		"userId":   rand.Int63n(100000),
		"userName": param["userName"],
		"token":    "example-token-abc123",
	})
}

func ExampleRegister(c *gin.Context) {
	var param map[string]interface{}
	c.ShouldBindJSON(&param)
	response.OK(c, map[string]interface{}{
		"userId":   rand.Int63n(100000),
		"userName": param["userName"],
		"success":  true,
	})
}

func ExampleUserInfo(c *gin.Context) {
	var param map[string]interface{}
	c.ShouldBindJSON(&param)
	response.OK(c, map[string]interface{}{
		"userId":   param["userId"],
		"userName": "示例用户",
		"email":    "user@example.com",
		"phone":    "13800138000",
	})
}
