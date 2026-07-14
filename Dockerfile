# Stage 1: Build frontend
FROM node:18-alpine AS frontend-builder
WORKDIR /app/console-ui
COPY console-ui/package*.json ./
RUN npm install
COPY console-ui/ ./
RUN npm run build

# Stage 2: Build server jar with Maven
FROM maven:3.8.8-eclipse-temurin-8 AS server-builder
WORKDIR /build
# 先拷 pom,利用层缓存拉依赖
COPY pom.xml ./
COPY server/pom.xml ./server/
COPY dag-engine/pom.xml ./dag-engine/
COPY sample/pom.xml ./sample/
COPY sample/item-server/pom.xml ./sample/item-server/
RUN mvn -q -pl server -am -DskipTests dependency:resolve
# 再拷源码,编译 server(及其依赖模块)
COPY server/src ./server/src
COPY dag-engine/src ./dag-engine/src
RUN mvn -q -pl server -am -DskipTests package

# Stage 3: Runtime
FROM eclipse-temurin:8-jre-alpine
RUN apk add --no-cache ca-certificates tzdata
ENV TZ=Asia/Shanghai

WORKDIR /app
COPY --from=server-builder /build/server/target/server-1.0.0.jar ./app.jar
COPY --from=frontend-builder /app/console-ui/dist ./static

RUN mkdir -p data

EXPOSE 9127
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
