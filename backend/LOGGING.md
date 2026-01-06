# 日志配置说明

## 概述

项目使用 Logback 作为日志框架，支持多级别日志输出（INFO、WARN、ERROR），并按照不同级别分别存储到不同的日志文件中。

## 日志级别

### INFO（信息）
- 用于记录程序正常运行的关键信息
- 如：用户登录、注册、商品操作等业务日志
- 存储位置：`logs/info.log`

### WARN（警告）
- 用于记录可能存在问题但不影响程序运行的情况
- 如：登录失败、参数验证失败等
- 存储位置：`logs/warn.log`

### ERROR（错误）
- 用于记录程序运行时的错误信息
- 如：异常堆栈、系统错误等
- 存储位置：`logs/error.log`

### DEBUG（调试）
- 用于记录详细的调试信息
- 仅在开发环境启用
- 存储位置：`logs/all.log`

## 日志文件结构

```
logs/
├── info.log          # INFO 级别日志（按天滚动）
├── warn.log          # WARN 级别日志（按天滚动）
├── error.log         # ERROR 级别日志（按天滚动）
├── all.log           # 所有级别日志（按天滚动）
└── application.log  # 应用日志（Spring Boot 默认）
```

## 日志配置

### logback-spring.xml

主要配置文件，包含：
- 控制台输出配置
- 文件输出配置（按级别分类）
- 日志滚动策略（按天滚动，保留历史）
- 异步输出配置（提高性能）

### application.yml

日志基础配置：
```yaml
logging:
  level:
    root: INFO
    com.golden: INFO
  file:
    name: ./logs/application.log
```

## 使用示例

### 在代码中使用日志

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YourClass {
    private static final Logger logger = LoggerFactory.getLogger(YourClass.class);
    
    public void someMethod() {
        // INFO 级别
        logger.info("用户操作: userId={}, action={}", userId, action);
        
        // WARN 级别
        logger.warn("警告信息: {}", warningMessage);
        
        // ERROR 级别
        logger.error("错误信息: {}", errorMessage, exception);
        
        // DEBUG 级别（开发环境）
        logger.debug("调试信息: {}", debugInfo);
    }
}
```

## 日志格式

```
2024-01-15 10:30:45.123 [http-nio-8080-exec-1] INFO  com.golden.controller.AuthController - 用户登录请求: username=testuser
```

格式说明：
- `2024-01-15 10:30:45.123` - 时间戳（精确到毫秒）
- `[http-nio-8080-exec-1]` - 线程名
- `INFO` - 日志级别
- `com.golden.controller.AuthController` - 类名
- `用户登录请求: username=testuser` - 日志消息

## 日志滚动策略

- **按天滚动**：每天生成新的日志文件
- **保留历史**：
  - INFO: 保留 30 天
  - WARN: 保留 30 天
  - ERROR: 保留 90 天
  - ALL: 保留 7 天
- **文件大小限制**：
  - INFO: 最大 1GB
  - WARN: 最大 500MB
  - ERROR: 最大 1GB
  - ALL: 最大 2GB

## 环境配置

### 开发环境（dev）
- 控制台输出：启用
- 文件输出：启用
- 日志级别：DEBUG（com.golden 包）

### 生产环境（prod）
- 控制台输出：禁用
- 文件输出：启用
- 日志级别：INFO

### 切换环境

在 `application.yml` 中设置：
```yaml
spring:
  profiles:
    active: dev  # 或 prod
```

或在启动参数中设置：
```bash
java -jar app.jar --spring.profiles.active=prod
```

## Docker 环境

在 Docker 容器中，日志文件会保存在容器内的 `./logs` 目录。

如果需要持久化日志，可以在 `docker-compose.yml` 中添加卷映射：

```yaml
volumes:
  - ./logs:/app/logs
```

## 查看日志

### 实时查看日志

```bash
# 查看所有日志
tail -f logs/all.log

# 查看错误日志
tail -f logs/error.log

# 查看警告日志
tail -f logs/warn.log

# 查看信息日志
tail -f logs/info.log
```

### Docker 容器中查看

```bash
# 查看容器日志
docker logs -f ecommerce-backend

# 进入容器查看文件日志
docker exec -it ecommerce-backend sh
tail -f /app/logs/info.log
```

## 最佳实践

1. **合理使用日志级别**
   - INFO: 记录业务流程关键节点
   - WARN: 记录异常但不影响运行的情况
   - ERROR: 记录所有错误和异常
   - DEBUG: 仅在开发调试时使用

2. **使用参数化日志**
   ```java
   // 推荐：使用参数化
   logger.info("用户登录: username={}, userId={}", username, userId);
   
   // 不推荐：字符串拼接
   logger.info("用户登录: username=" + username + ", userId=" + userId);
   ```

3. **异常日志包含堆栈**
   ```java
   logger.error("处理失败", exception);  // 包含完整堆栈
   ```

4. **敏感信息不记录**
   - 不要记录密码、Token 等敏感信息
   - 可以记录用户ID、操作类型等非敏感信息

## 性能优化

- 使用异步输出（已在配置中启用）
- 生产环境关闭 DEBUG 级别
- 定期清理历史日志文件
- 使用日志滚动避免单文件过大

