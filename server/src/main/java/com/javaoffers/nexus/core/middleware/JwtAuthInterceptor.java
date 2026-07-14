package com.javaoffers.nexus.core.middleware;

import com.javaoffers.nexus.pkg.auth.JwtUtil;
import com.javaoffers.nexus.pkg.response.R;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 对应 Go core/middleware/jwt_auth.go。
 * 解析 Authorization: Bearer <jwt>，将 userId 放入 request 属性。
 */
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    public static final String USER_ID = "userId";

    private final JwtUtil jwtUtil;

    public JwtAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String auth = request.getHeader("Authorization");
        if (auth == null || auth.isEmpty()) {
            R.writeError(response, 401, 401, "未登录");
            return false;
        }
        String token = auth.startsWith("Bearer ") ? auth.substring(7) : auth;
        Long userId = jwtUtil.parseToken(token);
        if (userId == null) {
            R.writeError(response, 401, 401, "登录已过期");
            return false;
        }
        request.setAttribute(USER_ID, userId);
        return true;
    }
}
