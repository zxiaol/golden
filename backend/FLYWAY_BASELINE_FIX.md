# Flyway 基线问题修复指南

## 问题描述

错误信息：
```
Found non-empty schema(s) `ecommerce` but no schema history table. 
Use baseline() or set baselineOnMigrate to true to initialize the schema history table.
```

## 原因分析

1. 数据库已有表（通过 `schema.sql` 初始化）
2. Flyway 历史表 `flyway_schema_history` 不存在
3. 虽然配置了 `baseline-on-migrate: true`，但可能在验证阶段就失败了

## 解决方案

### 方案一：重启应用（推荐，已修复配置）

已更新配置：
- `baseline-on-migrate: true` ✅
- `baseline-version: 0` ✅
- `validate-on-migrate: false` ✅（临时禁用，让基线先创建）

重启应用后，Flyway 应该能够：
1. 检测到数据库已有表
2. 自动创建 `flyway_schema_history` 表
3. 创建基线记录
4. 执行后续迁移

```bash
docker-compose restart backend
```

### 方案二：手动创建基线（如果方案一失败）

#### 步骤 1：连接数据库

```bash
# 进入 MySQL 容器
docker exec -it ecommerce-mysql mysql -uroot -proot

# 或使用 ecommerce 用户
docker exec -it ecommerce-mysql mysql -uecommerce -pecommerce ecommerce
```

#### 步骤 2：执行基线 SQL

```sql
-- 创建 Flyway 历史表
CREATE TABLE IF NOT EXISTS flyway_schema_history (
    installed_rank INT NOT NULL,
    version VARCHAR(50),
    description VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL,
    script VARCHAR(1000) NOT NULL,
    checksum INT,
    installed_by VARCHAR(100) NOT NULL,
    installed_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    execution_time INT NOT NULL,
    success BOOLEAN NOT NULL,
    PRIMARY KEY (installed_rank),
    INDEX idx_flyway_schema_history_version (version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入基线记录
INSERT INTO flyway_schema_history 
(installed_rank, version, description, type, script, checksum, installed_by, execution_time, success)
VALUES 
(1, '0', '<< Flyway Baseline >>', 'BASELINE', '<< Flyway Baseline >>', NULL, USER(), 0, 1);
```

#### 步骤 3：验证基线

```sql
-- 查看基线记录
SELECT * FROM flyway_schema_history;
```

#### 步骤 4：重新启用验证

手动创建基线后，可以重新启用验证：

```yaml
flyway:
  validate-on-migrate: true  # 重新启用验证
```

### 方案三：使用 Flyway Maven 命令创建基线

```bash
# 进入 backend 目录
cd backend

# 创建基线（使用 Maven 插件）
mvn flyway:baseline \
  -Dflyway.url=jdbc:mysql://mysql:3306/ecommerce?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai \
  -Dflyway.user=ecommerce \
  -Dflyway.password=ecommerce \
  -Dflyway.baselineVersion=0
```

## 验证修复

### 1. 检查历史表

```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

应该看到一条基线记录：
- `version`: `0`
- `description`: `<< Flyway Baseline >>`
- `type`: `BASELINE`

### 2. 检查应用日志

```bash
docker logs ecommerce-backend | grep -i flyway
```

应该看到：
```
INFO  o.flyway.core.internal.command.DbBaseline - Creating schema history table
INFO  o.flyway.core.internal.command.DbBaseline - Schema baselined with version: 0
```

### 3. 重启应用

```bash
docker-compose restart backend
```

应用应该能够正常启动。

## 配置说明

### 当前配置（已修复）

```yaml
flyway:
  enabled: true
  locations: classpath:db/migration
  baseline-on-migrate: true      # 自动创建基线
  baseline-version: 0             # 基线版本号
  validate-on-migrate: false    # 临时禁用验证
  clean-disabled: true
```

### 基线创建后的配置（恢复正常）

```yaml
flyway:
  enabled: true
  locations: classpath:db/migration
  baseline-on-migrate: true
  baseline-version: 0
  validate-on-migrate: true     # 重新启用验证
  clean-disabled: true
```

## 常见问题

### Q: 为什么 baseline-on-migrate 没有生效？

A: 可能的原因：
1. 配置没有正确加载
2. 数据库用户权限不足
3. 验证阶段在基线创建之前失败

### Q: 手动创建基线后，还需要做什么？

A: 
1. 验证基线记录存在
2. 重新启用 `validate-on-migrate: true`
3. 重启应用

### Q: 基线版本号应该设置多少？

A: 通常设置为 `0`，表示基线版本。后续迁移脚本会从 `V1` 开始。

## 预防措施

1. **新环境**：使用 Flyway 迁移脚本初始化，而不是 `schema.sql`
2. **现有环境**：先创建基线，再启用 Flyway
3. **权限检查**：确保数据库用户有创建表的权限
