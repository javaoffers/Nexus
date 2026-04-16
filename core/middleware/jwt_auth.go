package middleware

import (
	"net/http"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/nexus-flow/nexus/pkg/auth"
	"github.com/nexus-flow/nexus/pkg/response"
)

const ContextUserID = "userId"

func JWTAuth() gin.HandlerFunc {
	return func(c *gin.Context) {
		tokenStr := c.GetHeader("Authorization")
		if tokenStr == "" {
			response.ErrorHTTP(c, http.StatusUnauthorized, 401, "未登录")
			c.Abort()
			return
		}
		tokenStr = strings.TrimPrefix(tokenStr, "Bearer ")

		claims, err := auth.ParseToken(tokenStr)
		if err != nil {
			response.ErrorHTTP(c, http.StatusUnauthorized, 401, "登录已过期")
			c.Abort()
			return
		}

		c.Set(ContextUserID, claims.UserID)
		c.Next()
	}
}
