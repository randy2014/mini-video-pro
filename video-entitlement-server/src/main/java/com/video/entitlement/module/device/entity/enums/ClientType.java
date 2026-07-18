package com.video.entitlement.module.device.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum ClientType implements CodeEnum {
    ANDROID("ANDROID", "Android"),
    IOS("IOS", "iOS"),
    H5("H5", "H5"),
    ADMIN("ADMIN", "管理后台");

    private final String code;
    private final String description;

    ClientType(String code, String description) {
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
