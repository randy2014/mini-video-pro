package com.video.entitlement.module.playback.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum PlaybackRouteType implements CodeEnum {
    OFFICIAL_REDIRECT("OFFICIAL_REDIRECT", "官方页面跳转"),
    AUTHORIZED_WEB("AUTHORIZED_WEB", "授权H5播放页"),
    SIGNED_VOD("SIGNED_VOD", "签名云点播"),
    INTERNAL_WEB("INTERNAL_WEB", "自有内容页面"),
    DENIED("DENIED", "拒绝播放"),
    MAINTENANCE("MAINTENANCE", "维护状态");

    private final String code;
    private final String description;

    PlaybackRouteType(String code, String description) {
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
