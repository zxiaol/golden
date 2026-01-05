# 多阶段构建 - 第一阶段：构建 JAR 文件
FROM maven:3.9-eclipse-temurin-17 AS build

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 和源代码
COPY pom.xml .
COPY src ./src

# 构建应用（跳过测试以加快构建速度，生产环境可以运行测试）
RUN mvn clean package -DskipTests

# 第二阶段：运行 JAR 文件
FROM eclipse-temurin:17-jre-alpine

# 设置工作目录
WORKDIR /app

# 创建非 root 用户
RUN addgroup -S spring && adduser -S spring -G spring

# 从构建阶段复制 JAR 文件
COPY --from=build /app/target/ecommerce-backend-1.0.0.jar app.jar

# 创建上传文件目录
RUN mkdir -p /app/uploads && chown -R spring:spring /app

# 切换到非 root 用户
USER spring:spring

# 暴露端口
EXPOSE 8080

# 设置 JVM 参数（可根据需要调整）
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# 安装 curl 用于健康检查
USER root
RUN apk add --no-cache curl
USER spring:spring

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/api/products || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

