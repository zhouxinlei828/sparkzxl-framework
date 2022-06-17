package com.github.sparkzxl.alarm.enums;

import com.github.sparkzxl.core.support.code.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * description: 告警响应码
 *
 * @author zhouxinlei
 * @since 2022-05-18 10:50:29
 */
@Getter
@AllArgsConstructor
public enum AlarmResponseCodeEnum implements IErrorCode {
    SUCCESS("D000", "成功"),
    FAILED("D999", "失败"),
    ALARM_DISABLED("D101", "Alarm未启用"),
    MESSAGE_TYPE_UNSUPPORTED("D201", "无法支持的消息类型"),
    SEND_MESSAGE_FAILED("D202", "消息发送失败"),
    MESSAGE_PROCESSING_FAILED("D203", "消息处理异常"),
    ALARM_TYPE_UNSUPPORTED("D204", "不支持的[{0}]告警方式，请确认是否已加载[{1}]执行器"),
    CONFIG_NOT_FIND("D205", "未配置告警属性"),
    /**
     * 异步调用异常
     */
    ASYNC_CALL("Y101", "异步调用异常"),
    ;

    private final String errorCode;
    private final String errorMsg;

}