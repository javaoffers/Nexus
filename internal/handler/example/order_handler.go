package example

import (
	"fmt"
	"math/rand"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/nexus-flow/nexus/pkg/response"
)

func CreateOrder(c *gin.Context) {
	var param map[string]interface{}
	c.ShouldBindJSON(&param)
	orderNo := fmt.Sprintf("ORD%d%04d", time.Now().Unix(), rand.Intn(10000))
	response.OK(c, map[string]interface{}{
		"orderNo": orderNo,
		"qrCode":  "https://example.com/pay/" + orderNo,
	})
}

func QueryOrder(c *gin.Context) {
	var param map[string]interface{}
	c.ShouldBindJSON(&param)
	response.OK(c, map[string]interface{}{
		"orderNo": param["orderNo"],
		"status":  "PAID",
	})
}
