package com.javaoffers.nexus.core.repository;

import com.javaoffers.brief.modelhelper.core.Id;
import com.javaoffers.brief.modelhelper.fun.AggTag;
import com.javaoffers.brief.modelhelper.mapper.BriefMapper;
import com.javaoffers.nexus.core.model.API;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApiMapper extends BriefMapper<API> {

    default void create(API a) {
        insert().colAll(a).ex();
    }

    /** 取最新插入的一行（brief 不回填自增 id，用于 create 后取回真实 id）。 */
    default API findNewest() {
        return select().colAll().where().orderD(API::getId).limitPage(1, 1).ex();
    }

    default void update(API a) {
        update().npdateNull()
                .col(API::getSuiteId, a.getSuiteId())
                .col(API::getApiUrl, a.getApiUrl())
                .col(API::getApiName, a.getApiName())
                .col(API::getApiDesc, a.getApiDesc())
                .col(API::getApiRequestType, a.getApiRequestType())
                .col(API::getApiRequestContentType, a.getApiRequestContentType())
                .where().eq(API::getId, a.getId()).ex();
    }

    default void delete(Long id) {
        general().logicRemoveById(id);
    }

    default API getById(Long id) {
        return select().colAll().where().eq(API::getId, id).ex();
    }

    default API getByCode(String code) {
        return select().colAll().where().eq(API::getApiCode, code).ex();
    }

    default List<API> listBySuiteId(Long suiteId) {
        return select().colAll().where().eq(API::getSuiteId, suiteId).exs();
    }

    default List<API> page(int pageNum, int pageSize, Long suiteId, String apiName, String apiUrl) {
        return select().colAll().where()
                .eq(suiteId != null, API::getSuiteId, suiteId)
                .like(apiName != null && !apiName.isEmpty(), API::getApiName, apiName)
                .like(apiUrl != null && !apiUrl.isEmpty(), API::getApiUrl, apiUrl)
                .orderD(API::getId)
                .limitPage(pageNum, pageSize)
                .exs();
    }

    default long count(Long suiteId, String apiName, String apiUrl) {
        return select().col(AggTag.COUNT, API::getCountId).where()
                .eq(suiteId != null, API::getSuiteId, suiteId)
                .like(apiName != null && !apiName.isEmpty(), API::getApiName, apiName)
                .like(apiUrl != null && !apiUrl.isEmpty(), API::getApiUrl, apiUrl)
                .ex()
                .getCountId();
    }

    default long countBySuiteId(Long suiteId) {
        return select().col(AggTag.COUNT, API::getCountId).where().eq(API::getSuiteId, suiteId).ex().getCountId();
    }
}
