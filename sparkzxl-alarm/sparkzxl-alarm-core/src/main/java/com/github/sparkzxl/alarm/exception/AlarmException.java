package com.github.sparkzxl.alarm.exception;

import com.github.sparkzxl.alarm.enums.AlarmErrorEnum;
import com.github.sparkzxl.core.support.code.IErrorCode;
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
    private String errorMsg;

    public AlarmException(String errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public AlarmException(IErrorCode errorCode) {
        super(errorCode.getErrorMsg());
        this.errorCode = errorCode.getErrorCode();
        this.errorMsg = errorCode.getErrorMsg();
    }

    public AlarmException(Throwable cause) {
        super(cause);
        this.errorCode = AlarmErrorEnum.FAILED.getErrorCode();
        this.errorMsg = cause.getMessage();
    }

    public AlarmException(Throwable cause, String errorCode, String errorMsg) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
