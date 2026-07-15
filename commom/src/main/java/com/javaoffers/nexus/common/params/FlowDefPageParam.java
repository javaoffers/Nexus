package com.javaoffers.nexus.common.params;

import com.javaoffers.nexus.common.model.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FlowDefPageParam extends PageParam {
    private String flowName;
    private String flowType;
}