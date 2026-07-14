package com.javaoffers.nexus.core.controller;

import com.javaoffers.nexus.core.service.ObjectService;
import com.javaoffers.nexus.pkg.exception.BizException;
import com.javaoffers.nexus.pkg.response.R;
import com.javaoffers.nexus.pkg.response.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 对应 Go core/handler/object_handler.go 的 GetDataTypeList。 */
@RestController
@RequestMapping("/api/v1/dataType")
public class DataTypeController {

    private final ObjectService objectService;

    public DataTypeController(ObjectService objectService) {
        this.objectService = objectService;
    }

    @GetMapping("/list")
    public Result<?> getDataTypeList() {
        try {
            return R.ok(objectService.getDataTypeOptions());
        } catch (BizException e) {
            return R.error(4007, e.getMessage());
        }
    }
}
