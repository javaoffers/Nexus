package com.javaoffers.nexus.core.repository;

import com.javaoffers.brief.modelhelper.mapper.BriefMapper;
import com.javaoffers.nexus.core.model.VariableInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface VariableInfoMapper extends BriefMapper<VariableInfo> {

    default void batchCreate(Collection<VariableInfo> vars) {
        if (vars == null || vars.isEmpty()) {
            return;
        }
        general().saveBatch(vars);
    }

    /** 硬删除（原 GORM Delete，无软删除列）。 */
    default void deleteByFlowDefId(Long flowDefId) {
        delete().where().eq(VariableInfo::getFlowDefinitionId, flowDefId).ex();
    }

    default List<VariableInfo> listByFlowDefId(Long flowDefId) {
        return select().colAll().where().eq(VariableInfo::getFlowDefinitionId, flowDefId).exs();
    }
}
