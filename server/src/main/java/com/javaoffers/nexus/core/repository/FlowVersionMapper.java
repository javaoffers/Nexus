package com.javaoffers.nexus.core.repository;

import com.javaoffers.brief.modelhelper.fun.AggTag;
import com.javaoffers.brief.modelhelper.mapper.BriefMapper;
import com.javaoffers.nexus.core.model.FlowVersion;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FlowVersionMapper extends BriefMapper<FlowVersion> {

    default void create(FlowVersion fv) {
        insert().colAll(fv).ex();
    }

    default void delete(Long id) {
        general().logicRemoveById(id);
    }

    default void updateStatus(Long id, int status) {
        update().npdateNull().col(FlowVersion::getFlowVersionStatus, status).where().eq(FlowVersion::getId, id).ex();
    }

    default FlowVersion getById(Long id) {
        return select().colAll().where().eq(FlowVersion::getId, id).ex();
    }

    default FlowVersion getByFlowIdAndVersion(Long flowId, String version) {
        return select().colAll().where()
                .eq(FlowVersion::getFlowId, flowId)
                .eq(FlowVersion::getFlowVersion, version)
                .ex();
    }

    default FlowVersion getEnabledByFlowIdAndVersion(Long flowId, String version) {
        return select().colAll().where()
                .eq(FlowVersion::getFlowId, flowId)
                .eq(FlowVersion::getFlowVersion, version)
                .eq(FlowVersion::getFlowVersionStatus, 1)
                .ex();
    }

    default long countByFlowId(Long flowId) {
        return select().col(AggTag.COUNT, FlowVersion::getCountId).where().eq(FlowVersion::getFlowId, flowId).ex().getCountId();
    }

    default List<FlowVersion> page(int pageNum, int pageSize, Long flowId, Integer status) {
        return select().colAll().where()
                .eq(flowId != null, FlowVersion::getFlowId, flowId)
                .eq(status != null, FlowVersion::getFlowVersionStatus, status)
                .orderD(FlowVersion::getId)
                .limitPage(pageNum, pageSize)
                .exs();
    }

    default long count(Long flowId, Integer status) {
        return select().col(AggTag.COUNT, FlowVersion::getCountId).where()
                .eq(flowId != null, FlowVersion::getFlowId, flowId)
                .eq(status != null, FlowVersion::getFlowVersionStatus, status)
                .ex()
                .getCountId();
    }
}
