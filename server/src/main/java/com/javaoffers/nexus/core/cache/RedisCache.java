package com.javaoffers.nexus.core.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存，对应 Go 的 RedisCache（go-redis，30min 过期，JSON 序列化）。
 */
@Component
@ConditionalOnProperty(prefix = "nexus.cache", name = "type", havingValue = "redis")
public class RedisCache implements Cache {

    private final StringRedisTemplate redis;
    private final ObjectMapper mapper = new ObjectMapper();

    public RedisCache(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void set(String key, Object value) {
        try {
            redis.opsForValue().set(key, mapper.writeValueAsString(value), 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            // ignore serialization failure
        }
    }

    @Override
    public Object get(String key) {
        String val = redis.opsForValue().get(key);
        if (val == null) {
            return null;
        }
        try {
            return mapper.readValue(val, Object.class);
        } catch (Exception e) {
            return val;
        }
    }

    @Override
    public void delete(String key) {
        redis.delete(key);
    }
}
