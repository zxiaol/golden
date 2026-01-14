# Docker 外部配置文件使用指南

## 概述

Dockerfile 已配置支持外部配置文件，生产环境可以通过挂载配置文件的方式灵活修改配置，无需重新构建镜像。

## 目录结构

```
/app/
├── app.jar                    # 应用 JAR 文件
├── config/                    # 配置目录（可外部挂载）
│   └── application-prod.yml   # 生产环境配置文件
├── uploads/                   # 上传文件目录
└── logs/                      # 日志目录
```

## 使用方式

### 方式一：使用镜像内的默认配置（当前方式）

直接运行容器，使用镜像内已包含的 `application-prod.yml`：

```bash
docker run -d \
  --name ecommerce-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  ecommerce-backend:latest
```

### 方式二：挂载外部配置文件（推荐生产环境）

将配置文件挂载到容器的 `/app/config/` 目录：

```bash
docker run -d \
  --name ecommerce-backend \
  -p 8080:8080 \
  -v /path/to/your/config:/app/config \
  -e SPRING_PROFILES_ACTIVE=prod \
  ecommerce-backend:latest
```

或者使用 Docker Compose：

```yaml
services:
  backend:
    image: ecommerce-backend:latest
    volumes:
      - ./config:/app/config  # 挂载外部配置目录
    environment:
      SPRING_PROFILES_ACTIVE: prod
```

### 方式三：挂载单个配置文件

只挂载 `application-prod.yml` 文件：

```bash
docker run -d \
  --name ecommerce-backend \
  -p 8080:8080 \
  -v /path/to/application-prod.yml:/app/config/application-prod.yml \
  -e SPRING_PROFILES_ACTIVE=prod \
  ecommerce-backend:latest
```

## 配置优先级

Spring Boot 配置加载顺序（从高到低）：

1. **命令行参数**：`-Dspring.datasource.url=...`
2. **环境变量**：`SPRING_DATASOURCE_URL=...`
3. **外部配置文件**：`/app/config/application-prod.yml`（如果存在）
4. **JAR 内配置文件**：`application-prod.yml`（JAR 内）

## 配置文件位置

### 构建时复制

Dockerfile 会在构建时将 `src/main/resources/application-prod.yml` 复制到镜像的 `/app/config/` 目录。

### 运行时挂载

生产环境建议将配置文件放在宿主机，通过 volume 挂载：

```bash
# 宿主机目录结构
/path/to/config/
└── application-prod.yml
```

## Docker Compose 示例

### 完整示例

```yaml
version: '3.8'

services:
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: ecommerce-backend
    ports:
      - "8080:8080"
    volumes:
      # 挂载外部配置文件（推荐）
      - ./config:/app/config
      # 或只挂载单个文件
      # - ./config/application-prod.yml:/app/config/application-prod.yml
      # 持久化上传文件
      - uploads_data:/app/uploads
      # 持久化日志（可选）
      - ./logs:/app/logs
    environment:
      SPRING_PROFILES_ACTIVE: prod
      # 可以通过环境变量覆盖配置
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/ecommerce?...
      SPRING_DATASOURCE_USERNAME: ecommerce
      SPRING_DATASOURCE_PASSWORD: ecommerce
    depends_on:
      - mysql
    networks:
      - ecommerce-network

volumes:
  uploads_data:
    driver: local

networks:
  ecommerce-network:
    driver: bridge
```

## 修改配置

### 方法一：修改外部配置文件（推荐）

1. 修改宿主机上的配置文件：
   ```bash
   vi /path/to/config/application-prod.yml
   ```

2. 重启容器：
   ```bash
   docker restart ecommerce-backend
   ```

### 方法二：进入容器修改

```bash
# 进入容器
docker exec -it ecommerce-backend sh

# 修改配置文件
vi /app/config/application-prod.yml

# 重启应用（或重启容器）
```

### 方法三：使用环境变量覆盖

```bash
docker run -d \
  --name ecommerce-backend \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://new-host:3306/ecommerce \
  -e SPRING_DATASOURCE_USERNAME=new_user \
  -e SPRING_DATASOURCE_PASSWORD=new_password \
  ecommerce-backend:latest
```

## 验证配置

### 检查配置文件是否存在

```bash
docker exec ecommerce-backend ls -la /app/config/
```

应该看到：
```
application-prod.yml
```

### 查看应用启动日志

```bash
docker logs ecommerce-backend | grep -i "config"
```

### 检查实际使用的配置

应用启动后，可以通过日志或 Actuator 端点查看实际使用的配置（如果启用了 Actuator）。

## 注意事项

1. **配置文件格式**：确保 YAML 文件格式正确，缩进使用空格（不是 Tab）
2. **文件权限**：确保挂载的配置文件有读取权限
3. **配置优先级**：环境变量的优先级高于配置文件
4. **敏感信息**：生产环境的敏感信息（如密码）建议使用环境变量或密钥管理服务
5. **配置文件备份**：修改配置文件前建议先备份

## 最佳实践

1. **开发环境**：使用 JAR 内的默认配置
2. **生产环境**：使用外部挂载的配置文件，便于修改和维护
3. **敏感配置**：使用环境变量或密钥管理服务（如 Vault、AWS Secrets Manager）
4. **配置版本控制**：将配置文件纳入版本控制，但排除敏感信息
5. **配置验证**：修改配置后，先验证配置格式，再重启应用

## 故障排查

### 配置文件未生效

1. 检查文件是否存在：
   ```bash
   docker exec ecommerce-backend ls -la /app/config/
   ```

2. 检查文件内容：
   ```bash
   docker exec ecommerce-backend cat /app/config/application-prod.yml
   ```

3. 检查挂载是否正确：
   ```bash
   docker inspect ecommerce-backend | grep -A 10 Mounts
   ```

### 配置格式错误

检查 YAML 格式：
```bash
# 使用 yamllint 或在线工具验证 YAML 格式
yamllint application-prod.yml
```

### 配置优先级问题

记住配置优先级：
- 环境变量 > 外部配置文件 > JAR 内配置文件

如果环境变量设置了某个配置项，配置文件中的相同配置项会被覆盖。
