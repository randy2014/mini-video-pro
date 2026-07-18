package com.video.entitlement.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 认证
    AUTH_TOKEN_INVALID(1001, "Token无效"),
    AUTH_TOKEN_EXPIRED(1002, "Token已过期"),
    AUTH_CREDENTIALS_INVALID(1003, "用户名或密码错误"),
    CAPTCHA_INVALID(1004, "图形验证码错误"),
    CAPTCHA_EXPIRED(1005, "图形验证码已过期"),

    // 用户
    USER_DISABLED(2001, "用户已禁用"),
    USER_NOT_FOUND(2002, "用户不存在"),

    // 设备
    DEVICE_BLOCKED(3001, "设备已封禁"),

    // 平台
    PLATFORM_NOT_SUPPORTED(5001, "不支持该平台"),
    PLATFORM_DISABLED(5002, "平台已停用"),
    PLATFORM_MAINTENANCE(5003, "平台维护中"),

    // URL
    URL_INVALID(6001, "URL格式非法"),
    URL_HOST_NOT_ALLOWED(6002, "目标域名不在白名单"),
    URL_RULE_NOT_MATCHED(6003, "URL不符合内容规则"),

    // 配置
    CONFIG_VERSION_INVALID(8001, "配置版本无效"),
    CONFIG_SIGNATURE_INVALID(8002, "配置签名校验失败"),
    CONFIG_VALIDATION_FAILED(8003, "配置校验失败"),

    // 风控
    RATE_LIMITED(9001, "请求过于频繁"),
    RISK_BLOCKED(9002, "请求被风控阻止"),

    // 通用
    INVALID_ENUM_VALUE(10001, "枚举值不支持"),
    VALIDATION_ERROR(10002, "参数校验失败"),
    INVALID_STATUS_TRANSITION(10003, "不允许的状态转换"),
    SYSTEM_ERROR(10500, "系统内部错误"),

    // 管理员
    ADMIN_FORBIDDEN(11001, "权限不足"),
    EXPORT_NOT_ALLOWED(11002, "不允许导出"),
    RULE_CONFLICT(11003, "规则冲突");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
