package com.github.sparkzxl.alarm.exception;

import com.github.sparkzxl.alarm.enums.AlarmResponseCodeEnum;
import com.github.sparkzxl.entity.response.IErrorCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 异常类
 *
 * @author Jaemon
 * @since 1.0
 */
@Setter
@Getter
public class AlarmException extends RuntimeException {

    /**
     * 具体异常码
     */
    private String errorCode;
    /**
     * 异常信息
     */
    private String errorMessage;

    public AlarmException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public AlarmException(IErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode.getErrorCode();
        this.errorMessage = errorCode.getErrorMessage();
    }

    public AlarmException(Throwable cause) {
        super(cause);
        this.errorCode = AlarmResponseCodeEnum.FAILED.getErrorCode();
        this.errorMessage = cause.getMessage();
    }

    public AlarmException(Throwable cause,String errorCode, String errorMessage) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
