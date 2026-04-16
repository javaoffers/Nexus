# Nexus

可视化 API 编排平台 —— 通过拖拽方式编排 API 调用流程。

## 技术栈

- **后端**: Go (Gin + GORM)
- **前端**: Vue 3 + TypeScript + Element Plus + D3.js
- **数据库**: SQLite (默认) / MySQL (可选)
- **缓存**: 内存 (默认) / Redis (可选)

## 快速开始

### 编译运行

```bash
# 编译后端
go build -o nexus-server .

# 运行 (默认端口 9127)
./nexus-server

# 编译前端 (可选，开发时前端可单独运行)
cd console-ui && npm install && npm run build
```

### 配置

编辑 `config/config.yaml`:

```yaml
server:
  port: 9127

database:
  driver: sqlite    # sqlite 或 mysql
  sqlite:
    path: ./data/nexus.db
  mysql:
    host: 127.0.0.1
    port: 3306
    database: nexus
    username: root
    password: "123456"

cache:
  type: memory      # memory 或 redis
  redis:
    addr: "127.0.0.1:6379"
```

### Docker

```bash
docker build -t nexus .
docker run -p 9127:9127 nexus
```

### 默认账号

- 用户名: `nexus`
- 密码: `nexus123`

## 项目结构

```
├── main.go              # 入口
├── config/              # 配置
├── internal/
│   ├── model/           # 数据模型
│   ├── repository/      # 数据访问层
│   ├── service/         # 业务逻辑层
│   ├── handler/         # HTTP 处理器
│   ├── middleware/       # 中间件 (JWT/Token/CORS)
│   ├── engine/          # 流程编排执行引擎
│   └── cache/           # 缓存抽象层
├── pkg/                 # 公共工具包
├── router/              # 路由注册
├── db/                  # 数据库初始化
├── console-ui/          # Vue 前端
└── example/             # 独立示例项目
```

## License

GPLv3
