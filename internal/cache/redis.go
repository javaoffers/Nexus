package cache

import (
	"context"
	"encoding/json"
	"time"

	"github.com/redis/go-redis/v9"
)

type RedisCache struct {
	client *redis.Client
}

func NewRedisCache(addr, password string) *RedisCache {
	client := redis.NewClient(&redis.Options{
		Addr:     addr,
		Password: password,
		DB:       0,
	})
	return &RedisCache{client: client}
}

func (r *RedisCache) Set(key string, value interface{}) {
	ctx := context.Background()
	data, _ := json.Marshal(value)
	r.client.Set(ctx, key, string(data), 30*time.Minute)
}

func (r *RedisCache) Get(key string) (interface{}, bool) {
	ctx := context.Background()
	val, err := r.client.Get(ctx, key).Result()
	if err != nil {
		return nil, false
	}
	var result interface{}
	if json.Unmarshal([]byte(val), &result) != nil {
		return val, true
	}
	return result, true
}

func (r *RedisCache) Delete(key string) {
	ctx := context.Background()
	r.client.Del(ctx, key)
}
