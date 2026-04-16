package response

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

type Result struct {
	Success   bool        `json:"success"`
	ErrorCode int         `json:"errorCode"`
	ErrorMsg  string      `json:"errorMsg,omitempty"`
	Result    interface{} `json:"result,omitempty"`
}

type PaginationResult struct {
	Success   bool        `json:"success"`
	ErrorCode int         `json:"errorCode"`
	ErrorMsg  string      `json:"errorMsg,omitempty"`
	Total     int64       `json:"total"`
	Result    interface{} `json:"result"`
}

func OK(c *gin.Context, data interface{}) {
	c.JSON(http.StatusOK, Result{
		Success: true,
		Result:  data,
	})
}

func OKPage(c *gin.Context, total int64, data interface{}) {
	c.JSON(http.StatusOK, PaginationResult{
		Success: true,
		Total:   total,
		Result:  data,
	})
}

func Error(c *gin.Context, code int, msg string) {
	c.JSON(http.StatusOK, Result{
		Success:   false,
		ErrorCode: code,
		ErrorMsg:  msg,
	})
}

func ErrorHTTP(c *gin.Context, httpCode int, code int, msg string) {
	c.JSON(httpCode, Result{
		Success:   false,
		ErrorCode: code,
		ErrorMsg:  msg,
	})
}
