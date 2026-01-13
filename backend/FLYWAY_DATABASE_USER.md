# Flyway 独立数据库用户配置指南

## 概述

在生产环境中，为了安全性和权限管理，通常需要为 Flyway 数据库迁移配置独立的数据库用户，而不是使用应用运行时的数据库用户。

## 配置方式

### 方式一：使用环境变量（推荐）

在 `application-prod.yml` 中，Flyway 配置支持独立的数据库连接：

```yaml
flyway:
  enabled: true
  locations: classpath:db/migration
  baseline-on-migrate: true
  validate-on-migrate: true
  clean-disabled: true
  # Flyway 独立数据库配置
  url: ${FLYWAY_URL:}      # 如果为空，使用 spring.datasource.url
  user: ${FLYWAY_USER:}    # 如果为空，使用 spring.datasource.username
  password: ${FLYWAY_PASSWORD:}  # 如果为空，使用 spring.datasource.password
```

#### 使用场景

**场景 1：Flyway 使用默认配置（与应用相同）**
```bash
# 不设置 Flyway 环境变量，Flyway 会使用 spring.datasource 的配置
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_USERNAME=app_user
export SPRING_DATASOURCE_PASSWORD=app_password
```

**场景 2：Flyway 使用独立用户（推荐生产环境）**
```bash
# 应用使用普通用户
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_USERNAME=app_user
export SPRING_DATASOURCE_PASSWORD=app_password

# Flyway 使用迁移专用用户（具有 DDL 权限）
export FLYWAY_URL=jdbc:mysql://mysql:3306/ecommerce?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai
export FLYWAY_USER=migration_user
export FLYWAY_PASSWORD=migration_password
```

### 方式二：直接在配置文件中配置

如果不想使用环境变量，可以直接在 `application-prod.yml` 中配置：

```yaml
flyway:
  enabled: true
  locations: classpath:db/migration
  baseline-on-migrate: true
  validate-on-migrate: true
  clean-disabled: true
  url: jdbc:mysql://mysql:3306/ecommerce?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai
  user: migration_user
  password: ${FLYWAY_PASSWORD:migration_password}  # 密码建议使用环境变量
```

## Docker Compose 配置示例

在 `docker-compose.yml` 中配置环境变量：

```yaml
services:
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    environment:
      SPRING_PROFILES_ACTIVE: prod
      # 应用数据库配置
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/ecommerce?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai
      SPRING_DATASOURCE_USERNAME: app_user
      SPRING_DATASOURCE_PASSWORD: app_password
      # Flyway 独立数据库配置（可选）
      FLYWAY_URL: jdbc:mysql://mysql:3306/ecommerce?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai
      FLYWAY_USER: migration_user
      FLYWAY_PASSWORD: migration_password
```

## 数据库用户权限建议

### 应用用户（app_user）
```sql
-- 创建应用用户（只读和 DML 权限）
CREATE USER 'app_user'@'%' IDENTIFIED BY 'app_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON ecommerce.* TO 'app_user'@'%';
FLUSH PRIVILEGES;
```

### 迁移用户（migration_user）
```sql
-- 创建迁移用户（需要 DDL 权限）
CREATE USER 'migration_user'@'%' IDENTIFIED BY 'migration_password';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, ALTER, INDEX ON ecommerce.* TO 'migration_user'@'%';
FLUSH PRIVILEGES;
```

## 配置优先级

Flyway 配置的优先级（从高到低）：

1. **Flyway 独立配置** (`flyway.url`, `flyway.user`, `flyway.password`)
2. **Spring DataSource 配置** (`spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`)

如果配置了 Flyway 的独立配置，Flyway 会优先使用这些配置；如果未配置（为空），则使用 Spring DataSource 的配置。

## 验证配置

启动应用后，查看日志确认 Flyway 使用的数据库连接：

```bash
# 查看 Flyway 日志
grep -i flyway logs/application-prod.log

# 或者查看应用启动日志
# 应该能看到类似信息：
# Flyway Community Edition 9.x.x by Red Hat
# Database: jdbc:mysql://mysql:3306/ecommerce (MySQL 8.0)
```

## 安全建议

1. **生产环境**：使用独立的迁移用户，限制应用用户的权限
2. **密码管理**：使用环境变量或密钥管理服务（如 Vault、AWS Secrets Manager）
3. **最小权限原则**：迁移用户只授予必要的 DDL 权限
4. **审计日志**：记录迁移操作的执行者和时间

## Maven 插件配置（pom.xml）

`pom.xml` 中的 Flyway Maven 插件配置用于**手动执行迁移**（通过 `mvn flyway:migrate` 命令），与 Spring Boot 应用启动时的自动迁移是**分开的**。

### 配置说明

在 `pom.xml` 中，Flyway Maven 插件支持以下配置方式（优先级从高到低）：

1. **命令行参数**（最高优先级）
   ```bash
   mvn flyway:migrate -Dflyway.url=jdbc:mysql://... -Dflyway.user=migration_user -Dflyway.password=password
   ```

2. **Maven 属性**
   ```bash
   mvn flyway:migrate -Dflyway.user=migration_user -Dflyway.password=password
   ```

3. **配置文件**（`flyway.conf`）
   ```properties
   flyway.url=jdbc:mysql://...
   flyway.user=migration_user
   flyway.password=password
   ```

4. **默认值**（pom.xml 中的配置）

### 使用不同数据库用户

**方式一：通过命令行参数**
```bash
# 使用迁移用户执行迁移
mvn flyway:migrate \
  -Dflyway.url=jdbc:mysql://10.128.244.10:3306/ecommerce?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai \
  -Dflyway.user=migration_user \
  -Dflyway.password=migration_password
```

**方式二：更新 flyway.conf 文件**
```properties
flyway.url=jdbc:mysql://10.128.244.10:3306/ecommerce?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
flyway.user=migration_user
flyway.password=migration_password
```

**方式三：使用 Maven 属性文件**
在项目根目录创建 `.mvn/maven.config` 或使用 `-D` 参数传递。

### 两种使用场景的区别

| 场景 | 配置位置 | 执行时机 | 用途 |
|------|---------|---------|------|
| **Spring Boot 自动迁移** | `application-*.yml` | 应用启动时 | 生产环境自动执行 |
| **Maven 插件手动迁移** | `pom.xml` + `flyway.conf` | 手动执行 `mvn flyway:migrate` | CI/CD、开发环境手动执行 |

## 常见问题

### Q: 如果 Flyway 配置为空，会使用什么？
A: 如果 `flyway.url`、`flyway.user`、`flyway.password` 为空或未配置，Flyway 会自动使用 `spring.datasource` 的配置。

### Q: 可以在开发环境也使用独立用户吗？
A: 可以，在 `application-dev.yml` 中同样配置即可。但开发环境通常使用同一个用户更方便。

### Q: 如何禁用 Flyway 的独立配置？
A: 不设置 `FLYWAY_URL`、`FLYWAY_USER`、`FLYWAY_PASSWORD` 环境变量，或者在配置文件中不配置这些属性即可。

### Q: Maven 插件的配置和 Spring Boot 的配置需要一致吗？
A: 不需要。它们是独立的配置：
- Maven 插件：用于手动执行迁移，通常在 CI/CD 或开发时使用
- Spring Boot：应用启动时自动执行迁移，用于生产环境

### Q: 如何为 Maven 插件配置不同的数据库用户？
A: 可以通过以下方式：
1. 命令行参数：`mvn flyway:migrate -Dflyway.user=...`
2. 更新 `flyway.conf` 文件
3. 使用 Maven 属性文件

## 相关文档

- [Flyway 官方文档](https://flywaydb.org/documentation/)
- [Spring Boot Flyway 集成](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)
