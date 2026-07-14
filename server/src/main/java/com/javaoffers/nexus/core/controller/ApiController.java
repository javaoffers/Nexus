package com.javaoffers.nexus.core.controller;

import com.javaoffers.nexus.core.model.API;
import com.javaoffers.nexus.core.model.PageParam;
import com.javaoffers.nexus.core.model.Parameter;
import com.javaoffers.nexus.core.service.ApiService;
import com.javaoffers.nexus.pkg.exception.BizException;
import com.javaoffers.nexus.pkg.response.R;
import com.javaoffers.nexus.pkg.response.Result;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** 对应 Go core/handler/api_handler.go。 */
@RestController
@RequestMapping("/api/v1/api")
public class ApiController {

    private final ApiService apiService;

    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @Data
    public static class ApiAddParam {
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

    @Data
    public static class ApiUpdateParam {
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

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ApiQueryParam extends PageParam {
        private Long suiteId;
        private String apiName;
        private String apiUrl;
    }

    @Data
    public static class ApiDebugParam {
        private Map<String, Object> headerData;
        private Map<String, Object> inputParamData;
    }

    @PostMapping("/add")
    public Result<?> addAPI(@RequestBody ApiAddParam param) {
        if (param.getSuiteId() == null || param.getApiUrl() == null || param.getApiName() == null || param.getApiRequestType() == null) {
            return R.error(400, "参数错误");
        }
        API a = new API();
        a.setSuiteId(param.getSuiteId());
        a.setApiCode(param.getApiCode());
        a.setApiUrl(param.getApiUrl());
        a.setApiName(param.getApiName());
        a.setApiDesc(param.getApiDesc());
        a.setApiRequestType(param.getApiRequestType());
        a.setApiRequestContentType(param.getApiRequestContentType());
        try {
            apiService.createAPI(a);
            apiService.saveAPIParameters(a.getId(), "api", param.getApiHeaders(), param.getApiInputParams(), param.getApiOutputParams());
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
        API a = new API();
        a.setId(param.getId());
        a.setSuiteId(param.getSuiteId());
        a.setApiUrl(param.getApiUrl());
        a.setApiName(param.getApiName());
        a.setApiDesc(param.getApiDesc());
        a.setApiRequestType(param.getApiRequestType());
        a.setApiRequestContentType(param.getApiRequestContentType());
        try {
            apiService.updateAPI(a);
            apiService.saveAPIParameters(a.getId(), "api", param.getApiHeaders(), param.getApiInputParams(), param.getApiOutputParams());
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
            com.javaoffers.nexus.core.service.Page<API> p = apiService.pageAPIs(param, param.getSuiteId(), param.getApiName(), param.getApiUrl());
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
