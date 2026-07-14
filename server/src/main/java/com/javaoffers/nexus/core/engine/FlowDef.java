package com.javaoffers.nexus.core.engine;

import lombok.Data;

import java.util.List;
import java.util.Map;

/** 对应 Go engine.FlowDef：一次流程执行的完整定义。 */
@Data
public class FlowDef {
    private String instanceId;
    private String flowKey;
    private String flowType;
    private String flowContent;            // JSON 数组
    private List<ParameterDef> inputs;
    private List<ParameterDef> outputs;
    private List<VariableDef> variables;
}
