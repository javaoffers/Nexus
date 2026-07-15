package com.javaoffers.nexus.common.params;

import com.javaoffers.nexus.common.model.Parameter;
import lombok.Data;

import java.util.List;

@Data
public class ApiUpdateParam {
    private Long id;
    private Long suiteId;
    private String apiUrl;
    private String apiName;
    private String apiDesc;
    private String apiRequestType;
    private String apiRequestContentType;
    private List<Parameter> apiHeaders;
    private List<Parameter> apiInputParams;
    private List<Parameter> apiOutputParams;
}