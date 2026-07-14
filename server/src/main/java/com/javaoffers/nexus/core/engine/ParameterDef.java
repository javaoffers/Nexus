package com.javaoffers.nexus.core.engine;

import lombok.Data;

/** 对应 Go engine.ParameterDef。 */
@Data
public class ParameterDef {
    private String key;
    private String paramKey;
    private String name;
    private String paramName;
    private String dataType;

    public String getKey() {
        return (key != null && !key.isEmpty()) ? key : paramKey;
    }
}
