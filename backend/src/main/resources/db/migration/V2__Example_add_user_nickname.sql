-- 示例迁移脚本：添加用户昵称字段
-- 这是一个示例文件，展示如何创建迁移脚本
-- 实际使用时，根据 Entity 变更创建对应的迁移脚本

-- 添加昵称字段
-- ALTER TABLE users 
-- ADD COLUMN nickname VARCHAR(50) COMMENT '用户昵称' AFTER username;

-- 添加昵称索引
-- CREATE INDEX idx_nickname ON users(nickname);

-- 注意：
-- 1. 如果字段已存在，此脚本会报错
-- 2. 实际使用时，可以使用 IF NOT EXISTS（MySQL 8.0+）
-- 3. 或者先检查字段是否存在再添加
