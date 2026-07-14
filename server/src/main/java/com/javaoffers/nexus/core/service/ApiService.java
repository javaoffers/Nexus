package com.javaoffers.nexus.core.service;

import com.javaoffers.nexus.core.engine.HttpExecutor;
import com.javaoffers.nexus.core.engine.MethodDef;
import com.javaoffers.nexus.core.model.API;
import com.javaoffers.nexus.core.model.PageParam;
import com.javaoffers.nexus.core.model.Parameter;
import com.javaoffers.nexus.core.model.Suite;
import com.javaoffers.nexus.core.repository.ApiMapper;
import com.javaoffers.nexus.core.repository.ParameterMapper;
import com.javaoffers.nexus.core.repository.SuiteMapper;
import com.javaoffers.nexus.pkg.exception.BizException;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/** 对应 Go core/service/api_service.go。 */
@Service
public class ApiService {

    private final ApiMapper apiMapper;
    private final SuiteMapper suiteMapper;
    private final ParameterMapper parameterMapper;
    private final HttpExecutor httpExecutor;

    public ApiService(ApiMapper apiMapper, SuiteMapper suiteMapper, ParameterMapper parameterMapper, HttpExecutor httpExecutor) {
        this.apiMapper = apiMapper;
        this.suiteMapper = suiteMapper;
        this.parameterMapper = parameterMapper;
        this.httpExecutor = httpExecutor;
    }

    @Data
    public static class APIInfoDTO {
        private Long id;
        private Long suiteId;
        private Integer suiteFlag;
        private String apiUrl;
        private String apiName;
        private String apiDesc;
        private String apiRequestType;
        private String apiRequestContentType;
        private List<Parameter> apiHeaders;
        private List<Parameter> apiInputParams;
        private List<Parameter> apiOutputParams;
    }

    public void createAPI(API a) {
        apiMapper.create(a);
        // brief 不回填自增 id，按 apiCode 取回真实 id（无 apiCode 时取最新行）
        API saved = (a.getApiCode() != null && !a.getApiCode().isEmpty())
                ? apiMapper.getByCode(a.getApiCode())
                : apiMapper.findNewest();
        if (saved != null) {
            a.setId(saved.getId());
        }
    }

    public void updateAPI(API a) {
        apiMapper.update(a);
    }

    public void deleteAPI(Long id) {
        apiMapper.delete(id);
    }

    public APIInfoDTO getAPIInfo(Long id) {
        API a = apiMapper.getById(id);
        if (a == null) {
            throw new BizException("接口不存在");
        }
        return buildDTO(a);
    }

    public APIInfoDTO getAPIInfoByCode(String code) {
        API a = apiMapper.getByCode(code);
        if (a == null) {
            throw new BizException("接口不存在");
        }
        return buildDTO(a);
    }

    private APIInfoDTO buildDTO(API a) {
        APIInfoDTO dto = new APIInfoDTO();
        dto.setId(a.getId());
        dto.setSuiteId(a.getSuiteId());
        dto.setApiUrl(a.getApiUrl());
        dto.setApiName(a.getApiName());
        dto.setApiDesc(a.getApiDesc());
        dto.setApiRequestType(a.getApiRequestType());
        dto.setApiRequestContentType(a.getApiRequestContentType());
        Suite suite = suiteMapper.getById(a.getSuiteId());
        if (suite != null) {
            dto.setSuiteFlag(suite.getSuiteFlag());
        }
        dto.setApiHeaders(parameterMapper.listBySourceAndType("api", a.getId(), 4));
        dto.setApiInputParams(parameterMapper.listBySourceAndType("api", a.getId(), 1));
        dto.setApiOutputParams(parameterMapper.listBySourceAndType("api", a.getId(), 2));
        return dto;
    }

    public List<API> listAPIsBySuiteID(Long suiteID) {
        return apiMapper.listBySuiteId(suiteID);
    }

    public List<API> listAPIsBySuiteCode(String suiteCode) {
        Suite suite = suiteMapper.getByCode(suiteCode);
        if (suite == null) {
            throw new BizException("套件不存在");
        }
        return apiMapper.listBySuiteId(suite.getId());
    }

    public Page<API> pageAPIs(PageParam p, Long suiteID, String apiName, String apiURL) {
        int pn = p.getPageNum() <= 0 ? 1 : p.getPageNum();
        int ps = p.getPageSize() <= 0 ? 10 : p.getPageSize();
        List<API> list = apiMapper.page(pn, ps, suiteID, apiName, apiURL);
        long total = apiMapper.count(suiteID, apiName, apiURL);
        return new Page<>(total, list);
    }

    public Map<String, Object> debugAPI(Long apiID, Map<String, Object> headerData, Map<String, Object> inputParamData) {
        API a = apiMapper.getById(apiID);
        if (a == null) {
            throw new BizException("接口不存在");
        }
        MethodDef method = new MethodDef();
        method.setUrl(a.getApiUrl());
        method.setRequestType(a.getApiRequestType());
        method.setRequestContentType(a.getApiRequestContentType());
        Map<String, Object> result = httpExecutor.sendRequest(method, headerData, inputParamData);
        if (result == null) {
            throw new BizException("请求失败");
        }
        return result;
    }

    public void saveAPIParameters(Long apiID, String sourceType,
                                  List<Parameter> headers, List<Parameter> inputs, List<Parameter> outputs) {
        parameterMapper.deleteBySource(sourceType, apiID);
        if (headers != null) {
            for (Parameter p : headers) {
                p.setSourceType(sourceType);
                p.setSourceId(apiID);
                p.setParamType(4);
            }
        }
        if (inputs != null) {
            for (Parameter p : inputs) {
                p.setSourceType(sourceType);
                p.setSourceId(apiID);
                p.setParamType(1);
            }
        }
        if (outputs != null) {
            for (Parameter p : outputs) {
                p.setSourceType(sourceType);
                p.setSourceId(apiID);
                p.setParamType(2);
            }
        }
        java.util.List<Parameter> all = new java.util.ArrayList<>();
        if (headers != null) all.addAll(headers);
        if (inputs != null) all.addAll(inputs);
        if (outputs != null) all.addAll(outputs);
        parameterMapper.batchCreate(all);
    }
}
