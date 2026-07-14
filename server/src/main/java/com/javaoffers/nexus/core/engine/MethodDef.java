package com.javaoffers.nexus.core.engine;

import lombok.Data;

/** 对应 Go engine.MethodDef：METHOD 节点的 HTTP 调用定义。 */
@Data
public class MethodDef {
    private String suiteCode;
    private String methodCode;
    private String url;
    private String requestType;
    private String requestContentType;
    private java.util.List<FillRule> headerFillRules;
    private java.util.List<FillRule> inputFillRules;
    private java.util.List<FillRule> outputFillRules;
}
