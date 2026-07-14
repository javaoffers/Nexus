package com.javaoffers.nexus.core.middleware;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 对应 Go router.go 的路由分组鉴权：
 *  /api/v1/** 走 JWT（除 login / product info 公开），/open/v1/** 走 Token。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtAuthInterceptor jwtAuthInterceptor;
    private final TokenAuthInterceptor tokenAuthInterceptor;

    public WebMvcConfig(JwtAuthInterceptor jwtAuthInterceptor, TokenAuthInterceptor tokenAuthInterceptor) {
        this.jwtAuthInterceptor = jwtAuthInterceptor;
        this.tokenAuthInterceptor = tokenAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns("/api/v1/user/login", "/api/v1/user/product/info");
        registry.addInterceptor(tokenAuthInterceptor)
                .addPathPatterns("/open/v1/**");
    }
}
