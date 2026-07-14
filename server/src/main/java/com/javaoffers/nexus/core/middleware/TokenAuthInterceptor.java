package com.javaoffers.nexus.core.middleware;

import com.javaoffers.nexus.core.repository.TokenMapper;
import com.javaoffers.nexus.pkg.response.R;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 对应 Go core/middleware/token_auth.go：校验 Nexus-Token 存在且有效，
 * 解析 base64 编码的 {userId,timestamp}，将 userId 放入 request 属性。
 */
@Component
public class TokenAuthInterceptor implements HandlerInterceptor {

    private final TokenMapper tokenMapper;

    public TokenAuthInterceptor(TokenMapper tokenMapper) {
        this.tokenMapper = tokenMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Nexus-Token");
        if (token == null || token.isEmpty()) {
            token = request.getParameter("nexusToken");
        }
        if (token == null || token.isEmpty()) {
            R.writeError(response, 401, 401, "缺少Token");
            return false;
        }
        if (!tokenMapper.existsByTokenValue(token)) {
            R.writeError(response, 401, 401, "Token无效");
            return false;
        }
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(token.getBytes(StandardCharsets.UTF_8));
            String json = new String(decoded, StandardCharsets.UTF_8);
            // 朴素解析 "userId":123
            long uid = extractUserId(json);
            if (uid > 0) {
                request.setAttribute(JwtAuthInterceptor.USER_ID, uid);
            }
        } catch (Exception ignore) {
            // 解析失败不阻断，仅不设置 userId
        }
        return true;
    }

    private long extractUserId(String json) {
        int i = json.indexOf("\"userId\"");
        if (i < 0) return 0;
        int colon = json.indexOf(':', i);
        if (colon < 0) return 0;
        int start = colon + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)))) end++;
        if (end == start) return 0;
        try {
            return Long.parseLong(json.substring(start, end));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
