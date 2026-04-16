package cache

import (
	"time"

	"github.com/nexus-flow/nexus/config"
	gocache "github.com/patrickmn/go-cache"
)

type Cache interface {
	Set(key string, value interface{})
	Get(key string) (interface{}, bool)
	Delete(key string)
}

var instance Cache

func Init() {
	cfg := config.C.Cache
	switch cfg.Type {
	case "redis":
		instance = NewRedisCache(cfg.Redis.Addr, cfg.Redis.Password)
	default:
		instance = NewMemoryCache()
	}
}

func GetCache() Cache {
	return instance
}

// MemoryCache implements Cache using go-cache
type MemoryCache struct {
	c *gocache.Cache
}

func NewMemoryCache() *MemoryCache {
	return &MemoryCache{
		c: gocache.New(30*time.Minute, 60*time.Minute),
	}
}

func (m *MemoryCache) Set(key string, value interface{}) {
	m.c.Set(key, value, gocache.DefaultExpiration)
}

func (m *MemoryCache) Get(key string) (interface{}, bool) {
	return m.c.Get(key)
}

func (m *MemoryCache) Delete(key string) {
	m.c.Delete(key)
}
