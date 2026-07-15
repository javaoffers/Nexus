package com.javaoffers.nexus.core.repository;

import com.javaoffers.brief.modelhelper.fun.AggTag;
import com.javaoffers.brief.modelhelper.mapper.BriefMapper;
import com.javaoffers.nexus.core.model.NexusObject;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ObjectMapper extends BriefMapper<NexusObject> {

    default void create(NexusObject o) {
        insert().colAll(o).ex();
    }

    default NexusObject getByKey(String objectKey) {
        return select().colAll().where().eq(NexusObject::getObjectKey, objectKey).ex();
    }

    default void update(NexusObject o) {
        update().npdateNull()
                .col(NexusObject::getObjectKey, o.getObjectKey())
                .col(NexusObject::getObjectName, o.getObjectName())
                .col(NexusObject::getObjectDesc, o.getObjectDesc())
                .where().eq(NexusObject::getId, o.getId()).ex();
    }

    default void delete(Long id) {
        general().logicRemoveById(id);
    }

    default NexusObject getById(Long id) {
        return select().colAll().where().eq(NexusObject::getId, id).ex();
    }

    default List<NexusObject> listAll() {
        return select().colAll().where().exs();
    }

    default List<NexusObject> page(int pageNum, int pageSize, String objectName) {
        return select().colAll().where()
                .like(objectName != null && !objectName.isEmpty(), NexusObject::getObjectName, objectName)
                .orderD(NexusObject::getId)
                .limitPage(pageNum, pageSize)
                .exs();
    }

    default long count(String objectName) {
        return select().col(AggTag.COUNT, NexusObject::getCountId).where()
                .like(objectName != null && !objectName.isEmpty(), NexusObject::getObjectName, objectName)
                .ex()
                .getCountId();
    }

    default boolean objectKeyExists(String objectKey, Long excludeId) {
        long c = select().col(AggTag.COUNT, NexusObject::getCountId).where()
                .eq(NexusObject::getObjectKey, objectKey)
                .ueq(excludeId != null, NexusObject::getId, excludeId)
                .ex()
                .getCountId();
        return c > 0;
    }
}
