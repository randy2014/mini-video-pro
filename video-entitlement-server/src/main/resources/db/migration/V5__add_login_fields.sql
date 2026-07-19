-- V5: 登录体系改造：新增密码与权益码字段（幂等）
-- 使用存储过程安全添加列，避免重复执行报错
DELIMITER $$

CREATE PROCEDURE IF NOT EXISTS add_column_if_not_exists()
BEGIN
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_SCHEMA = DATABASE() 
                   AND TABLE_NAME = 'user_account' 
                   AND COLUMN_NAME = 'password') THEN
        ALTER TABLE user_account ADD COLUMN password VARCHAR(100) NULL COMMENT '登录密码(BCrypt 哈希)';
    END IF;
    
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_SCHEMA = DATABASE() 
                   AND TABLE_NAME = 'user_account' 
                   AND COLUMN_NAME = 'entitlement_code') THEN
        ALTER TABLE user_account ADD COLUMN entitlement_code VARCHAR(64) NULL COMMENT '权益码（注册时写入）';
    END IF;
END$$

DELIMITER ;

CALL add_column_if_not_exists();
DROP PROCEDURE IF EXISTS add_column_if_not_exists;
