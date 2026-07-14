package com.javaoffers.nexus.core.service;

import com.javaoffers.nexus.core.model.User;
import com.javaoffers.nexus.core.repository.UserMapper;
import com.javaoffers.nexus.pkg.auth.JwtUtil;
import com.javaoffers.nexus.pkg.exception.BizException;
import lombok.Data;
import org.springframework.stereotype.Service;

/** 对应 Go core/service/user_service.go。 */
@Service
public class UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    public UserService(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    @Data
    public static class LoginResult {
        private String userName;
        private String token;
    }

    @Data
    public static class UserInfo {
        private Long id;
        private String userName;
    }

    public LoginResult login(String userName, String password) {
        User user = userMapper.getByName(userName);
        if (user == null) {
            throw new BizException("用户名或密码错误");
        }
        if (password == null || !password.equals(user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        LoginResult r = new LoginResult();
        r.setUserName(user.getUserName());
        r.setToken(jwtUtil.generateToken(user.getId()));
        return r;
    }

    public UserInfo getUserInfo(Long userId) {
        User user = userMapper.getById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        UserInfo info = new UserInfo();
        info.setId(user.getId());
        info.setUserName(user.getUserName());
        return info;
    }

    public void updatePassword(Long userId, String oldPwd, String newPwd) {
        User user = userMapper.getById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        if (oldPwd == null || !oldPwd.equals(user.getPassword())) {
            throw new BizException("原密码错误");
        }
        userMapper.updatePassword(userId, newPwd);
    }
}
