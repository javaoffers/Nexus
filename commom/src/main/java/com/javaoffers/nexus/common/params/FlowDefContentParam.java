package com.javaoffers.nexus.common.params;

import com.javaoffers.nexus.common.model.VariableInfo;
import lombok.Data;

import java.util.List;

@Data
public class FlowDefContentParam {
    private Long id;
    private String flowContent;
    private List<VariableInfo> flowVariables;
}