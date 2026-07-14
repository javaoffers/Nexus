package com.javaoffers.nexus.core.middleware;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * 对应 Go main.go 的 setupStaticFiles / NoRoute：托管 ../console-ui/dist 前端 SPA。
 * dist 不存在时进入 API-only 模式。匹配优先级最低，不影响 /api、/open 等真实路由。
 */
@Slf4j
@Configuration
public class SpaStaticHandler {

    private static final String DIST_DIR = "../console-ui/dist";
    private static final Map<String, String> CONTENT_TYPES = new HashMap<>();

    static {
        CONTENT_TYPES.put(".html", "text/html;charset=UTF-8");
        CONTENT_TYPES.put(".js", "application/javascript;charset=UTF-8");
        CONTENT_TYPES.put(".css", "text/css;charset=UTF-8");
        CONTENT_TYPES.put(".json", "application/json;charset=UTF-8");
        CONTENT_TYPES.put(".svg", "image/svg+xml");
        CONTENT_TYPES.put(".png", "image/png");
        CONTENT_TYPES.put(".jpg", "image/jpeg");
        CONTENT_TYPES.put(".ico", "image/x-icon");
        CONTENT_TYPES.put(".woff", "font/woff");
        CONTENT_TYPES.put(".woff2", "font/woff2");
        CONTENT_TYPES.put(".map", "application/json;charset=UTF-8");
    }

    @PostConstruct
    public void init() {
        File dist = new File(DIST_DIR);
        if (!dist.exists()) {
            log.info("Frontend dist not found at {}, running in API-only mode", DIST_DIR);
        }
    }

    public boolean handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/api/") || path.startsWith("/open/") || path.startsWith("/example/")) {
            response.setStatus(404);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"not found\"}");
            return true;
        }
        File dist = new File(DIST_DIR);
        if (!dist.exists()) {
            response.setStatus(404);
            return true;
        }
        File target = new File(dist, path);
        if (target.exists() && target.isFile()) {
            writeFile(response, target);
            return true;
        }
        File index = new File(dist, "index.html");
        if (index.exists()) {
            writeFile(response, index);
            return true;
        }
        response.setStatus(404);
        return true;
    }

    private void writeFile(HttpServletResponse response, File file) throws IOException {
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        String contentType = (dot >= 0) ? CONTENT_TYPES.get(name.substring(dot)) : null;
        if (contentType == null) {
            contentType = Files.probeContentType(file.toPath());
            if (contentType == null) contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        response.setContentType(contentType);
        response.setContentLengthLong(file.length());
        Files.copy(file.toPath(), response.getOutputStream());
    }
}
