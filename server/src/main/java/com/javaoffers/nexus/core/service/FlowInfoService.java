package com.javaoffers.nexus.core.service;

import com.javaoffers.nexus.core.model.FlowInfo;
import com.javaoffers.nexus.core.model.PageParam;
import com.javaoffers.nexus.core.repository.FlowInfoMapper;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** 对应 Go core/service/flow_info_service.go。 */
@Service
public class FlowInfoService {

    private final FlowInfoMapper flowInfoMapper;

    public FlowInfoService(FlowInfoMapper flowInfoMapper) {
        this.flowInfoMapper = flowInfoMapper;
    }

    @Data
    public static class FlowInfoDTO {
        private Long id;
        private String flowKey;
        private String flowName;
        private String flowType;
        private String remark;
    }

    public void deleteFlowInfo(Long id) {
        flowInfoMapper.delete(id);
    }

    public Page<FlowInfoDTO> pageFlowInfos(PageParam p, String flowName, String flowType) {
        int pn = p.getPageNum() <= 0 ? 1 : p.getPageNum();
        int ps = p.getPageSize() <= 0 ? 10 : p.getPageSize();
        List<FlowInfo> list = flowInfoMapper.page(pn, ps, flowName, flowType);
        long total = flowInfoMapper.count(flowName, flowType);
        List<FlowInfoDTO> dtos = new ArrayList<>();
        for (FlowInfo f : list) {
            FlowInfoDTO dto = new FlowInfoDTO();
            dto.setId(f.getId());
            dto.setFlowKey(f.getFlowKey());
            dto.setFlowName(f.getFlowName());
            dto.setFlowType(f.getFlowType());
            dto.setRemark(f.getRemark());
            dtos.add(dto);
        }
        return new Page<>(total, dtos);
    }
}
