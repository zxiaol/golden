# Flyway 使用指南

## 问题解决

如果遇到 `Unable to connect to the database` 错误，说明 Flyway Maven 插件需要配置数据库连接信息。

## 使用方式

### 方式一：通过 Spring Boot 应用启动（推荐）

**最简单的方式**，应用启动时 Flyway 会自动执行迁移：

```bash
# 启动应用，Flyway 会自动执行迁移
mvn spring-boot:run

# 或运行 JAR
java -jar target/ecommerce-backend-1.0.0.jar
```

**优点：**
- 自动读取 `application.yml` 中的数据库配置
- 无需额外配置
- 迁移和应用启动一起完成

### 方式二：使用 Maven 命令（需要配置）

#### 1. 使用默认配置（pom.xml 中已配置）

```bash
# 使用 pom.xml 中的默认配置（localhost:3306）
mvn flyway:migrate
```

#### 2. 通过环境变量指定数据库

```bash
# 设置数据库连接信息
export flyway.url=jdbc:mysql://10.128.244.10:3306/ecommerce?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
export flyway.user=root
export flyway.password=root

# 执行迁移
mvn flyway:migrate
```

#### 3. 通过 Maven 参数指定

```bash
mvn flyway:migrate \
  -Dflyway.url=jdbc:mysql://10.128.244.10:3306/ecommerce?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai \
  -Dflyway.user=root \
  -Dflyway.password=root
```

#### 4. 在 pom.xml 中配置（已配置）

pom.xml 中已经配置了 Flyway Maven 插件，默认使用：
- URL: `jdbc:mysql://localhost:3306/ecommerce?...`
- User: `root`
- Password: `root`

可以通过环境变量或 Maven 参数覆盖。

## 常用 Flyway Maven 命令

### 查看迁移状态
```bash
mvn flyway:info
```

### 执行迁移
```bash
mvn flyway:migrate
```

### 验证迁移脚本
```bash
mvn flyway:validate
```

### 查看迁移历史
```bash
mvn flyway:history
```

### 清理数据库（仅开发环境）
```bash
mvn flyway:clean
```

**警告：** `clean` 会删除所有数据库对象，仅用于开发环境！

## 配置说明

### pom.xml 配置

```xml
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
    <configuration>
        <url>${flyway.url:jdbc:mysql://localhost:3306/ecommerce?...}</url>
        <user>${flyway.user:root}</user>
        <password>${flyway.password:root}</password>
        <locations>
            <location>classpath:db/migration</location>
        </locations>
    </configuration>
</plugin>
```

### 环境变量优先级

1. Maven 参数（`-Dflyway.url=...`）
2. 环境变量（`export flyway.url=...`）
3. pom.xml 中的默认值

## 推荐工作流程

### 开发环境

1. **修改 Entity 或 Schema**
   ```java
   // User.java
   private String nickname;
   ```

2. **创建迁移脚本**
   ```sql
   -- V2__Add_user_nickname.sql
   ALTER TABLE users ADD COLUMN nickname VARCHAR(50);
   ```

3. **启动应用（自动迁移）**
   ```bash
   mvn spring-boot:run
   ```
   
   或者手动执行迁移：
   ```bash
   mvn flyway:migrate -Dflyway.url=jdbc:mysql://10.128.244.10:3306/ecommerce?...
   ```

### 生产环境

1. **提交迁移脚本到 Git**
2. **部署应用**
3. **应用启动时自动执行迁移**

## 故障排查

### 问题 1：无法连接数据库

**错误：** `Unable to connect to the database`

**解决：**
1. 检查数据库是否运行
2. 检查连接信息是否正确
3. 使用环境变量或 Maven 参数指定正确的数据库连接

```bash
mvn flyway:migrate \
  -Dflyway.url=jdbc:mysql://your-host:3306/ecommerce?...
  -Dflyway.user=your_user
  -Dflyway.password=your_password
```

### 问题 2：迁移脚本执行失败

**解决：**
1. 查看错误日志
2. 修复脚本问题
3. 删除失败的迁移记录（如果需要）：
   ```sql
   DELETE FROM flyway_schema_history WHERE success = 0;
   ```
4. 重新执行迁移

### 问题 3：迁移脚本已存在但未执行

**解决：**
检查 `flyway_schema_history` 表：
```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC;
```

如果脚本版本号冲突，需要重命名脚本文件。

## 最佳实践

1. **开发环境**：直接启动应用，让 Flyway 自动迁移
2. **生产环境**：应用启动时自动迁移（推荐）
3. **手动迁移**：仅在特殊情况下使用 Maven 命令
4. **版本管理**：迁移脚本提交到 Git，团队共享

## 总结

- ✅ **推荐**：启动 Spring Boot 应用，Flyway 自动执行迁移
- ⚠️ **可选**：使用 Maven 命令手动执行迁移（需要配置数据库连接）
- 📝 **记住**：Maven 插件需要单独配置数据库连接，不会自动读取 application.yml
