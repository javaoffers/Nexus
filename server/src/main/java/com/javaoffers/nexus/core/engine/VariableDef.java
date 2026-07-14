package com.javaoffers.nexus.core.engine;

import lombok.Data;

/** 对应 Go engine.VariableDef。 */
@Data
public class VariableDef {
    private String key;
    private String variableKey;
    private String name;
    private String variableName;
    private int variableType;
    private String dataType;

    public String getKey() {
        return (key != null && !key.isEmpty()) ? key : variableKey;
    }
}
