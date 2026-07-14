package com.javaoffers.nexus.core.engine;

import lombok.Data;

import java.util.Map;

/** 对应 Go engine.ExecutionContext。 */
@Data
public class ExecutionContext {
    private String instanceId;
    private Map<String, FlowElement> elementMap;
    private VariableManager variables;
    private String status;   // RUNNING, FINISH, ABORT
    private Map<String, Object> errorData;
}
