package com.javaoffers.nexus.core.engine;

import lombok.Data;

import java.util.List;

/** 对应 Go engine.ConditionExpr：单个比较条件。 */
@Data
public class ConditionExpr {
    private String variableKey;
    private Object dataType;
    private String operator;
    private String assignType;   // CONSTANT or VARIABLE
    private Object value;
}
