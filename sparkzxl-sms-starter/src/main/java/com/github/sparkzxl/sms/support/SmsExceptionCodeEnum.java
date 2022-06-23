package com.github.sparkzxl.sms.support;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * description: 短信异常响应码
 *
 * @author zhouxinlei
 * @since 2022-05-18 10:50:29
 */
@Getter
@AllArgsConstructor
public enum SmsExceptionCodeEnum {

    SUCCESS("S000", "成功"),
    PHONE_IS_EMPTY("S001", "手机号为空"),
    ;

    private final String errorCode;
    private final String errorMsg;
}
