package com.javaoffers.nexus.core.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaoffers.nexus.core.cache.Cache;
import com.javaoffers.nexus.core.engine.ExecutionResult;
import com.javaoffers.nexus.core.engine.FlowDef;
import com.javaoffers.nexus.core.engine.FlowEngine;
import com.javaoffers.nexus.core.engine.ParameterDef;
import com.javaoffers.nexus.core.engine.VariableDef;
import com.javaoffers.nexus.core.model.FlowDefinition;
import com.javaoffers.nexus.core.model.FlowInfo;
import com.javaoffers.nexus.core.model.FlowVersion;
import com.javaoffers.nexus.core.model.Parameter;
import com.javaoffers.nexus.core.model.VariableInfo;
import com.javaoffers.nexus.core.repository.FlowDefinitionMapper;
import com.javaoffers.nexus.core.repository.FlowInfoMapper;
import com.javaoffers.nexus.core.repository.FlowVersionMapper;
import com.javaoffers.nexus.core.repository.ParameterMapper;
import com.javaoffers.nexus.core.repository.VariableInfoMapper;
import com.javaoffers.nexus.pkg.exception.BizException;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** 对应 Go core/service/flow_runtime_service.go。 */
@Service
public class FlowRuntimeService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final FlowEngine flowEngine;
    private final FlowInfoMapper flowInfoMapper;
    private final FlowVersionMapper flowVersionMapper;
    private final FlowDefinitionMapper flowDefinitionMapper;
    private final ParameterMapper parameterMapper;
    private final VariableInfoMapper variableInfoMapper;
    private final Cache cache;

    public FlowRuntimeService(FlowEngine flowEngine,
                              FlowInfoMapper flowInfoMapper,
                              FlowVersionMapper flowVersionMapper,
                              FlowDefinitionMapper flowDefinitionMapper,
                              ParameterMapper parameterMapper,
                              VariableInfoMapper variableInfoMapper,
                              Cache cache) {
        this.flowEngine = flowEngine;
        this.flowInfoMapper = flowInfoMapper;
        this.flowVersionMapper = flowVersionMapper;
        this.flowDefinitionMapper = flowDefinitionMapper;
        this.parameterMapper = parameterMapper;
        this.variableInfoMapper = variableInfoMapper;
        this.cache = cache;
    }

    @Data
    public static class FlowResult {
        private String flowInstanceId;
        private String status;
        private Map<String, Object> data;
    }

    public FlowResult triggerFlow(String flowKey, String flowVersion, Map<String, Object> flowData) {
        FlowInfo flowInfo = flowInfoMapper.getByKey(flowKey);
        if (flowInfo == null) {
            throw new BizException("流程不存在");
        }
        FlowVersion fv = flowVersionMapper.getEnabledByFlowIdAndVersion(flowInfo.getId(), flowVersion);
        if (fv == null) {
            throw new BizException("流程版本不存在或未启用");
        }

        String instanceID = flowInfo.getFlowType() + "_" + randomString(16);

        List<ParameterDef> inputs = parseParams(fv.getInputs());
        List<ParameterDef> outputs = parseParams(fv.getOutputs());
        List<VariableDef> variables = parseVars(fv.getVariables());

        FlowDef flowDef = new FlowDef();
        flowDef.setInstanceId(instanceID);
        flowDef.setFlowKey(flowKey);
        flowDef.setFlowType(flowInfo.getFlowType());
        flowDef.setFlowContent(fv.getFlowContent());
        flowDef.setInputs(inputs);
        flowDef.setOutputs(outputs);
        flowDef.setVariables(variables);

        if ("async".equals(flowInfo.getFlowType())) {
            new Thread(() -> {
                ExecutionResult result = flowEngine.execute(flowDef, flowData);
                cache.set(instanceID, result);
            }).start();
            FlowResult r = new FlowResult();
            r.setFlowInstanceId(instanceID);
            r.setStatus("RUNNING");
            return r;
        }

        ExecutionResult result = flowEngine.execute(flowDef, flowData);
        FlowResult r = new FlowResult();
        r.setFlowInstanceId(instanceID);
        r.setStatus(result.getStatus());
        r.setData(result.getData());
        return r;
    }

    public FlowResult triggerFlowByDefinition(String flowKey, Map<String, Object> flowData) {
        FlowDefinition fd = flowDefinitionMapper.getByKey(flowKey);
        if (fd == null) {
            throw new BizException("流程定义不存在");
        }
        String instanceID = "debug_" + randomString(16);

        List<Parameter> inputParams = parameterMapper.listBySourceAndType("flow", fd.getId(), 1);
        List<Parameter> outputParams = parameterMapper.listBySourceAndType("flow", fd.getId(), 2);
        List<VariableInfo> variableInfos = variableInfoMapper.listByFlowDefId(fd.getId());

        List<ParameterDef> inputs = new ArrayList<>();
        if (inputParams != null) {
            for (Parameter p : inputParams) {
                ParameterDef pd = new ParameterDef();
                pd.setKey(p.getParamKey());
                pd.setName(p.getParamName());
                pd.setDataType(p.getDataType());
                inputs.add(pd);
            }
        }
        List<ParameterDef> outputs = new ArrayList<>();
        if (outputParams != null) {
            for (Parameter p : outputParams) {
                ParameterDef pd = new ParameterDef();
                pd.setKey(p.getParamKey());
                pd.setName(p.getParamName());
                pd.setDataType(p.getDataType());
                outputs.add(pd);
            }
        }
        List<VariableDef> variables = new ArrayList<>();
        if (variableInfos != null) {
            for (VariableInfo v : variableInfos) {
                VariableDef vd = new VariableDef();
                vd.setKey(v.getVariableKey());
                vd.setName(v.getVariableName());
                vd.setVariableType(v.getVariableType());
                vd.setDataType(v.getDataType());
                variables.add(vd);
            }
        }

        FlowDef flowDef = new FlowDef();
        flowDef.setInstanceId(instanceID);
        flowDef.setFlowKey(flowKey);
        flowDef.setFlowType(fd.getFlowType());
        flowDef.setFlowContent(fd.getFlowContent());
        flowDef.setInputs(inputs);
        flowDef.setOutputs(outputs);
        flowDef.setVariables(variables);

        ExecutionResult result = flowEngine.execute(flowDef, flowData);
        FlowResult r = new FlowResult();
        r.setFlowInstanceId(instanceID);
        r.setStatus(result.getStatus());
        r.setData(result.getData());
        return r;
    }

    public Map<String, Object> getAsyncFlowResult(String instanceID) {
        Object result = cache.get(instanceID);
        if (result == null) {
            throw new BizException("流程执行结果不存在或尚未完成");
        }
        if (result instanceof ExecutionResult) {
            return ((ExecutionResult) result).getData();
        }
        throw new BizException("结果格式异常");
    }

    private List<ParameterDef> parseParams(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return MAPPER.readValue(json, new TypeReference<List<ParameterDef>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<VariableDef> parseVars(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return MAPPER.readValue(json, new TypeReference<List<VariableDef>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String randomString(int n) {
        String letters = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(letters.charAt((int) (Math.random() * letters.length())));
        }
        return sb.toString();
    }
}
