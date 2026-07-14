package com.javaoffers.nexus.core.repository;

import com.javaoffers.brief.modelhelper.core.Id;
import com.javaoffers.brief.modelhelper.fun.AggTag;
import com.javaoffers.brief.modelhelper.mapper.BriefMapper;
import com.javaoffers.nexus.core.model.DataSource;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DataSourceMapper extends BriefMapper<DataSource> {

    default void create(DataSource d) {
        Id id = insert().colAll(d).ex();
        d.setId(id.toLong());
    }

    /** 对应 Go repository.UpdateDataSource 的 db.Save（全字段更新）。 */
    default void update(DataSource d) {
        update().updateNull().colAll(d).where().eq(DataSource::getId, d.getId()).ex();
    }

    default void delete(Long id) {
        general().logicRemoveById(id);
    }

    default DataSource getById(Long id) {
        return select().colAll().where().eq(DataSource::getId, id).ex();
    }

    default List<DataSource> listAll() {
        return select().colAll().where().exs();
    }

    default List<DataSource> page(int pageNum, int pageSize, String name, String dsType) {
        return select().colAll().where()
                .like(name != null && !name.isEmpty(), DataSource::getDataSourceName, name)
                .eq(dsType != null && !dsType.isEmpty(), DataSource::getDataSourceType, dsType)
                .orderD(DataSource::getId)
                .limitPage(pageNum, pageSize)
                .exs();
    }

    default long count(String name, String dsType) {
        return select().col(AggTag.COUNT, DataSource::getCountId).where()
                .like(name != null && !name.isEmpty(), DataSource::getDataSourceName, name)
                .eq(dsType != null && !dsType.isEmpty(), DataSource::getDataSourceType, dsType)
                .ex()
                .getCountId();
    }
}
