-- V6: APP版本升级管理表
CREATE TABLE IF NOT EXISTS app_version (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    version_name VARCHAR(32) NOT NULL COMMENT '版本号名称',
    version_code INT NOT NULL COMMENT '版本号(整数，用于比较大小)',
    download_url VARCHAR(512) DEFAULT NULL COMMENT 'APK下载地址',
    release_notes TEXT DEFAULT NULL COMMENT '更新日志',
    force_update TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否强制更新',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态(ACTIVE启用/INACTIVE停用)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_version_code (version_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='APP版本升级管理表';

-- 插入初始版本数据
INSERT INTO app_version (version_name, version_code, download_url, release_notes, force_update, status)
VALUES ('0.9.202607190931', 7190931, 'http://43.161.222.78:8082/downloads/video-entitlement-latest.apk', '沉浸式登录页+透明状态栏+半透明导航栏', 0, 'ACTIVE');
