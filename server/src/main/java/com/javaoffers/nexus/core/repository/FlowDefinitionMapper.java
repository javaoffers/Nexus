package com.javaoffers.nexus.core.repository;

import com.javaoffers.brief.modelhelper.fun.AggTag;
import com.javaoffers.brief.modelhelper.mapper.BriefMapper;
import com.javaoffers.nexus.core.model.FlowDefinition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FlowDefinitionMapper extends BriefMapper<FlowDefinition> {

    default void create(FlowDefinition f) {
        insert().colAll(f).ex();
    }

    default void update(FlowDefinition f) {
        update().npdateNull()
                .col(FlowDefinition::getFlowName, f.getFlowName())
                .col(FlowDefinition::getFlowType, f.getFlowType())
                .col(FlowDefinition::getRemark, f.getRemark())
                .where().eq(FlowDefinition::getId, f.getId()).ex();
    }

    default void updateContent(Long id, String content) {
        update().updateNull().col(FlowDefinition::getFlowContent, content).where().eq(FlowDefinition::getId, id).ex();
    }

    default void delete(Long id) {
        general().logicRemoveById(id);
    }

    default FlowDefinition getById(Long id) {
        return select().colAll().where().eq(FlowDefinition::getId, id).ex();
    }

    default FlowDefinition getByKey(String key) {
        return select().colAll().where().eq(FlowDefinition::getFlowKey, key).ex();
    }

    default List<FlowDefinition> page(int pageNum, int pageSize, String flowName, String flowType) {
        return select().colAll().where()
                .like(flowName != null && !flowName.isEmpty(), FlowDefinition::getFlowName, flowName)
                .eq(flowType != null && !flowType.isEmpty(), FlowDefinition::getFlowType, flowType)
                .orderD(FlowDefinition::getId)
                .limitPage(pageNum, pageSize)
                .exs();
    }

    default long count(String flowName, String flowType) {
        return select().col(AggTag.COUNT, FlowDefinition::getCountId).where()
                .like(flowName != null && !flowName.isEmpty(), FlowDefinition::getFlowName, flowName)
                .eq(flowType != null && !flowType.isEmpty(), FlowDefinition::getFlowType, flowType)
                .ex()
                .getCountId();
    }
}
