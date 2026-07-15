package com.javaoffers.nexus.common.params;

import lombok.Data;

@Data
public class FlowDefDeployParam {
    private Long flowDefinitionId;
    private String flowDeployVersion;
    private String flowVersionRemark;
}