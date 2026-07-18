-- 用户权益关联表（记录用户持有的权益）
CREATE TABLE user_entitlement (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    entitlement_id BIGINT NOT NULL COMMENT '权益ID',
    entitlement_code VARCHAR(8) NOT NULL COMMENT '权益代码(冗余，便于查询)',
    obtained_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '获取时间',
    expire_time DATETIME DEFAULT NULL COMMENT '失效时间',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态(ACTIVE有效/EXPIRED已过期/REVOKED已回收)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_user_id (user_id),
    INDEX idx_entitlement_id (entitlement_id),
    UNIQUE KEY uk_user_entitlement (user_id, entitlement_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户权益关联表';
