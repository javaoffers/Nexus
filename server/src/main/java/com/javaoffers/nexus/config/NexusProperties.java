package com.javaoffers.nexus.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 绑定 application.yml 中 nexus.* 配置，键名与原 config.yaml 保持一致。
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "nexus")
public class NexusProperties {

    private ServerCfg server = new ServerCfg();
    private DatabaseCfg database = new DatabaseCfg();
    private AuthCfg auth = new AuthCfg();
    private CacheCfg cache = new CacheCfg();
    private NexusCfg nexus = new NexusCfg();

    @Data
    public static class ServerCfg {
        private int port = 9127;
    }

    @Data
    public static class DatabaseCfg {
        private String driver = "sqlite";
        private SqliteCfg sqlite = new SqliteCfg();
        private MysqlCfg mysql = new MysqlCfg();
    }

    @Data
    public static class SqliteCfg {
        private String path = "/Users/cmj/IdeaProjects/Nexus/server/data/nexus.db";
    }

    @Data
    public static class MysqlCfg {
        private String host = "127.0.0.1";
        private int port = 3306;
        private String database = "nexus";
        private String username = "root";
        private String password = "123456";
    }

    @Data
    public static class AuthCfg {
        private String jwtSecret = "www.javaoffers.com####www.nexus.plus";
        private int jwtExpire = 7200;
    }

    @Data
    public static class CacheCfg {
        private String type = "memory";
        private RedisCfg redis = new RedisCfg();
    }

    @Data
    public static class RedisCfg {
        private String addr = "127.0.0.1:6379";
        private String password = "";
    }

    @Data
    public static class NexusCfg {
        private String apiKey = "";
        private String openServerAddr = "https://open.nexus.plus";
    }
}
