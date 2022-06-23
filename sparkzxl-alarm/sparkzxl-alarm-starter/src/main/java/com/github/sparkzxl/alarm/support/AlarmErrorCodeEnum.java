package com.github.sparkzxl.alarm.support;

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
public enum AlarmErrorCodeEnum implements IErrorCode {
    TEMPLATE_ID_NOT_FOUND("D301", "告警模板配置id不能为空"),
    TEMPLATE_NOT_FOUND("D302", "未发现告警配置模板"),
    ;

    private final String errorCode;
    private final String errorMsg;

}