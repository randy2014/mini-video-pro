-- 删除空置的风控管理 + 配置发布 模块数据表
-- risk 模块: risk_blacklist / risk_event / risk_rule
-- configrelease 模块: config_release / config_release_item

DROP TABLE IF EXISTS risk_rule;
DROP TABLE IF EXISTS risk_event;
DROP TABLE IF EXISTS risk_blacklist;
DROP TABLE IF EXISTS config_release_item;
DROP TABLE IF EXISTS config_release;
