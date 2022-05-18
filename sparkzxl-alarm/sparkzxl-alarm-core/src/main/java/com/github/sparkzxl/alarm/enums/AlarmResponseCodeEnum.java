package com.github.sparkzxl.alarm.enums;

/**
 * description: 告警响应码
 *
 * @author zhouxinlei
 * @since 2022-05-18 10:50:29
 */
public enum AlarmResponseCodeEnum {
    SUCCESS("D000", "success"),

    ALARM_DISABLED("D101", "Alarm未启用"),

    MESSAGE_TYPE_UNSUPPORTED("D201", "无法支持的消息类型"),
    SEND_MESSAGE_FAILED("D202", "消息发送失败"),
    MESSAGE_PROCESSING_FAILED("D203", "消息处理异常"),
    FAILED("D999", "failed");

    private String code;
    private String message;

    AlarmResponseCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}