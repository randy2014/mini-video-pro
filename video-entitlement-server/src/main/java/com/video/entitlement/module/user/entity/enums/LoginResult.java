package com.video.entitlement.module.user.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum LoginResult implements CodeEnum {
    SUCCESS("SUCCESS", "成功"),
    FAILED("FAILED", "失败"),
    BLOCKED("BLOCKED", "已拦截");

    private final String code;
    private final String description;

    LoginResult(String code, String description) {
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
