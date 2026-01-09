# Spring Boot 配置优先级说明

## 配置优先级（从高到低）

Spring Boot 配置的优先级从**高到低**如下：

### 1. 命令行参数（最高优先级）
```bash
java -jar app.jar --server.port=9090 --spring.profiles.active=prod
```
- 通过 `--` 传递的参数
- **优先级最高**，会覆盖所有其他配置

### 2. 系统属性（System Properties）
```bash
java -Dserver.port=9090 -Dspring.profiles.active=prod -jar app.jar
```
- 通过 `-D` 传递的系统属性
- 优先级仅次于命令行参数

### 3. 环境变量（Environment Variables）
```bash
export SERVER_PORT=9090
export SPRING_PROFILES_ACTIVE=prod
java -jar app.jar
```
- 操作系统环境变量
- 在 Docker 中常用：`SPRING_DATASOURCE_URL=...`

### 4. 配置文件中的 Profile 特定配置
```
application-{profile}.yml
application-{profile}.properties
```
- 例如：`application-prod.yml`、`application-dev.yml`
- 优先级高于通用配置文件

### 5. 通用配置文件
```
application.yml
application.properties
```
- 基础配置文件
- 优先级低于 Profile 特定配置

### 6. @PropertySource 注解
```java
@PropertySource("classpath:custom.properties")
```

### 7. 默认配置（最低优先级）
- Spring Boot 的默认值
- 例如：`server.port` 默认为 `8080`

## 配置加载顺序示例

假设有以下配置：

### application.yml
```yaml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db
```

### application-prod.yml
```yaml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:mysql://mysql:3306/db
```

### 环境变量
```bash
SPRING_DATASOURCE_URL=jdbc:mysql://prod-db:3306/db
```

### 命令行参数
```bash
--server.port=9090
```

**最终生效的配置：**
- `server.port` = `9090`（命令行参数，最高优先级）
- `spring.datasource.url` = `jdbc:mysql://prod-db:3306/db`（环境变量，覆盖了配置文件）

## 配置文件内部优先级

在同一个配置文件中，也有优先级：

### 1. Profile 特定配置（application-{profile}.yml）
```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://mysql:3306/db
```

### 2. 通用配置（application.yml）
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db
```

**结果：** `application-prod.yml` 中的配置会覆盖 `application.yml` 中的相同配置。

## 环境变量命名规则

Spring Boot 将环境变量转换为配置属性：

### 规则
1. **大写字母**
2. **下划线分隔**
3. **点号转换为下划线**

### 示例

| 配置文件格式 | 环境变量格式 |
|------------|------------|
| `server.port` | `SERVER_PORT` |
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` |
| `spring.profiles.active` | `SPRING_PROFILES_ACTIVE` |
| `jwt.secret` | `JWT_SECRET` |
| `file.upload.path` | `FILE_UPLOAD_PATH` |

## 实际应用场景

### 场景 1：Docker 环境
```yaml
# docker-compose.yml
environment:
  SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/ecommerce
  JWT_SECRET: production-secret-key
```

这些环境变量会覆盖 `application-prod.yml` 中的配置。

### 场景 2：本地开发
```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ecommerce
```

直接使用配置文件，不需要环境变量。

### 场景 3：生产环境敏感信息
```bash
# 通过环境变量传递敏感信息
export SPRING_DATASOURCE_PASSWORD=secure_password
export JWT_SECRET=very_secure_secret_key
```

不在配置文件中硬编码密码。

## 配置覆盖示例

### 示例 1：数据库 URL

**application.yml**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ecommerce
```

**application-prod.yml**
```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:mysql://mysql:3306/ecommerce}
```

**环境变量**
```bash
SPRING_DATASOURCE_URL=jdbc:mysql://prod-server:3306/ecommerce
```

**最终生效：** `jdbc:mysql://prod-server:3306/ecommerce`（环境变量）

### 示例 2：端口配置

**application.yml**
```yaml
server:
  port: 8080
```

**命令行参数**
```bash
--server.port=9090
```

**最终生效：** `9090`（命令行参数）

## 最佳实践

### 1. 使用环境变量覆盖敏感配置
```yaml
# application-prod.yml
spring:
  datasource:
    password: ${SPRING_DATASOURCE_PASSWORD:default_password}
```

### 2. 提供合理的默认值
```yaml
# 使用 ${VAR:default_value} 语法
url: ${SPRING_DATASOURCE_URL:jdbc:mysql://mysql:3306/ecommerce}
```

### 3. 分层配置
- **application.yml**: 公共配置
- **application-dev.yml**: 开发环境特定配置
- **application-prod.yml**: 生产环境特定配置（使用环境变量）

### 4. 避免硬编码敏感信息
```yaml
# ❌ 不好
jwt:
  secret: my-secret-key

# ✅ 好
jwt:
  secret: ${JWT_SECRET:default-secret-key}
```

## 验证配置优先级

### 方法 1：查看启动日志
```bash
# 启动时会显示激活的配置
The following profiles are active: prod
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
    System.out.println("Database URL: " + dbUrl);
}
```

## 总结

**优先级顺序（从高到低）：**
1. 命令行参数
2. 系统属性（-D）
3. 环境变量
4. Profile 特定配置文件（application-{profile}.yml）
5. 通用配置文件（application.yml）
6. @PropertySource
7. 默认值

**记住：** 高优先级的配置会覆盖低优先级的配置！
