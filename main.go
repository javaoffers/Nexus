package main

import (
	"fmt"
	"log"
	"net/http"
	"os"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/nexus-flow/nexus/config"
	"github.com/nexus-flow/nexus/core/cache"
	"github.com/nexus-flow/nexus/db"
	"github.com/nexus-flow/nexus/router"
)

func main() {
	// Load config
	if err := config.Load(); err != nil {
		log.Fatalf("Failed to load config: %v", err)
	}

	// Init database
	if err := db.Init(); err != nil {
		log.Fatalf("Failed to init database: %v", err)
	}

	// Init cache
	cache.Init()

	// Setup Gin
	gin.SetMode(gin.ReleaseMode)
	r := gin.Default()

	// Register API routes
	router.Setup(r)

	// Serve frontend static files if built
	setupStaticFiles(r)

	port := config.C.Server.Port
	log.Printf("Nexus server starting on port %d", port)
	if err := r.Run(fmt.Sprintf(":%d", port)); err != nil {
		log.Fatalf("Failed to start server: %v", err)
	}
}

func setupStaticFiles(r *gin.Engine) {
	distDir := "console-ui/dist"
	if _, err := os.Stat(distDir); err != nil {
		log.Printf("Frontend dist not found at %s, running in API-only mode", distDir)
		return
	}

	r.Static("/assets", distDir+"/assets")

	// SPA: serve index.html for non-API routes
	r.NoRoute(func(c *gin.Context) {
		path := c.Request.URL.Path
		if strings.HasPrefix(path, "/api/") ||
			strings.HasPrefix(path, "/open/") ||
			strings.HasPrefix(path, "/example/") {
			c.JSON(http.StatusNotFound, gin.H{"error": "not found"})
			return
		}
		// Try to serve the file directly
		filePath := distDir + path
		if _, err := os.Stat(filePath); err == nil {
			c.File(filePath)
			return
		}
		// Fallback to index.html for SPA routing
		c.File(distDir + "/index.html")
	})
}
