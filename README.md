# Nexus

A visual API orchestration platform — design API call flows with drag-and-drop.

## Tech Stack

- **Backend**: Java 8 / Spring Boot 2.7 + MyBatis + brief ORM + Aviator (expression engine)
- **Frontend**: Vue 3 + TypeScript + Element Plus + D3.js
- **Database**: SQLite (default) / MySQL (optional)
- **Cache**: Caffeine in-memory (default) / Redis (optional)

## Modules

```
nexus               # 顶层父 POM (packaging=pom),聚合所有子模块
├── server          # 主服务,Spring Boot 应用,端口 9127
├── dag-engine      # DAG 流程编排引擎(库)
└── sample
    └── item-server # 示例 mock item API,端口 8090
```

所有子模块继承顶层 `nexus` 父 POM,再由 `spring-boot-starter-parent` 传递统一版本管理。

## Quick Start

### Build & Run

```bash
# 编译整个 reactor(根目录执行)
mvn clean package -DskipTests

# 运行主服务(默认端口 9127)
java -jar server/target/server-1.0.0.jar

# 开发期也可直接跑
mvn -pl server spring-boot:run

# 构建前端(可选,开发期可单独运行)
cd console-ui && npm install && npm run build
```

### Configuration

主服务配置位于 `server/src/main/resources/application.yml`:

```yaml
server:
  port: 9127

spring:
  profiles:
    active: sqlite          # sqlite 或 mysql
  datasource:
    driver-class-name: org.sqlite.JDBC
    url: jdbc:sqlite:./data/nexus.db

nexus:
  database:
    driver: sqlite          # sqlite or mysql
    mysql:
      host: 127.0.0.1
      port: 3306
      database: nexus
      username: root
      password: "123456"
  auth:
    jwt-secret: "www.javaoffers.com####www.nexus.plus"
    jwt-expire: 7200
  cache:
    type: memory            # memory or redis
    redis:
      addr: "127.0.0.1:6379"
```

### Docker

```bash
docker compose up --build        # 构建并运行,映射 9127 端口
```

### Default Account

- Username: `nexus`
- Password: `nexus123`

## Project Structure

```
├── server/                      # 主 Spring Boot 服务
│   └── src/main/java/com/javaoffers/nexus/
│       ├── NexusApplication.java
│       ├── core/                # model / repository / service / handler / engine
│       ├── config/              # 配置
│       └── pkg/                 # 通用工具
├── dag-engine/                  # DAG 编排引擎
├── sample/item-server/          # 示例 mock API 服务
├── console-ui/                  # Vue 前端
├── pom.xml                      # 顶层父 POM
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## License

GPLv3
