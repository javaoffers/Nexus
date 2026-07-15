package com.javaoffers.nexus.core.controller;

import com.javaoffers.nexus.core.model.API;
import com.javaoffers.nexus.core.params.ApiAddParam;
import com.javaoffers.nexus.core.params.ApiDebugParam;
import com.javaoffers.nexus.core.params.ApiQueryParam;
import com.javaoffers.nexus.core.params.ApiUpdateParam;
import com.javaoffers.nexus.core.service.ApiService;
import com.javaoffers.nexus.pkg.exception.BizException;
import com.javaoffers.nexus.pkg.response.R;
import com.javaoffers.nexus.pkg.response.Result;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对应 api 处理
 */
@RestController
@RequestMapping("/api/v1/api")
public class ApiController {

    private final ApiService apiService;

    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @PostMapping("/add")
    public Result<?> addAPI(@RequestBody ApiAddParam param) {
        if (param.getSuiteId() == null || param.getApiUrl() == null
                || param.getApiName() == null || param.getApiRequestType() == null) {
            return R.error(400, "参数错误");
        }
        API api = new API();
        api.setSuiteId(param.getSuiteId());
        api.setApiCode(param.getApiCode());
        api.setApiUrl(param.getApiUrl());
        api.setApiName(param.getApiName());
        api.setApiDesc(param.getApiDesc());
        api.setApiRequestType(param.getApiRequestType());
        api.setApiRequestContentType(param.getApiRequestContentType());
        try {
            apiService.createAPI(api);
            apiService.saveAPIParameters(api.getId(), "api", param.getApiHeaders(), param.getApiInputParams(), param.getApiOutputParams());
            return R.ok(true);
        } catch (BizException e) {
            return R.error(3001, e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<?> updateAPI(@RequestBody ApiUpdateParam param) {
        if (param.getId() == null) {
            return R.error(400, "参数错误");
        }
        API api = new API();
        api.setId(param.getId());
        api.setSuiteId(param.getSuiteId());
        api.setApiUrl(param.getApiUrl());
        api.setApiName(param.getApiName());
        api.setApiDesc(param.getApiDesc());
        api.setApiRequestType(param.getApiRequestType());
        api.setApiRequestContentType(param.getApiRequestContentType());
        try {
            apiService.updateAPI(api);
            apiService.saveAPIParameters(api.getId(), "api", param.getApiHeaders(), param.getApiInputParams(), param.getApiOutputParams());
            return R.ok(true);
        } catch (BizException e) {
            return R.error(3002, e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteAPI(@PathVariable Long id) {
        try {
            apiService.deleteAPI(id);
            return R.ok(true);
        } catch (BizException e) {
            return R.error(3003, e.getMessage());
        }
    }

    @GetMapping("/info/{id}")
    public Result<?> getAPIInfo(@PathVariable Long id) {
        try {
            return R.ok(apiService.getAPIInfo(id));
        } catch (BizException e) {
            return R.error(3004, e.getMessage());
        }
    }

    @GetMapping("/info/code/{code}")
    public Result<?> getAPIInfoByCode(@PathVariable String code) {
        try {
            return R.ok(apiService.getAPIInfoByCode(code));
        } catch (BizException e) {
            return R.error(3005, e.getMessage());
        }
    }

    @PostMapping("/getApiListBySuiteId/{id}")
    public Result<?> getAPIListBySuiteID(@PathVariable Long id) {
        try {
            return R.ok(apiService.listAPIsBySuiteID(id));
        } catch (BizException e) {
            return R.error(3006, e.getMessage());
        }
    }

    @PostMapping("/getApiListBySuiteCode/{code}")
    public Result<?> getAPIListBySuiteCode(@PathVariable String code) {
        try {
            return R.ok(apiService.listAPIsBySuiteCode(code));
        } catch (BizException e) {
            return R.error(3007, e.getMessage());
        }
    }

    @PostMapping("/page")
    public Object pageAPIs(@RequestBody ApiQueryParam param) {
        try {
            com.javaoffers.nexus.core.service.Page<API> p = apiService.pageAPIs(param, param.getSuiteId(),
                    param.getApiName(), param.getApiUrl());
            return R.okPage(p.getTotal(), p.getRecords());
        } catch (BizException e) {
            return R.error(3008, e.getMessage());
        }
    }

    @PostMapping("/debug/{id}")
    public Result<?> debugAPI(@PathVariable Long id, @RequestBody ApiDebugParam param) {
        try {
            return R.ok(apiService.debugAPI(id, param.getHeaderData(), param.getInputParamData()));
        } catch (BizException e) {
            return R.error(3009, e.getMessage());
        }
    }
}
