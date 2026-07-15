package com.javaoffers.nexus.core.repository;

import com.javaoffers.brief.modelhelper.mapper.BriefMapper;
import com.javaoffers.nexus.core.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BriefMapper<User> {

    default User getByName(String userName) {
        return select().colAll().where().eq(User::getUserName, userName).ex();
    }

    default User getById(Long id) {
        return select().colAll().where().eq(User::getId, id).ex();
    }

    default void updatePassword(Long id, String password) {
        update().npdateNull().col(User::getPassword, password).where().eq(User::getId, id).ex();
    }
}
