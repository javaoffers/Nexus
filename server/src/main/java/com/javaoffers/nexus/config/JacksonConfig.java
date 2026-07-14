package com.javaoffers.nexus.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局过滤 brief 实体中不应暴露给前端的内部字段（deleted、countId），
 * 使响应 JSON 结构与原 Go 版（BaseModel.Deleted json:"-"、无 countId）保持一致。
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer nexusJacksonCustomizer() {
        return builder -> {
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            SimpleModule module = new SimpleModule();
            module.setSerializerModifier(new BeanSerializerModifier() {
                @Override
                public List<BeanPropertyWriter> changeProperties(
                        com.fasterxml.jackson.databind.SerializationConfig config,
                        com.fasterxml.jackson.databind.BeanDescription beanDesc,
                        List<BeanPropertyWriter> beanProperties) {
                    return super.changeProperties(config, beanDesc, beanProperties).stream()
                            .filter(p -> !"deleted".equals(p.getName()) && !"countId".equals(p.getName()))
                            .collect(Collectors.toList());
                }
            });
            builder.modulesToInstall(module);
        };
    }
}
