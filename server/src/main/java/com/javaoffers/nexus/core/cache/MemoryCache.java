package com.javaoffers.nexus.core.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 内存缓存，对应 Go 的 MemoryCache（go-cache，默认 30min 过期）。
 */
@Component
@ConditionalOnProperty(prefix = "nexus.cache", name = "type", havingValue = "memory", matchIfMissing = true)
public class MemoryCache implements Cache {

    private final com.github.benmanes.caffeine.cache.Cache<String, Object> cache =
            Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).maximumSize(100_000).build();

    @Override
    public void set(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public Object get(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void delete(String key) {
        cache.invalidate(key);
    }
}
