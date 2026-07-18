-- ======================================================
-- V1: 初始化所有数据表 (视频权益APP后端 - 第一阶段)
-- ======================================================

-- 用户模块
CREATE TABLE user_account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_no VARCHAR(32) NOT NULL,
    mobile VARCHAR(20) NULL,
    nickname VARCHAR(64) NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    risk_level VARCHAR(32) NOT NULL DEFAULT 'LOW',
    last_login_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL,
    UNIQUE INDEX uk_user_no (user_no),
    UNIQUE INDEX uk_mobile (mobile),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    device_public_id VARCHAR(128) NOT NULL,
    client_type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    app_version VARCHAR(32) NULL,
    last_active_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_user_device (user_id, device_public_id),
    INDEX idx_device_public_id (device_public_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_login_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    device_public_id VARCHAR(128) NULL,
    ip_address VARCHAR(64) NULL,
    user_agent VARCHAR(512) NULL,
    login_result VARCHAR(32) NOT NULL,
    failure_reason VARCHAR(256) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id_created (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 权益模块
CREATE TABLE entitlement_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(64) NOT NULL,
    product_name VARCHAR(128) NOT NULL,
    description VARCHAR(512) NULL,
    validity_type VARCHAR(32) NOT NULL,
    valid_days INT NULL,
    valid_from DATETIME NULL,
    valid_until DATETIME NULL,
    daily_usage_limit INT NULL,
    total_usage_limit INT NULL,
    device_limit INT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_product_code (product_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE entitlement_product_platform (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    platform_code VARCHAR(64) NOT NULL,
    scope_type VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_product_platform (product_id, platform_code),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE entitlement_code_batch (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_no VARCHAR(64) NOT NULL,
    product_id BIGINT NOT NULL,
    channel_code VARCHAR(64) NULL,
    quantity INT NOT NULL DEFAULT 0,
    generated_count INT NOT NULL DEFAULT 0,
    activated_count INT NOT NULL DEFAULT 0,
    status VARCHAR(32) NOT NULL DEFAULT 'CREATED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_batch_no (batch_no),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE entitlement_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_id BIGINT NOT NULL,
    code_hash VARCHAR(128) NOT NULL,
    code_masked VARCHAR(32) NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'UNUSED',
    activated_user_id BIGINT NULL,
    activated_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_code_hash (code_hash),
    INDEX idx_batch_id (batch_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_entitlement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    source_type VARCHAR(32) NOT NULL,
    source_id BIGINT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    effective_at DATETIME NULL,
    expires_at DATETIME NULL,
    used_total INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_status (user_id, status),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE entitlement_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_entitlement_id BIGINT NOT NULL,
    platform_code VARCHAR(64) NOT NULL,
    content_key VARCHAR(256) NULL,
    usage_date DATE NOT NULL,
    usage_count INT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_entitlement_date (user_entitlement_id, usage_date),
    INDEX idx_usage_date (usage_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 平台模块
CREATE TABLE video_platform (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    platform_code VARCHAR(64) NOT NULL,
    platform_name VARCHAR(128) NOT NULL,
    home_url VARCHAR(512) NOT NULL,
    description VARCHAR(512) NULL,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    minimum_android_version VARCHAR(32) NULL,
    minimum_ios_version VARCHAR(32) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_platform_code (platform_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE video_platform_domain (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    platform_id BIGINT NOT NULL,
    scheme VARCHAR(16) NOT NULL DEFAULT 'https',
    host VARCHAR(256) NOT NULL,
    include_subdomains TINYINT(1) NOT NULL DEFAULT 0,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_platform_host (platform_id, host),
    INDEX idx_platform_id (platform_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE video_url_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    platform_id BIGINT NOT NULL,
    rule_type VARCHAR(32) NOT NULL,
    pattern VARCHAR(1024) NOT NULL,
    priority INT NOT NULL DEFAULT 0,
    extract_config JSON NULL,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_platform_priority (platform_id, priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 播放模块
CREATE TABLE playback_provider (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider_code VARCHAR(64) NOT NULL,
    provider_name VARCHAR(128) NOT NULL,
    provider_type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    authorization_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    authorization_expiry DATETIME NULL,
    config_json JSON NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_provider_code (provider_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE playback_route_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_code VARCHAR(64) NOT NULL,
    platform_code VARCHAR(64) NULL,
    selection_strategy VARCHAR(32) NOT NULL DEFAULT 'PRIORITY',
    maximum_attempts INT NOT NULL DEFAULT 3,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_group_code (group_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE playback_route (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_code VARCHAR(64) NOT NULL,
    provider_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    route_type VARCHAR(32) NOT NULL,
    target_template VARCHAR(1024) NULL,
    priority INT NOT NULL DEFAULT 0,
    allowed_hosts JSON NULL,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    authorization_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_route_code (route_code),
    INDEX idx_group_id (group_id),
    INDEX idx_provider_id (provider_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE playback_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    platform_code VARCHAR(64) NOT NULL,
    client_type VARCHAR(32) NULL,
    version_range VARCHAR(64) NULL,
    entitlement_product_id BIGINT NULL,
    route_group_id BIGINT NOT NULL,
    priority INT NOT NULL DEFAULT 0,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_platform_priority (platform_code, priority),
    INDEX idx_route_group (route_group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE playback_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_no VARCHAR(64) NOT NULL,
    user_id BIGINT NOT NULL,
    platform_code VARCHAR(64) NOT NULL,
    content_key VARCHAR(256) NULL,
    canonical_url VARCHAR(2048) NULL,
    original_url VARCHAR(2048) NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'CREATED',
    maximum_attempts INT NOT NULL DEFAULT 3,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_request_no (request_no),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE playback_attempt (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    playback_request_id BIGINT NOT NULL,
    attempt_no INT NOT NULL DEFAULT 1,
    route_id BIGINT NOT NULL,
    result VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    error_type VARCHAR(64) NULL,
    duration_ms INT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_request_attempt (playback_request_id, attempt_no),
    INDEX idx_request_id (playback_request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE playback_result (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    playback_request_id BIGINT NOT NULL,
    final_status VARCHAR(32) NOT NULL,
    successful_route_id BIGINT NULL,
    attempt_count INT NOT NULL DEFAULT 0,
    total_duration_ms INT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_request_id (playback_request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 健康模块
CREATE TABLE route_health (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_id BIGINT NOT NULL,
    health_status VARCHAR(32) NOT NULL DEFAULT 'HEALTHY',
    success_rate_5m DECIMAL(5,4) NOT NULL DEFAULT 1.0000,
    consecutive_failure_count INT NOT NULL DEFAULT 0,
    circuit_open_until DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_route_id (route_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE route_metric_minute (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_id BIGINT NOT NULL,
    metric_minute DATETIME NOT NULL,
    request_count INT NOT NULL DEFAULT 0,
    success_count INT NOT NULL DEFAULT 0,
    failure_count INT NOT NULL DEFAULT 0,
    p95_ms INT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_route_minute (route_id, metric_minute)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 配置发布模块
CREATE TABLE config_release (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    release_no VARCHAR(64) NOT NULL,
    release_type VARCHAR(32) NOT NULL,
    config_version VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
    gray_percentage INT DEFAULT 0,
    description VARCHAR(512) NULL,
    published_at DATETIME NULL,
    created_by BIGINT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_release_no (release_no),
    UNIQUE INDEX uk_config_version (config_version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE config_release_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    release_id BIGINT NOT NULL,
    config_type VARCHAR(64) NOT NULL,
    business_id BIGINT NOT NULL,
    content_json TEXT NOT NULL,
    content_hash VARCHAR(128) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_release_business (release_id, config_type, business_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 管理员模块
CREATE TABLE admin_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL,
    password_hash VARCHAR(256) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    failed_login_attempts INT NOT NULL DEFAULT 0,
    locked_until DATETIME NULL,
    last_login_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE admin_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(64) NOT NULL,
    role_name VARCHAR(128) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE admin_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    permission_code VARCHAR(128) NOT NULL,
    permission_name VARCHAR(256) NOT NULL,
    resource_type VARCHAR(32) NOT NULL,
    path VARCHAR(256) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_permission_code (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE admin_user_role (
    admin_user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (admin_user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE admin_role_permission (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE admin_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    admin_user_id BIGINT NULL,
    module VARCHAR(64) NOT NULL,
    operation VARCHAR(64) NOT NULL,
    business_id VARCHAR(128) NULL,
    before_json TEXT NULL,
    after_json TEXT NULL,
    result VARCHAR(32) NOT NULL,
    request_id VARCHAR(64) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_admin_created (admin_user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 风控模块
CREATE TABLE risk_blacklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blacklist_type VARCHAR(32) NOT NULL,
    target_value VARCHAR(256) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    reason VARCHAR(512) NULL,
    start_time DATETIME NULL,
    end_time DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_type_target (blacklist_type, target_value),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE risk_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    risk_event_type VARCHAR(64) NOT NULL,
    user_id BIGINT NULL,
    device_id VARCHAR(128) NULL,
    ip_address VARCHAR(64) NULL,
    risk_level VARCHAR(32) NOT NULL,
    action VARCHAR(32) NOT NULL,
    evidence JSON NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_event_created (risk_event_type, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE risk_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_code VARCHAR(64) NOT NULL,
    event_type VARCHAR(64) NOT NULL,
    threshold INT NOT NULL,
    window_seconds INT NOT NULL,
    action VARCHAR(32) NOT NULL,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_rule_code (rule_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
