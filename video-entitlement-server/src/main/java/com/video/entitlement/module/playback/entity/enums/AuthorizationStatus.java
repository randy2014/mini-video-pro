package com.video.entitlement.module.playback.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum AuthorizationStatus implements CodeEnum {
    VERIFIED("VERIFIED", "授权已验证"),
    PENDING("PENDING", "等待审核"),
    EXPIRED("EXPIRED", "授权已到期"),
    REJECTED("REJECTED", "审核未通过");

    private final String code;
    private final String description;

    AuthorizationStatus(String code, String description) {
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
