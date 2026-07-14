package com.javaoffers.nexus.core.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对应 Go engine.VariableManager：流程执行期间的变量存取，支持点号嵌套取值。
 */
public class VariableManager {

    private final Map<String, Object> data = new ConcurrentHashMap<>();

    public void set(String key, Object value) {
        data.put(key, value);
    }

    public boolean contains(String key) {
        if (data.containsKey(key)) {
            return true;
        }
        return key.contains(".") && getNested(key) != null;
    }

    public Object get(String key) {
        if (data.containsKey(key)) {
            return data.get(key);
        }
        if (key.contains(".")) {
            return getNested(key);
        }
        return null;
    }

    public Map<String, Object> getAll() {
        return new HashMap<>(data);
    }

    @SuppressWarnings("unchecked")
    private Object getNested(String key) {
        String[] parts = key.split("\\.");
        if (parts.length < 2) {
            return null;
        }
        Object val = data.get(parts[0]);
        for (int i = 1; i < parts.length && val != null; i++) {
            if (val instanceof Map) {
                val = ((Map<String, Object>) val).get(parts[i]);
            } else {
                return null;
            }
        }
        return val;
    }

    /**
     * 对应 Go ResolveValue：VARIABLE 取变量；CONSTANT 取字面量；其他先取变量，取不到回退字面量。
     */
    public Object resolveValue(String source, String sourceType) {
        if ("CONSTANT".equals(sourceType)) {
            return source;
        }
        if ("VARIABLE".equals(sourceType)) {
            return get(source);
        }
        if (contains(source)) {
            return get(source);
        }
        return source;
    }
}
