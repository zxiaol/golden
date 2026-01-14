-- 手动创建 Flyway 基线（仅在自动基线创建失败时使用）
-- 使用方法：在数据库中执行此 SQL 脚本
-- 注意：此文件不在 migration 目录下，不会被 Flyway 自动执行

-- 1. 创建 Flyway 历史表（如果不存在）
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

-- 2. 插入基线记录（标记当前数据库状态为基线版本 0）
-- 注意：如果表中已有记录，请先检查再插入
INSERT INTO flyway_schema_history 
(installed_rank, version, description, type, script, checksum, installed_by, execution_time, success)
SELECT 1, '0', '<< Flyway Baseline >>', 'BASELINE', '<< Flyway Baseline >>', NULL, USER(), 0, 1
WHERE NOT EXISTS (
    SELECT 1 FROM flyway_schema_history WHERE version = '0'
);
