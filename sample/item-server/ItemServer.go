package item_server

import (
	"fmt"
	"math/rand"
	"net/http"

	"github.com/gin-gonic/gin"
)

var ParamType string = "type"

var ItemTypes = []string{"food", "phone", "book", "pen"}

func main() {
	r := gin.Default()

	r.GET("/query_item", func(c *gin.Context) {
		param := c.Query(ParamType)
		fmt.Println(param)
		c.JSON(http.StatusOK, gin.H{
			"size": rand.Intn(100),
			"data": []map[string]any{
				{
					"id":        rand.Intn(100),
					"item_name": ItemTypes[rand.Intn(len(ItemTypes))],
				},
			},
		})
	})
	r.Run("localhost:8090")
}
