# 数据库迁移指南

## 概述

项目使用 **Flyway** 进行数据库版本管理和迁移。当 Entity 或 Schema 发生变化时，通过创建 Flyway 迁移脚本来同步数据库结构。

## Flyway 工作原理

1. **版本控制**：每个迁移脚本都有版本号（V1, V2, V3...）
2. **自动执行**：应用启动时自动检测并执行未运行的迁移脚本
3. **版本追踪**：在数据库中创建 `flyway_schema_history` 表记录已执行的迁移
4. **不可修改**：已执行的迁移脚本不能修改，只能创建新的迁移脚本

## 迁移脚本命名规则

```
V{version}__{description}.sql
```

**示例：**
- `V1__Initial_schema.sql` - 初始数据库结构
- `V2__Add_user_phone_index.sql` - 添加用户手机号索引
- `V3__Add_product_tags.sql` - 添加商品标签字段

**规则：**
- `V` 必须大写
- 版本号必须是数字（可以带下划线，如 `V1_1`）
- 两个下划线 `__` 分隔版本号和描述
- 描述使用下划线分隔单词
- 文件扩展名必须是 `.sql`

## 使用流程

### 1. Entity 或 Schema 变更

当需要修改数据库结构时：

#### 示例：添加新字段
```java
// User.java 添加新字段
private String nickname;
```

#### 示例：修改字段类型
```java
// Product.java 修改价格精度
private BigDecimal price; // 从 DECIMAL(10,2) 改为 DECIMAL(12,2)
```

### 2. 创建迁移脚本

在 `src/main/resources/db/migration/` 目录下创建新的迁移脚本：

```sql
-- V2__Add_user_nickname.sql
ALTER TABLE users ADD COLUMN nickname VARCHAR(50) AFTER username;
CREATE INDEX idx_nickname ON users(nickname);
```

### 3. 启动应用

应用启动时，Flyway 会自动：
1. 检查 `flyway_schema_history` 表
2. 发现新的迁移脚本（V2）
3. 按顺序执行未执行的迁移
4. 记录执行历史

### 4. 验证迁移

查看迁移历史：
```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

## 迁移脚本示例

### 示例 1：添加新字段

```sql
-- V2__Add_user_nickname.sql
ALTER TABLE users 
ADD COLUMN nickname VARCHAR(50) COMMENT '用户昵称' AFTER username;

CREATE INDEX idx_nickname ON users(nickname);
```

### 示例 2：修改字段类型

```sql
-- V3__Modify_product_price_precision.sql
ALTER TABLE products 
MODIFY COLUMN price DECIMAL(12, 2) NOT NULL COMMENT '商品价格';
```

### 示例 3：添加新表

```sql
-- V4__Create_user_addresses_table.sql
CREATE TABLE IF NOT EXISTS user_addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL COMMENT '收货人姓名',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    address TEXT NOT NULL COMMENT '详细地址',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认地址',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 示例 4：添加索引

```sql
-- V5__Add_product_name_index.sql
CREATE INDEX idx_product_name ON products(name);
```

### 示例 5：删除字段（谨慎操作）

```sql
-- V6__Remove_user_old_field.sql
-- 注意：删除字段前确保没有代码在使用该字段
ALTER TABLE users DROP COLUMN old_field;
```

## 配置说明

### application.yml

```yaml
flyway:
  enabled: true                    # 启用 Flyway
  locations: classpath:db/migration # 迁移脚本位置
  baseline-on-migrate: true        # 如果数据库已有表，自动创建基线
  validate-on-migrate: true        # 迁移前验证脚本
  clean-disabled: true             # 禁止在生产环境使用 clean
```

### 环境特定配置

#### 开发环境 (application-dev.yml)
```yaml
flyway:
  enabled: true
  clean-disabled: false  # 开发环境可以清理数据库
```

#### 生产环境 (application-prod.yml)
```yaml
flyway:
  enabled: true
  clean-disabled: true   # 生产环境禁止清理
  validate-on-migrate: true
```

## 常见操作

### 查看迁移状态

#### 方式一：查看数据库表
```sql
SELECT * FROM flyway_schema_history 
ORDER BY installed_rank DESC;
```

#### 方式二：查看启动日志
```
Flyway Community Edition 9.x.x by Red Hat
Database: jdbc:mysql://localhost:3306/ecommerce
Successfully validated 5 migrations
Current version of schema `ecommerce`: 5
```

### 手动执行迁移（Maven）

```bash
# 执行迁移
mvn flyway:migrate

# 查看迁移信息
mvn flyway:info

# 验证迁移脚本
mvn flyway:validate
```

### 回滚迁移

**注意：** Flyway 不支持自动回滚，需要手动创建回滚脚本。

#### 方式一：创建回滚迁移脚本
```sql
-- V6__Rollback_add_nickname.sql
ALTER TABLE users DROP COLUMN nickname;
DROP INDEX idx_nickname ON users;
```

#### 方式二：手动删除迁移记录（不推荐）
```sql
-- 危险操作，谨慎使用
DELETE FROM flyway_schema_history WHERE version = '2';
-- 然后手动回滚数据库变更
```

## 最佳实践

### 1. 版本号管理
- 使用递增的版本号：V1, V2, V3...
- 可以使用带下划线的版本号：V1_1, V1_2（用于补丁）

### 2. 脚本编写
- **可重复执行**：使用 `IF NOT EXISTS` 或 `IF EXISTS`
- **事务性**：每个脚本应该是一个完整的事务
- **可回滚**：考虑如何回滚变更

### 3. 测试迁移
- 在开发环境先测试迁移脚本
- 确保迁移脚本可以重复执行
- 验证迁移后的数据完整性

### 4. 生产环境
- **备份数据库**：迁移前备份
- **测试环境验证**：先在测试环境验证
- **逐步发布**：大变更分多个小迁移脚本
- **监控执行**：关注迁移执行日志

### 5. 团队协作
- 迁移脚本提交到 Git
- 版本号不要冲突（团队内协调）
- 迁移脚本描述清晰

## 常见问题

### Q: 迁移脚本执行失败怎么办？

A: 
1. 查看错误日志
2. 修复脚本问题
3. 手动修复数据库（如果需要）
4. 删除失败的迁移记录：
   ```sql
   DELETE FROM flyway_schema_history WHERE success = 0;
   ```
5. 重新执行迁移

### Q: 如何跳过某个迁移？

A: 不推荐跳过，但可以：
1. 修改迁移脚本，使其不执行任何操作
2. 或手动在 `flyway_schema_history` 中插入记录

### Q: 迁移脚本可以修改吗？

A: **不可以**。已执行的迁移脚本不能修改，只能创建新的迁移脚本来修复。

### Q: 如何重置数据库？

A: 
```bash
# 开发环境可以使用
mvn flyway:clean  # 清理所有对象
mvn flyway:migrate # 重新执行所有迁移
```

**注意：** 生产环境禁止使用 `clean`！

## 迁移脚本模板

### 添加字段模板
```sql
-- V{version}__Add_{table}_{field}.sql
ALTER TABLE {table_name} 
ADD COLUMN {field_name} {field_type} {constraints} 
AFTER {existing_field};
```

### 修改字段模板
```sql
-- V{version}__Modify_{table}_{field}.sql
ALTER TABLE {table_name} 
MODIFY COLUMN {field_name} {new_type} {constraints};
```

### 添加索引模板
```sql
-- V{version}__Add_{table}_{field}_index.sql
CREATE INDEX idx_{field_name} ON {table_name}({field_name});
```

## 与 Entity 同步

### 流程
1. **修改 Entity** → 添加/修改字段
2. **创建迁移脚本** → 同步数据库结构
3. **测试验证** → 确保 Entity 和数据库一致
4. **提交代码** → 包括 Entity 和迁移脚本

### 示例
```java
// 1. 修改 Entity
@Data
public class User {
    private Long id;
    private String username;
    private String nickname;  // 新增字段
    // ...
}
```

```sql
-- 2. 创建迁移脚本 V2__Add_user_nickname.sql
ALTER TABLE users ADD COLUMN nickname VARCHAR(50) AFTER username;
```

## 总结

使用 Flyway 可以：
- ✅ 版本化管理数据库结构
- ✅ 自动同步 Schema 变更
- ✅ 团队协作时保持数据库一致
- ✅ 生产环境安全迁移
- ✅ 追踪所有数据库变更历史

记住：**已执行的迁移脚本不能修改，只能创建新的迁移脚本！**
