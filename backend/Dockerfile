# 基于已生成的 JAR 文件构建镜像
FROM amazoncorretto:17-alpine-jdk

# 设置工作目录
WORKDIR /app

# 复制已生成的 JAR 文件
# 注意：JAR 文件需要在构建上下文中（backend 目录根目录或 target 目录）
COPY ecommerce-backend-1.0.0.jar app.jar

# 创建上传文件目录和配置目录
RUN mkdir -p /app/uploads /app/config

# 复制生产环境配置文件到 config 目录（便于外部挂载和修改）
COPY src/main/resources/application-prod.yml /app/config/application-prod.yml

# 安装 curl 用于健康检查
RUN apk add --no-cache curl

# 暴露端口
EXPOSE 8080

# 设置 JVM 参数（可根据需要调整）
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/api/products || exit 1

# 启动应用（支持环境变量配置和外部配置文件）
# 优先使用外部配置文件（/app/config/application-prod.yml），如果不存在则使用 JAR 内的配置
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -Dspring.config.additional-location=file:/app/config/ -jar app.jar"]

