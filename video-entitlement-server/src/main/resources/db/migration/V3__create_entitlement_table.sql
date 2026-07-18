-- 权益表
CREATE TABLE entitlement (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    entitlement_name VARCHAR(128) NOT NULL COMMENT '权益名称',
    entitlement_code VARCHAR(8) NOT NULL COMMENT '权益代码(8位数字)',
    start_time DATETIME DEFAULT NULL COMMENT '权益开始时间',
    end_time DATETIME DEFAULT NULL COMMENT '权益结束时间',
    status VARCHAR(16) NOT NULL DEFAULT 'ENABLED' COMMENT '权益状态(ENABLED启用/DISABLED禁用)',
    owner_name VARCHAR(64) DEFAULT NULL COMMENT '权益拥有人',
    owner_phone VARCHAR(32) DEFAULT NULL COMMENT '权益拥有人电话',
    owner_profession VARCHAR(64) DEFAULT NULL COMMENT '权益拥有人职业',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_entitlement_code (entitlement_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权益表';
