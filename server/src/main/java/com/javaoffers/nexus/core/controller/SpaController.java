package com.javaoffers.nexus.core.controller;

import com.javaoffers.nexus.core.middleware.SpaStaticHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 前端 SPA 兜底：未被具体 @RestController 匹配的 GET 请求交给静态资源托管。
 * 优先级最低，不影响 /api/v1/**、/open/v1/** 等真实接口。
 */
@Controller
public class SpaController {

    private final SpaStaticHandler spa;

    public SpaController(SpaStaticHandler spa) {
        this.spa = spa;
    }

    @GetMapping("/**")
    public void forward(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!spa.handle(request, response)) {
            response.setStatus(404);
        }
    }
}
