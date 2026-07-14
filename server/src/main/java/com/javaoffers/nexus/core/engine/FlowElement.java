package com.javaoffers.nexus.core.engine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/** 对应 Go engine.FlowElement：流程图中的节点。Jackson 一次性反序列化整个数组。 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowElement {
    private String key;
    private String name;
    private String elementType;
    private String desc;
    private List<String> incomings;
    private List<String> outgoings;

    // METHOD 节点
    private MethodDef method;

    // CONDITION 节点
    private List<ConditionItem> conditions;

    // ASSIGN 节点
    private List<FillRule> assignRules;

    // CODE 节点
    private String language;
    private String content;

    // MYSQL 节点
    private Long dataSourceId;
    private String operationType;
    private String sql;
    private String outputVariableKey;
}
