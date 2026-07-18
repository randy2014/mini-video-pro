package com.video.entitlement.module.playback.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum ProviderType implements CodeEnum {
    OFFICIAL_PLATFORM("OFFICIAL_PLATFORM", "官方视频平台"),
    AUTHORIZED_PARTNER("AUTHORIZED_PARTNER", "授权合作供应商"),
    CLOUD_VOD("CLOUD_VOD", "云点播供应商"),
    INTERNAL("INTERNAL", "项目自有内容"),
    MOCK("MOCK", "开发测试供应商");

    private final String code;
    private final String description;

    ProviderType(String code, String description) {
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
