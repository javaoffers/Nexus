package com.javaoffers.nexus.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaoffers.nexus.core.model.FlowDefinition;
import com.javaoffers.nexus.core.model.FlowInfo;
import com.javaoffers.nexus.core.model.FlowVersion;
import com.javaoffers.nexus.core.model.PageParam;
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
import java.util.Date;
import java.util.List;

/** 对应 Go core/service/flow_definition_service.go。 */
@Service
public class FlowDefinitionService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static final String DEFAULT_FLOW_CONTENT =
            "[{\"key\":\"start_1\",\"name\":\"开始\",\"outgoings\":[\"end_1\"],\"incomings\":[],\"elementType\":\"START\"},"
            + "{\"key\":\"end_1\",\"name\":\"结束\",\"outgoings\":[],\"incomings\":[\"start_1\"],\"elementType\":\"END\"}]";

    private final FlowDefinitionMapper flowDefinitionMapper;
    private final FlowInfoMapper flowInfoMapper;
    private final FlowVersionMapper flowVersionMapper;
    private final ParameterMapper parameterMapper;
    private final VariableInfoMapper variableInfoMapper;

    public FlowDefinitionService(FlowDefinitionMapper flowDefinitionMapper,
                                 FlowInfoMapper flowInfoMapper,
                                 FlowVersionMapper flowVersionMapper,
                                 ParameterMapper parameterMapper,
                                 VariableInfoMapper variableInfoMapper) {
        this.flowDefinitionMapper = flowDefinitionMapper;
        this.flowInfoMapper = flowInfoMapper;
        this.flowVersionMapper = flowVersionMapper;
        this.parameterMapper = parameterMapper;
        this.variableInfoMapper = variableInfoMapper;
    }

    @Data
    public static class FlowDefinitionInfoDTO {
        private Long id;
        private String flowKey;
        private String flowName;
        private String flowType;
        private String flowContent;
        private String remark;
        private Date createdAt;
        private List<Parameter> flowInputParams;
        private List<Parameter> flowOutputParams;
        private List<VariableInfo> flowVariables;
    }

    public void createFlowDefinition(String flowName, String flowType, String remark,
                                    List<Parameter> inputParams, List<Parameter> outputParams) {
        FlowDefinition fd = new FlowDefinition();
        fd.setFlowKey(generateFlowKey());
        fd.setFlowName(flowName);
        fd.setFlowType(flowType);
        fd.setFlowContent(DEFAULT_FLOW_CONTENT);
        fd.setRemark(remark);
        flowDefinitionMapper.create(fd);
        fd = flowDefinitionMapper.getByKey(fd.getFlowKey());
        saveFlowDefinitionParams(fd.getId(), inputParams, outputParams);
    }

    public void updateFlowDefinition(Long id, String flowName, String flowType, String remark,
                                     List<Parameter> inputParams, List<Parameter> outputParams) {
        FlowDefinition fd = new FlowDefinition();
        fd.setId(id);
        fd.setFlowName(flowName);
        fd.setFlowType(flowType);
        fd.setRemark(remark);
        flowDefinitionMapper.update(fd);
        saveFlowDefinitionParams(id, inputParams, outputParams);
    }

    public void saveFlowDefinitionContent(Long id, String content, List<VariableInfo> variables) {
        flowDefinitionMapper.updateContent(id, content);
        variableInfoMapper.deleteByFlowDefId(id);
        if (variables != null) {
            for (VariableInfo v : variables) {
                v.setId(null);
                v.setFlowDefinitionId(id);
            }
            variableInfoMapper.batchCreate(variables);
        }
    }

    public void deleteFlowDefinition(Long id) {
        parameterMapper.deleteBySource("flow", id);
        variableInfoMapper.deleteByFlowDefId(id);
        flowDefinitionMapper.delete(id);
    }

    public FlowDefinitionInfoDTO getFlowDefinitionInfo(Long id) {
        FlowDefinition fd = flowDefinitionMapper.getById(id);
        if (fd == null) {
            throw new BizException("流程定义不存在");
        }
        return buildDTO(fd);
    }

    public Page<FlowDefinitionInfoDTO> pageFlowDefinitions(PageParam p, String flowName, String flowType) {
        int pn = p.getPageNum() <= 0 ? 1 : p.getPageNum();
        int ps = p.getPageSize() <= 0 ? 10 : p.getPageSize();
        List<FlowDefinition> list = flowDefinitionMapper.page(pn, ps, flowName, flowType);
        long total = flowDefinitionMapper.count(flowName, flowType);
        List<FlowDefinitionInfoDTO> dtos = new ArrayList<>();
        for (FlowDefinition fd : list) {
            dtos.add(buildDTO(fd));
        }
        return new Page<>(total, dtos);
    }

    public void deployFlowDefinition(Long flowDefID, String version, String remark) {
        FlowDefinition fd = flowDefinitionMapper.getById(flowDefID);
        if (fd == null) {
            throw new BizException("流程定义不存在");
        }

        FlowInfo flowInfo = flowInfoMapper.getByKey(fd.getFlowKey());
        if (flowInfo == null) {
            flowInfo = new FlowInfo();
            flowInfo.setFlowKey(fd.getFlowKey());
            flowInfo.setFlowName(fd.getFlowName());
            flowInfo.setFlowType(fd.getFlowType());
            flowInfo.setRemark(fd.getRemark());
            flowInfoMapper.create(flowInfo);
            flowInfo = flowInfoMapper.getByKey(fd.getFlowKey());
        }

        List<Parameter> inputs = parameterMapper.listBySourceAndType("flow", fd.getId(), 1);
        List<Parameter> outputs = parameterMapper.listBySourceAndType("flow", fd.getId(), 2);
        List<VariableInfo> variables = variableInfoMapper.listByFlowDefId(fd.getId());

        FlowVersion fv = new FlowVersion();
        fv.setFlowId(flowInfo.getId());
        fv.setFlowVersion(version);
        fv.setFlowVersionStatus(0);
        fv.setFlowVersionRemark(remark);
        fv.setFlowContent(fd.getFlowContent());
        fv.setInputs(toJson(inputs));
        fv.setOutputs(toJson(outputs));
        fv.setVariables(toJson(variables));
        flowVersionMapper.create(fv);
    }

    private FlowDefinitionInfoDTO buildDTO(FlowDefinition fd) {
        FlowDefinitionInfoDTO dto = new FlowDefinitionInfoDTO();
        dto.setId(fd.getId());
        dto.setFlowKey(fd.getFlowKey());
        dto.setFlowName(fd.getFlowName());
        dto.setFlowType(fd.getFlowType());
        String content = fd.getFlowContent();
        dto.setFlowContent((content == null || content.isEmpty()) ? DEFAULT_FLOW_CONTENT : content);
        dto.setRemark(fd.getRemark());
        dto.setCreatedAt(fd.getCreatedAt());
        dto.setFlowInputParams(parameterMapper.listBySourceAndType("flow", fd.getId(), 1));
        dto.setFlowOutputParams(parameterMapper.listBySourceAndType("flow", fd.getId(), 2));
        dto.setFlowVariables(variableInfoMapper.listByFlowDefId(fd.getId()));
        return dto;
    }

    private void saveFlowDefinitionParams(Long fdID, List<Parameter> inputParams, List<Parameter> outputParams) {
        parameterMapper.deleteBySource("flow", fdID);
        List<Parameter> all = new ArrayList<>();
        if (inputParams != null) {
            for (Parameter p : inputParams) {
                p.setSourceType("flow");
                p.setSourceId(fdID);
                p.setParamType(1);
            }
            all.addAll(inputParams);
        }
        if (outputParams != null) {
            for (Parameter p : outputParams) {
                p.setSourceType("flow");
                p.setSourceId(fdID);
                p.setParamType(2);
            }
            all.addAll(outputParams);
        }
        parameterMapper.batchCreate(all);
    }

    private String generateFlowKey() {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder("flow_");
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }

    private String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            return "[]";
        }
    }
}
