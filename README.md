# Nexus

A visual API orchestration platform — design API call flows with drag-and-drop.

## Tech Stack

- **Backend**: Go (Gin + GORM)
- **Frontend**: Vue 3 + TypeScript + Element Plus + D3.js
- **Database**: SQLite (default) / MySQL (optional)
- **Cache**: In-memory (default) / Redis (optional)

## Quick Start

### Build & Run

```bash
# Build backend
go build -o nexus-server .

# Run (default port 9127)
./nexus-server

# Build frontend (optional, can run separately during development)
cd console-ui && npm install && npm run build
```

### Configuration

Edit `config/config.yaml`:

```yaml
server:
  port: 9127

database:
  driver: sqlite    # sqlite or mysql
  sqlite:
    path: ./data/nexus.db
  mysql:
    host: 127.0.0.1
    port: 3306
    database: nexus
    username: root
    password: "123456"

cache:
  type: memory      # memory or redis
  redis:
    addr: "127.0.0.1:6379"
```

### Docker

```bash
docker build -t nexus .
docker run -p 9127:9127 nexus
```

### Default Account

- Username: `nexus`
- Password: `nexus123`

## Project Structure

```
├── main.go              # Entry point
├── config/              # Configuration
├── core/
│   ├── model/           # Data models
│   ├── repository/      # Data access layer
│   ├── service/         # Business logic layer
│   ├── handler/         # HTTP handlers
│   ├── middleware/       # Middleware (JWT/Token/CORS)
│   ├── engine/          # Flow orchestration engine
│   └── cache/           # Cache abstraction layer
├── pkg/                 # Common utilities
├── router/              # Route registration
├── db/                  # Database initialization
├── console-ui/          # Vue frontend
└── example/             # Standalone example project
```

## License

GPLv3
