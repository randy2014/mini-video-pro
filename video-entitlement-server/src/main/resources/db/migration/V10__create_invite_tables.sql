-- V10: 邀请裂变系统
CREATE TABLE invite_code (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    code VARCHAR(16) NOT NULL COMMENT '邀请码',
    total_used INT NOT NULL DEFAULT 0 COMMENT '已使用次数',
    reward_days INT NOT NULL DEFAULT 3 COMMENT '邀请人和被邀请人各奖励天数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code),
    UNIQUE KEY uk_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户邀请码';

CREATE TABLE invite_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    code VARCHAR(16) NOT NULL COMMENT '邀请码',
    inviter_user_id BIGINT NOT NULL COMMENT '邀请人ID',
    invitee_user_id BIGINT NOT NULL COMMENT '被邀请人ID',
    reward_days INT NOT NULL DEFAULT 3 COMMENT '各奖励天数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_inviter (inviter_user_id),
    KEY idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邀请记录';
