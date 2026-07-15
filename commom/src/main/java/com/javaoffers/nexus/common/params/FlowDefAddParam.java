package com.javaoffers.nexus.common.params;

import com.javaoffers.nexus.common.model.Parameter;
import lombok.Data;

import java.util.List;

@Data
public class FlowDefAddParam {
    private String flowName;
    private String flowType;
    private String remark;
    private List<Parameter> flowInputParams;
    private List<Parameter> flowOutputParams;
}