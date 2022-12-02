package com.github.sparkzxl.sms.support;


import lombok.Getter;

/**
 * description: 短信异常
 *
 * @author zhouxinlei
 * @since 2022-01-03 15:41:54
 */
@Getter
public class SmsException extends RuntimeException {

    private static final long serialVersionUID = -9028217853132686956L;
    private String errorCode;

    private String errorMsg;


    public SmsException(Throwable cause) {
        super(cause);
    }

    public SmsException(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public SmsException(SmsExceptionCodeEnum smsExceptionCodeEnum) {
        this.errorCode = smsExceptionCodeEnum.getErrorCode();
        this.errorMsg = smsExceptionCodeEnum.getErrorMsg();
    }

}