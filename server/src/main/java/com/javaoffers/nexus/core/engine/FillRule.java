package com.javaoffers.nexus.core.engine;

import lombok.Data;

/** 对应 Go engine.FillRule：变量映射规则（source → target）。 */
@Data
public class FillRule {
    private String source;
    private String sourceType;
    private String sourceDataType;
    private String target;
    private String targetType;
    private String targetDataType;
}
