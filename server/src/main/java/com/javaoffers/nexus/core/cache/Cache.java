package com.javaoffers.nexus.core.cache;

/**
 * 对应 Go core/cache/cache.go 的 Cache 接口。
 */
public interface Cache {
    void set(String key, Object value);
    Object get(String key);
    void delete(String key);
}
