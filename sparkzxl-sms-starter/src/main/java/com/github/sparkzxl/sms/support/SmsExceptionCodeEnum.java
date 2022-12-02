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

    SUCCESS("0000", "发送成功"),
    SMS_SEND_FAIL("S001", "发送失败"),
    PHONE_IS_EMPTY("S002", "手机号为空"),
    NOT_FOUND_SMS_REGISTER("S003", "未找到短信注册商"),
    ;
    private final String errorCode;
    private final String errorMsg;
}
