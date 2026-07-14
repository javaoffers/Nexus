package com.javaoffers.nexus.core.repository;

import com.javaoffers.brief.modelhelper.core.Id;
import com.javaoffers.brief.modelhelper.fun.AggTag;
import com.javaoffers.brief.modelhelper.mapper.BriefMapper;
import com.javaoffers.nexus.core.model.FlowInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FlowInfoMapper extends BriefMapper<FlowInfo> {

    default void create(FlowInfo f) {
        insert().colAll(f).ex();
    }

    default void delete(Long id) {
        general().logicRemoveById(id);
    }

    default FlowInfo getByKey(String key) {
        return select().colAll().where().eq(FlowInfo::getFlowKey, key).ex();
    }

    default List<FlowInfo> page(int pageNum, int pageSize, String flowName, String flowType) {
        return select().colAll().where()
                .like(flowName != null && !flowName.isEmpty(), FlowInfo::getFlowName, flowName)
                .eq(flowType != null && !flowType.isEmpty(), FlowInfo::getFlowType, flowType)
                .orderD(FlowInfo::getId)
                .limitPage(pageNum, pageSize)
                .exs();
    }

    default long count(String flowName, String flowType) {
        return select().col(AggTag.COUNT, FlowInfo::getCountId).where()
                .like(flowName != null && !flowName.isEmpty(), FlowInfo::getFlowName, flowName)
                .eq(flowType != null && !flowType.isEmpty(), FlowInfo::getFlowType, flowType)
                .ex()
                .getCountId();
    }
}
