-- V2: 添加平台类型字段 + 清理已删除模块的表
ALTER TABLE video_platform ADD COLUMN IF NOT EXISTS platform_type VARCHAR(32) DEFAULT 'video' COMMENT '平台类型: video/music/tv/drama';

-- 为现有数据设置类型
UPDATE video_platform SET platform_type = 'music' WHERE platform_code IN ('wangyiyun', 'qqmusic', 'kugou');
UPDATE video_platform SET platform_type = 'tv' WHERE platform_code = 'cctv';
UPDATE video_platform SET platform_type = 'drama' WHERE platform_code IN ('meiju', 'hanju');

-- 删除已废弃的表 (播放路由 + 权益管理)
DROP TABLE IF EXISTS playback_attempt;
DROP TABLE IF EXISTS playback_result;
DROP TABLE IF EXISTS playback_request;
DROP TABLE IF EXISTS playback_route;
DROP TABLE IF EXISTS playback_route_group;
DROP TABLE IF EXISTS playback_provider;
DROP TABLE IF EXISTS playback_rule;
DROP TABLE IF EXISTS route_health;
DROP TABLE IF EXISTS route_metric_minute;
DROP TABLE IF EXISTS entitlement_code;
DROP TABLE IF EXISTS entitlement_code_batch;
DROP TABLE IF EXISTS entitlement_product;
DROP TABLE IF EXISTS entitlement_product_platform;
DROP TABLE IF EXISTS entitlement_usage;
DROP TABLE IF EXISTS user_entitlement;
