package example

import (
	"math/rand"

	"github.com/gin-gonic/gin"
	"github.com/nexus-flow/nexus/pkg/response"
)

func CreateGoods(c *gin.Context) {
	var param map[string]interface{}
	c.ShouldBindJSON(&param)
	response.OK(c, map[string]interface{}{
		"goodsId":   rand.Int63n(100000),
		"goodsName": param["goodsName"],
		"price":     param["price"],
	})
}

func QueryGoods(c *gin.Context) {
	var param map[string]interface{}
	c.ShouldBindJSON(&param)
	response.OK(c, map[string]interface{}{
		"goodsId":   param["goodsId"],
		"goodsName": "示例商品",
		"price":     99.99,
		"stock":     100,
	})
}
