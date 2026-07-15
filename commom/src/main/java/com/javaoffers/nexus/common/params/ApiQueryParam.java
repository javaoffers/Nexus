package com.javaoffers.nexus.common.params;

import com.javaoffers.nexus.common.model.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApiQueryParam extends PageParam {
    private Long suiteId;
    private String apiName;
    private String apiUrl;
}