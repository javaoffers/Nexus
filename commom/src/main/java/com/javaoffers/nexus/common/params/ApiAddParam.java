package com.javaoffers.nexus.common.params;

import com.javaoffers.nexus.common.model.Parameter;
import lombok.Data;

import java.util.List;

@Data
public class ApiAddParam {
    private Long suiteId;
    private String apiCode;
    private String apiUrl;
    private String apiName;
    private String apiDesc;
    private String apiRequestType;
    private String apiRequestContentType;
    private List<Parameter> apiHeaders;
    private List<Parameter> apiInputParams;
    private List<Parameter> apiOutputParams;
}