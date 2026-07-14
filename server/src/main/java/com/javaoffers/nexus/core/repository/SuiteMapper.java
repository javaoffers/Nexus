package com.javaoffers.nexus.core.repository;

import com.javaoffers.brief.modelhelper.mapper.BriefMapper;
import com.javaoffers.brief.modelhelper.core.Id;
import com.javaoffers.nexus.core.model.Suite;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SuiteMapper extends BriefMapper<Suite> {

    default void create(Suite s) {
        Id id = insert().colAll(s).ex();
        s.setId(id.toLong());
    }

    default void update(Suite s) {
        update().npdateNull()
                .col(Suite::getSuiteCode, s.getSuiteCode())
                .col(Suite::getSuiteName, s.getSuiteName())
                .col(Suite::getSuiteImage, s.getSuiteImage())
                .col(Suite::getSuiteDesc, s.getSuiteDesc())
                .where().eq(Suite::getId, s.getId()).ex();
    }

    default void delete(Long id) {
        general().logicRemoveById(id);
    }

    default Suite getById(Long id) {
        return select().colAll().where().eq(Suite::getId, id).ex();
    }

    default Suite getByCode(String suiteCode) {
        return select().colAll().where().eq(Suite::getSuiteCode, suiteCode).ex();
    }

    default List<Suite> listAll() {
        return select().colAll().where().exs();
    }

    default List<Suite> page(int pageNum, int pageSize, String suiteName, Integer suiteFlag) {
        return select().colAll().where()
                .like(suiteName != null && !suiteName.isEmpty(), Suite::getSuiteName, suiteName)
                .eq(suiteFlag != null, Suite::getSuiteFlag, suiteFlag)
                .orderD(Suite::getId)
                .limitPage(pageNum, pageSize)
                .exs();
    }

    default long count(String suiteName, Integer suiteFlag) {
        return select().col(com.javaoffers.brief.modelhelper.fun.AggTag.COUNT, Suite::getCountId).where()
                .like(suiteName != null && !suiteName.isEmpty(), Suite::getSuiteName, suiteName)
                .eq(suiteFlag != null, Suite::getSuiteFlag, suiteFlag)
                .ex()
                .getCountId();
    }
}
