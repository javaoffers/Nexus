package com.javaoffers.nexus.core.repository;

import com.javaoffers.brief.modelhelper.mapper.BriefMapper;
import com.javaoffers.nexus.core.model.Parameter;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface ParameterMapper extends BriefMapper<Parameter> {

    default void create(Parameter p) {
        insert().colAll(p).ex();
    }

    default void batchCreate(Collection<Parameter> params) {
        if (params == null || params.isEmpty()) {
            return;
        }
        general().saveBatch(params);
    }

    /** 硬删除（原 GORM Delete 未走软删除），按来源清理参数。 */
    default void deleteBySource(String sourceType, Long sourceId) {
        delete().where()
                .eq(Parameter::getSourceType, sourceType)
                .eq(Parameter::getSourceId, sourceId)
                .ex();
    }

    default List<Parameter> listBySource(String sourceType, Long sourceId) {
        return select().colAll().where()
                .eq(Parameter::getSourceType, sourceType)
                .eq(Parameter::getSourceId, sourceId)
                .exs();
    }

    default List<Parameter> listBySourceAndType(String sourceType, Long sourceId, int paramType) {
        return select().colAll().where()
                .eq(Parameter::getSourceType, sourceType)
                .eq(Parameter::getSourceId, sourceId)
                .eq(Parameter::getParamType, paramType)
                .exs();
    }
}
