package com.javaoffers.nexus.common.params;

import lombok.Data;

import java.util.Map;

@Data
public class ApiDebugParam {
    private Map<String, Object> headerData;
    private Map<String, Object> inputParamData;
}