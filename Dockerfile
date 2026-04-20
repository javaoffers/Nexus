# Stage 1: Build frontend
FROM node:18-alpine AS frontend-builder
WORKDIR /app/console-ui
COPY console-ui/package*.json ./
RUN npm install
COPY console-ui/ ./
RUN npm run build

# Stage 2: Build Go binary
FROM golang:1.21-alpine AS go-builder
RUN apk add --no-cache gcc musl-dev
WORKDIR /app
COPY server/go.mod server/go.sum ./server/
RUN cd server && go mod download
COPY server/ ./server/
COPY --from=frontend-builder /app/console-ui/dist ./console-ui/dist
RUN cd server && CGO_ENABLED=1 GOOS=linux go build -o /app/nexus-server .

# Stage 3: Runtime
FROM alpine:3.19
RUN apk add --no-cache ca-certificates tzdata
ENV TZ=Asia/Shanghai

WORKDIR /home/nexus
COPY --from=go-builder /app/nexus-server .
COPY --from=go-builder /app/server/config/config.yaml ./config/

RUN mkdir -p data

EXPOSE 9127
ENTRYPOINT ["./nexus-server"]
