package com.video.entitlement.module.entitlement.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum EntitlementBatchStatus implements CodeEnum {
    CREATED("CREATED", "已创建"),
    ACTIVE("ACTIVE", "已启用"),
    SUSPENDED("SUSPENDED", "已暂停"),
    FINISHED("FINISHED", "已结束"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String description;

    EntitlementBatchStatus(String code, String description) {
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
