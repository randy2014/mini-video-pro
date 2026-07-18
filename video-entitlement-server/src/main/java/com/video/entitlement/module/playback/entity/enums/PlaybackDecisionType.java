package com.video.entitlement.module.playback.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum PlaybackDecisionType implements CodeEnum {
    OFFICIAL_REDIRECT("OFFICIAL_REDIRECT", "跳转官方页面"),
    AUTHORIZED_WEB("AUTHORIZED_WEB", "打开授权播放页"),
    SIGNED_VOD("SIGNED_VOD", "使用签名云点播"),
    INTERNAL_WEB("INTERNAL_WEB", "打开内部内容页"),
    DENIED("DENIED", "拒绝本次请求"),
    MAINTENANCE("MAINTENANCE", "服务维护"),
    UPGRADE_REQUIRED("UPGRADE_REQUIRED", "需要升级"),
    REGION_BLOCKED("REGION_BLOCKED", "地区不可用"),
    RISK_BLOCKED("RISK_BLOCKED", "风控阻止");

    private final String code;
    private final String description;

    PlaybackDecisionType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
