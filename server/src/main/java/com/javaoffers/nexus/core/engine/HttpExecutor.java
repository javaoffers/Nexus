package com.javaoffers.nexus.core.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 对应 Go engine/httpclient.go 的 SendHTTPRequest：动态 HTTP 调用，返回解析后的 JSON Map。
 * 同时供 api_service.DebugAPI 复用。
 */
@Slf4j
@Component
public class HttpExecutor {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int TIMEOUT_MS = 30_000;

    @SuppressWarnings("unchecked")
    public Map<String, Object> sendRequest(MethodDef method, Map<String, Object> headers, Map<String, Object> params) {
        if (params == null) params = new HashMap<>();
        if (headers == null) headers = new HashMap<>();
        String url = method.getUrl();
        if (url == null || url.isEmpty()) {
            return null;
        }

        // 替换路径变量 {key}
        for (Map.Entry<String, Object> e : new HashMap<>(params).entrySet()) {
            String placeholder = "{" + e.getKey() + "}";
            if (url.contains(placeholder)) {
                url = url.replace(placeholder, String.valueOf(e.getValue()));
                params.remove(e.getKey());
            }
        }

        try {
            String requestType = method.getRequestType() == null ? "GET" : method.getRequestType().toUpperCase();
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setDoInput(true);

            if ("GET".equals(requestType)) {
                conn.setRequestMethod("GET");
                String qs = buildQueryString(params);
                if (!qs.isEmpty()) {
                    u = new URL(url + (url.contains("?") ? "&" : "?") + qs);
                    conn = (HttpURLConnection) u.openConnection();
                    conn.setConnectTimeout(TIMEOUT_MS);
                    conn.setReadTimeout(TIMEOUT_MS);
                }
            } else {
                conn.setRequestMethod(requestType);
                conn.setDoOutput(true);
                String ct = method.getRequestContentType();
                if (ct == null || ct.isEmpty()) {
                    ct = "application/json";
                }
                conn.setRequestProperty("Content-Type", ct);
                byte[] body = MAPPER.writeValueAsBytes(params);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(body);
                }
            }

            for (Map.Entry<String, Object> e : headers.entrySet()) {
                conn.setRequestProperty(e.getKey(), String.valueOf(e.getValue()));
            }

            int code = conn.getResponseCode();
            InputStream is = (code >= 400) ? conn.getErrorStream() : conn.getInputStream();
            String respBody = readAll(is);

            try {
                Object parsed = MAPPER.readValue(respBody, Object.class);
                if (parsed instanceof Map) {
                    return (Map<String, Object>) parsed;
                }
            } catch (Exception ignore) {
                // 非 JSON，按原文包装
            }
            Map<String, Object> wrap = new HashMap<>();
            wrap.put("response", respBody);
            return wrap;
        } catch (Exception e) {
            log.warn("[Engine] HTTP request failed: {}", e.getMessage());
            return null;
        }
    }

    private String buildQueryString(Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Object> e : params.entrySet()) {
            if (!first) sb.append('&');
            sb.append(urlEncode(e.getKey())).append('=').append(urlEncode(String.valueOf(e.getValue())));
            first = false;
        }
        return sb.toString();
    }

    private String urlEncode(String s) {
        try {
            return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return s;
        }
    }

    private String readAll(InputStream is) {
        if (is == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (Exception e) {
            log.warn("[Engine] read response failed: {}", e.getMessage());
        }
        return sb.toString();
    }
}
