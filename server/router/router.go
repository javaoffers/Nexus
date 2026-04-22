package router

import (
	"github.com/gin-gonic/gin"
	"github.com/nexus-flow/nexus/core/handler"
	"github.com/nexus-flow/nexus/core/middleware"
)

func Setup(r *gin.Engine) {
	r.Use(middleware.CORS())

	// ---- Console API (JWT auth) ----
	api := r.Group("/api/v1")
	{
		// Public: login
		api.POST("/user/login", handler.Login)
		api.GET("/user/product/info", handler.GetProductInfo)

		// Protected routes
		auth := api.Group("")
		auth.Use(middleware.JWTAuth())
		{
			// User
			auth.POST("/user/getUserInfo", handler.GetUserInfo)
			auth.PUT("/user/updatePassword", handler.UpdatePassword)

			// Suite
			auth.POST("/suite/add", handler.AddSuite)
			auth.PUT("/suite/update", handler.UpdateSuite)
			auth.DELETE("/suite/delete/:id", handler.DeleteSuite)
			auth.GET("/suite/list", handler.ListSuites)
			auth.POST("/suite/page", handler.PageSuites)
			auth.POST("/suite/market", handler.SuiteMarketPage)
			auth.POST("/suite/market/classify", func(c *gin.Context) {
				handler.ListSuites(c) // simplified
			})

			// API
			auth.POST("/api/add", handler.AddAPI)
			auth.PUT("/api/update", handler.UpdateAPI)
			auth.DELETE("/api/delete/:id", handler.DeleteAPI)
			auth.GET("/api/info/:id", handler.GetAPIInfo)
			auth.GET("/api/info/code/:code", handler.GetAPIInfoByCode)
			auth.POST("/api/getApiListBySuiteId/:id", handler.GetAPIListBySuiteID)
			auth.POST("/api/getApiListBySuiteCode/:code", handler.GetAPIListBySuiteCode)
			auth.POST("/api/page", handler.PageAPIs)
			auth.POST("/api/debug/:id", handler.DebugAPI)

			// Object
			auth.POST("/object/add", handler.AddObject)
			auth.PUT("/object/update", handler.UpdateObject)
			auth.DELETE("/object/delete/:id", handler.DeleteObject)
			auth.GET("/object/info/:id", handler.GetObjectInfo)
			auth.GET("/object/list", handler.ListObjects)
			auth.POST("/object/page", handler.PageObjects)
			auth.POST("/object/exist/key", handler.CheckObjectKeyExists)

			// DataType
			auth.GET("/dataType/list", handler.GetDataTypeList)

			// Flow Definition
			auth.POST("/flow/definition/add", handler.AddFlowDefinition)
			auth.PUT("/flow/definition/update", handler.UpdateFlowDefinition)
			auth.PUT("/flow/definition/save", handler.SaveFlowDefinitionContent)
			auth.DELETE("/flow/definition/delete/:id", handler.DeleteFlowDefinition)
			auth.GET("/flow/definition/info/:id", handler.GetFlowDefinitionInfo)
			auth.POST("/flow/definition/page", handler.PageFlowDefinitions)
			auth.POST("/flow/definition/debug/:flowKey", handler.DebugFlowDefinition)
			auth.POST("/flow/definition/deploy", handler.DeployFlowDefinition)

			// Flow Info
			auth.DELETE("/flow/delete/:id", handler.DeleteFlow)
			auth.POST("/flow/page", handler.PageFlows)

			// Flow Version
			auth.PUT("/flow/version/status", handler.UpdateFlowVersionStatus)
			auth.DELETE("/flow/version/delete/:id", handler.DeleteFlowVersion)
			auth.POST("/flow/version/page", handler.PageFlowVersions)
			auth.GET("/flow/version/latest/:flowKey", handler.GetLatestVersion)
			auth.POST("/flow/version/trigger/:flowVersion/:flowKey", handler.TriggerFlowVersion)
			auth.GET("/flow/version/getAsyncFlowResult/:flowInstanceId", handler.GetAsyncFlowResult)

			// Token
			auth.POST("/token/add", handler.AddToken)
			auth.DELETE("/token/delete/:id", handler.DeleteToken)
			auth.POST("/token/page", handler.PageTokens)

			// DataSource
			auth.POST("/datasource/add", handler.AddDataSource)
			auth.PUT("/datasource/update", handler.UpdateDataSource)
			auth.DELETE("/datasource/delete/:id", handler.DeleteDataSource)
			auth.GET("/datasource/info/:id", handler.GetDataSourceInfo)
			auth.GET("/datasource/list", handler.ListDataSources)
			auth.POST("/datasource/page", handler.PageDataSources)
			auth.GET("/datasource/connect/:id", handler.TestDataSourceConnection)

			// Template Market
			auth.POST("/template/market", handler.TemplateMarketPage)
			auth.POST("/template/market/classify", func(c *gin.Context) {
				handler.TemplateMarketPage(c) // simplified
			})
		}
	}

	// ---- Open API (Token auth) ----
	open := r.Group("/open/v1")
	open.Use(middleware.TokenAuth())
	{
		open.GET("/flow/trigger/:flowVersion/:flowKey", handler.OpenTriggerFlow)
		open.POST("/flow/trigger/:flowVersion/:flowKey", handler.OpenTriggerFlow)
		open.GET("/flow/getAsyncFlowResult/:flowInstanceId", handler.OpenGetAsyncFlowResult)
	}

}
