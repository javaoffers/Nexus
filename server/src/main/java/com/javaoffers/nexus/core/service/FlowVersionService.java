package com.javaoffers.nexus.core.service;

import com.javaoffers.nexus.core.model.FlowInfo;
import com.javaoffers.nexus.core.model.FlowVersion;
import com.javaoffers.nexus.core.model.PageParam;
import com.javaoffers.nexus.core.repository.FlowInfoMapper;
import com.javaoffers.nexus.core.repository.FlowVersionMapper;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** 对应 Go core/service/flow_version_service.go。 */
@Service
public class FlowVersionService {

    private final FlowVersionMapper flowVersionMapper;
    private final FlowInfoMapper flowInfoMapper;

    public FlowVersionService(FlowVersionMapper flowVersionMapper, FlowInfoMapper flowInfoMapper) {
        this.flowVersionMapper = flowVersionMapper;
        this.flowInfoMapper = flowInfoMapper;
    }

    @Data
    public static class FlowVersionDTO {
        private Long id;
        private String flowVersion;
        private int flowVersionStatus;
        private String flowVersionRemark;
    }

    public void updateFlowVersionStatus(Long id, int status) {
        flowVersionMapper.updateStatus(id, status);
    }

    public void deleteFlowVersion(Long id) {
        flowVersionMapper.delete(id);
    }

    public String getLatestVersion(String flowKey) {
        FlowInfo info = flowInfoMapper.getByKey(flowKey);
        if (info == null) {
            return "v1";
        }
        long count = flowVersionMapper.countByFlowId(info.getId());
        return "v" + (count + 1);
    }

    public Page<FlowVersionDTO> pageFlowVersions(PageParam p, Long flowID, Integer status) {
        int pn = p.getPageNum() <= 0 ? 1 : p.getPageNum();
        int ps = p.getPageSize() <= 0 ? 10 : p.getPageSize();
        List<FlowVersion> list = flowVersionMapper.page(pn, ps, flowID, status);
        long total = flowVersionMapper.count(flowID, status);
        List<FlowVersionDTO> dtos = new ArrayList<>();
        for (FlowVersion fv : list) {
            FlowVersionDTO dto = new FlowVersionDTO();
            dto.setId(fv.getId());
            dto.setFlowVersion(fv.getFlowVersion());
            dto.setFlowVersionStatus(fv.getFlowVersionStatus());
            dto.setFlowVersionRemark(fv.getFlowVersionRemark());
            dtos.add(dto);
        }
        return new Page<>(total, dtos);
    }
}
