-- 登录体系改造：新增密码与权益码字段
ALTER TABLE user_account ADD COLUMN password VARCHAR(100) NULL COMMENT '登录密码(BCrypt 哈希)';
ALTER TABLE user_account ADD COLUMN entitlement_code VARCHAR(64) NULL COMMENT '权益码（注册时写入）';
