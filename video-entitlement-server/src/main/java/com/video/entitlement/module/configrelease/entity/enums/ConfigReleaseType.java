package com.video.entitlement.module.configrelease.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum ConfigReleaseType implements CodeEnum {
    DRAFT("DRAFT", "草稿"),
    TEST("TEST", "测试"),
    GRAY("GRAY", "灰度"),
    OFFICIAL("OFFICIAL", "正式");

    private final String code;
    private final String description;

    ConfigReleaseType(String code, String description) {
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
