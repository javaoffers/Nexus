package com.javaoffers.nexus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.javaoffers.nexus.core.repository")
@EnableAsync
public class NexusApplication {
    public static void main(String[] args) {
        SpringApplication.run(NexusApplication.class, args);
    }
}
