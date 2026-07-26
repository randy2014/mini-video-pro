-- ============================================================
-- V9: 删除已废弃模块的冗余表（14张）
-- ============================================================

-- 播放路由模块（已删除，代码中无对应 Entity）
DROP TABLE IF EXISTS playback_attempt;
DROP TABLE IF EXISTS playback_result;
DROP TABLE IF EXISTS playback_rule;
DROP TABLE IF EXISTS playback_route;
DROP TABLE IF EXISTS playback_route_group;
DROP TABLE IF EXISTS playback_provider;
DROP TABLE IF EXISTS playback_request;

-- 路由健康/指标（已删除）
DROP TABLE IF EXISTS route_health;
DROP TABLE IF EXISTS route_metric_minute;

-- 权益码/产品/使用记录模块（已删除，仅保留 entitlement + user_entitlement）
DROP TABLE IF EXISTS entitlement_code;
DROP TABLE IF EXISTS entitlement_code_batch;
DROP TABLE IF EXISTS entitlement_product;
DROP TABLE IF EXISTS entitlement_product_platform;
DROP TABLE IF EXISTS entitlement_usage;
