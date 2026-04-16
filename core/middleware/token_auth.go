package middleware

import (
	"encoding/base64"
	"encoding/json"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/nexus-flow/nexus/core/repository"
	"github.com/nexus-flow/nexus/pkg/response"
)

type openAPITokenVO struct {
	UserID    int64 `json:"userId"`
	Timestamp int64 `json:"timestamp"`
}

func TokenAuth() gin.HandlerFunc {
	return func(c *gin.Context) {
		tokenValue := c.GetHeader("Nexus-Token")
		if tokenValue == "" {
			tokenValue = c.Query("nexusToken")
		}
		if tokenValue == "" {
			response.ErrorHTTP(c, http.StatusUnauthorized, 401, "缺少Token")
			c.Abort()
			return
		}

		exists, err := repository.TokenValueExists(tokenValue)
		if err != nil || !exists {
			response.ErrorHTTP(c, http.StatusUnauthorized, 401, "Token无效")
			c.Abort()
			return
		}

		decoded, err := base64.URLEncoding.DecodeString(tokenValue)
		if err == nil {
			var vo openAPITokenVO
			if json.Unmarshal(decoded, &vo) == nil && vo.UserID > 0 {
				c.Set(ContextUserID, vo.UserID)
			}
		}

		c.Next()
	}
}
