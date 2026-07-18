package com.video.entitlement.module.platform.entity.enums;

import com.video.entitlement.common.enums.CodeEnum;

public enum UrlRuleType implements CodeEnum {
    REGEX("REGEX", "正则表达式"),
    PREFIX("PREFIX", "地址前缀"),
    HOST_PATH("HOST_PATH", "域名路径规则");

    private final String code;
    private final String description;

    UrlRuleType(String code, String description) {
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
