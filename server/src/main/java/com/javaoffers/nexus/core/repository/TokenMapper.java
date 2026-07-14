package com.javaoffers.nexus.core.repository;

import com.javaoffers.brief.modelhelper.core.Id;
import com.javaoffers.brief.modelhelper.fun.AggTag;
import com.javaoffers.brief.modelhelper.mapper.BriefMapper;
import com.javaoffers.nexus.core.model.Token;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TokenMapper extends BriefMapper<Token> {

    default void create(Token t) {
        Id id = insert().colAll(t).ex();
        t.setId(id.toLong());
    }

    default void delete(Long id) {
        general().logicRemoveById(id);
    }

    default List<Token> page(int pageNum, int pageSize) {
        return select().colAll().where().orderD(Token::getId).limitPage(pageNum, pageSize).exs();
    }

    default long count() {
        return select().col(AggTag.COUNT, Token::getCountId).where().ex().getCountId();
    }

    default boolean existsByTokenValue(String tokenValue) {
        return select().col(AggTag.COUNT, Token::getCountId).where().eq(Token::getTokenValue, tokenValue).ex().getCountId() > 0;
    }
}
