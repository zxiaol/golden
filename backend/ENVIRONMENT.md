# 环境配置说明

## 概述

项目支持多环境配置，通过 Spring Boot 的 Profile 机制实现。目前支持两个环境：
- **dev**：开发环境
- **prod**：生产环境

## 配置文件结构

```
src/main/resources/
├── application.yml          # 公共配置
├── application-dev.yml     # 开发环境配置
└── application-prod.yml    # 生产环境配置
```

## 环境切换方式

### 方式一：通过环境变量（推荐）

#### 开发环境
```bash
export SPRING_PROFILES_ACTIVE=dev
java -jar ecommerce-backend-1.0.0.jar
```

#### 生产环境
```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar ecommerce-backend-1.0.0.jar
```

### 方式二：通过启动参数

#### 开发环境
```bash
java -jar ecommerce-backend-1.0.0.jar --spring.profiles.active=dev
```

#### 生产环境
```bash
java -jar ecommerce-backend-1.0.0.jar --spring.profiles.active=prod
```

### 方式三：在 IDE 中配置

#### IntelliJ IDEA
1. Run → Edit Configurations
2. 在 "VM options" 或 "Program arguments" 中添加：
   ```
   --spring.profiles.active=dev
   ```

#### Eclipse
1. Run → Run Configurations
2. 在 "Arguments" 标签页的 "Program arguments" 中添加：
   ```
   --spring.profiles.active=dev
   ```

### 方式四：Docker 环境

#### 使用 docker-compose
```yaml
environment:
  SPRING_PROFILES_ACTIVE: prod  # 或 dev
```

#### 使用 docker run
```bash
docker run -e SPRING_PROFILES_ACTIVE=prod ecommerce-backend:1.0.0
```

## 环境配置差异

### 开发环境 (dev)

| 配置项 | 值 | 说明 |
|--------|-----|------|
| 数据库 URL | `localhost:3306` | 本地数据库 |
| 日志级别 | DEBUG | 详细日志 |
| MyBatis SQL | 显示 | 控制台输出 SQL |
| JWT Secret | `golden-ecommerce-secret-key-2024-dev` | 开发密钥 |
| 文件上传路径 | `./uploads` | 相对路径 |

### 生产环境 (prod)

| 配置项 | 值 | 说明 |
|--------|-----|------|
| 数据库 URL | 环境变量或 `mysql:3306` | 容器内数据库 |
| 日志级别 | INFO | 标准日志 |
| MyBatis SQL | 不显示 | 不输出 SQL |
| JWT Secret | 环境变量或默认值 | 生产密钥（建议使用环境变量） |
| 文件上传路径 | `/app/uploads` | 绝对路径 |

## 环境变量配置

生产环境建议使用环境变量覆盖敏感配置：

### 数据库配置
```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/ecommerce?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai
export SPRING_DATASOURCE_USERNAME=your_username
export SPRING_DATASOURCE_PASSWORD=your_password
```

### JWT 配置
```bash
export JWT_SECRET=your-strong-secret-key-here
export JWT_EXPIRATION=86400000
```

### 文件上传配置
```bash
export FILE_UPLOAD_PATH=/app/uploads
export FILE_UPLOAD_URL=https://yourdomain.com/uploads/
```

## Docker Compose 配置示例

### 开发环境
```yaml
services:
  backend:
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/ecommerce?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
```

### 生产环境
```yaml
services:
  backend:
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: ${DB_URL}
      SPRING_DATASOURCE_USERNAME: ${DB_USERNAME}
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      FILE_UPLOAD_URL: ${FILE_UPLOAD_URL}
```

## 验证当前环境

### 方式一：查看启动日志
启动时会显示当前激活的环境：
```
The following profiles are active: dev
```

### 方式二：通过 API 查看
可以添加一个健康检查接口查看当前环境（需要实现）。

### 方式三：查看日志文件
- 开发环境：`logs/application-dev.log`
- 生产环境：`logs/application-prod.log`

## 默认环境

- **application.yml** 中设置默认环境为 `dev`
- 可以通过环境变量 `SPRING_PROFILES_ACTIVE` 覆盖
- Docker 容器中默认使用 `prod` 环境

## 最佳实践

1. **开发环境**
   - 使用本地数据库
   - 启用 DEBUG 日志
   - 显示 SQL 语句
   - 使用相对路径

2. **生产环境**
   - 使用环境变量配置敏感信息
   - 使用 INFO 级别日志
   - 隐藏 SQL 语句
   - 使用绝对路径
   - 启用 SSL 连接数据库

3. **安全建议**
   - 生产环境 JWT Secret 必须使用强密钥
   - 数据库密码不要硬编码在配置文件中
   - 使用环境变量或密钥管理服务

## 常见问题

### Q: 如何知道当前使用的是哪个环境？
A: 查看启动日志，会显示 "The following profiles are active: xxx"

### Q: 环境变量不生效？
A: 确保环境变量名称正确，Spring Boot 使用 `${VAR_NAME}` 格式

### Q: Docker 中如何切换环境？
A: 在 docker-compose.yml 或 docker run 命令中设置 `SPRING_PROFILES_ACTIVE` 环境变量

### Q: 可以同时激活多个环境吗？
A: 可以，使用逗号分隔：`--spring.profiles.active=dev,test`

