package com.javaoffers.nexus.core.engine;

import lombok.Data;

import java.util.Map;

/** 对应 Go engine.ExecutionResult。 */
@Data
public class ExecutionResult {
    private String status;
    private Map<String, Object> data;

    public ExecutionResult() {}

    public ExecutionResult(String status, Map<String, Object> data) {
        this.status = status;
        this.data = data;
    }
}
