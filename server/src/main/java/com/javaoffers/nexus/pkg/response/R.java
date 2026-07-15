package com.javaoffers.nexus.pkg.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 统一响应
 */
@Slf4j
public class R {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <T> Result<T> ok(T data) {
        return Result.ok(data);
    }

    public static <T> PageResult<T> okPage(long total, T data) {
        return PageResult.ok(total, data);
    }

    public static <T> Result<T> error(int code, String msg) {
        return Result.error(code, msg);
    }

    public static void writeError(HttpServletResponse response, int httpCode, int code, String msg) {
        response.setStatus(httpCode);
        response.setContentType("application/json;charset=UTF-8");
        try {
            response.getWriter().write(MAPPER.writeValueAsString(Result.error(code, msg)));
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("write error response failed", e);
        }
    }
}
