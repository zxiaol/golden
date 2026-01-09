# 外部配置文件使用说明

## 概述

Spring Boot 支持在 JAR 文件外部放置配置文件，外部配置文件的优先级高于 JAR 内部的配置。

## 外部配置文件位置（按优先级从高到低）

### 1. 当前目录下的 `/config` 子目录（最高优先级）
```
/app/
├── app.jar
└── config/
    └── application.yml
```

### 2. 当前目录
```
/app/
├── app.jar
└── application.yml
```

### 3. classpath 下的 `/config` 目录
```
src/main/resources/config/application.yml
```

### 4. classpath 根目录（JAR 内部，最低优先级）
```
src/main/resources/application.yml
```

## 使用方法

### 方法一：放在 JAR 同目录下

#### 1. 创建外部配置文件
```bash
# 在 JAR 文件所在目录创建
/app/
├── ecommerce-backend-1.0.0.jar
└── application.yml
```

#### 2. 启动应用
```bash
cd /app
java -jar ecommerce-backend-1.0.0.jar
```

Spring Boot 会自动加载同目录下的 `application.yml`。

### 方法二：放在 `/config` 子目录（推荐）

#### 1. 创建目录结构
```bash
/app/
├── ecommerce-backend-1.0.0.jar
└── config/
    ├── application.yml
    ├── application-prod.yml
    └── application-dev.yml
```

#### 2. 启动应用
```bash
cd /app
java -jar ecommerce-backend-1.0.0.jar
```

### 方法三：通过命令行指定配置文件位置

```bash
java -jar app.jar --spring.config.location=file:/path/to/config/application.yml
```

或者指定目录：
```bash
java -jar app.jar --spring.config.location=file:/path/to/config/
```

### 方法四：通过环境变量指定

```bash
export SPRING_CONFIG_LOCATION=file:/path/to/config/application.yml
java -jar app.jar
```

## Docker 环境中的使用

### 方式一：使用 Volume 挂载配置文件

#### 1. 创建外部配置文件
```bash
# 在宿主机创建配置文件
mkdir -p /opt/ecommerce/config
cat > /opt/ecommerce/config/application-prod.yml << EOF
spring:
  datasource:
    url: jdbc:mysql://mysql:3306/ecommerce?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
jwt:
  secret: your-production-secret-key
EOF
```

#### 2. 修改 Dockerfile
```dockerfile
# 创建配置目录
RUN mkdir -p /app/config

# 启动时使用外部配置
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.config.location=file:/app/config/"]
```

#### 3. 修改 docker-compose.yml
```yaml
services:
  backend:
    volumes:
      - ./config:/app/config  # 挂载配置文件目录
      - uploads_data:/app/uploads
```

### 方式二：使用环境变量（推荐）

在 docker-compose.yml 中通过环境变量配置，无需外部文件：

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/ecommerce?...
  SPRING_DATASOURCE_PASSWORD: your_password
  JWT_SECRET: your-secret-key
```

## 配置文件示例

### 外部 application-prod.yml
```yaml
# /app/config/application-prod.yml
server:
  port: 8080

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://mysql:3306/ecommerce?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: ${DB_PASSWORD:root}  # 仍可使用环境变量

jwt:
  secret: ${JWT_SECRET:default-secret-key}
  expiration: 86400000

file:
  upload:
    path: /app/uploads
    url: http://localhost:8080/uploads/

logging:
  level:
    root: INFO
    com.golden: INFO
```

## 配置优先级（完整列表）

从高到低：

1. **命令行参数** (`--server.port=9090`)
2. **系统属性** (`-Dserver.port=9090`)
3. **环境变量** (`SPRING_DATASOURCE_URL=...`)
4. **外部配置文件** (`/app/config/application.yml`)
5. **JAR 内部的 Profile 配置** (`application-prod.yml`)
6. **JAR 内部的通用配置** (`application.yml`)
7. **默认值**

## 实际应用场景

### 场景 1：生产环境不同服务器配置不同

```bash
# 服务器 A
/app/config/application-prod.yml  # 数据库指向 db-server-a

# 服务器 B
/app/config/application-prod.yml  # 数据库指向 db-server-b
```

### 场景 2：敏感信息不打包进 JAR

```bash
# JAR 内部（提交到 Git）
application-prod.yml:
  jwt:
    secret: ${JWT_SECRET:default}  # 使用环境变量

# 外部文件（不提交到 Git）
/app/config/application-prod.yml:
  jwt:
    secret: actual-production-secret-key  # 实际密钥
```

### 场景 3：快速切换配置

```bash
# 切换到测试配置
cp config/application-test.yml config/application-prod.yml
java -jar app.jar

# 切换回生产配置
cp config/application-prod-backup.yml config/application-prod.yml
java -jar app.jar
```

## 验证外部配置是否生效

### 方法 1：查看启动日志
```bash
# 启动时会显示加载的配置文件
Loaded config file 'file:/app/config/application-prod.yml'
```

### 方法 2：使用 Actuator（如果已添加）
```bash
GET /actuator/configprops
GET /actuator/env
```

### 方法 3：在代码中打印
```java
@Value("${spring.datasource.url}")
private String dbUrl;

@PostConstruct
public void printConfig() {
    logger.info("Database URL: {}", dbUrl);
}
```

## 注意事项

1. **配置文件路径**：使用绝对路径或相对于 JAR 文件的路径
2. **文件权限**：确保应用有读取配置文件的权限
3. **配置文件格式**：支持 `.yml` 和 `.properties` 格式
4. **Profile 配置**：外部配置文件也支持 Profile，如 `application-prod.yml`
5. **环境变量优先级**：环境变量仍然优先于外部配置文件

## 最佳实践

1. **开发环境**：使用 JAR 内部的配置文件
2. **生产环境**：使用外部配置文件，便于修改而不重新打包
3. **敏感信息**：通过环境变量传递，而不是写在配置文件中
4. **配置管理**：使用配置中心（如 Spring Cloud Config）管理多环境配置

## 示例：完整的 Docker 部署

### 1. 目录结构
```
/app/
├── ecommerce-backend-1.0.0.jar
├── config/
│   └── application-prod.yml
└── logs/
```

### 2. Dockerfile
```dockerfile
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY ecommerce-backend-1.0.0.jar app.jar
RUN mkdir -p /app/config /app/logs
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.config.location=file:/app/config/"]
```

### 3. docker-compose.yml
```yaml
services:
  backend:
    volumes:
      - ./config:/app/config  # 挂载外部配置
      - ./logs:/app/logs      # 挂载日志目录
```

这样配置后，修改 `/app/config/application-prod.yml` 文件，重启容器即可生效，无需重新构建镜像。
