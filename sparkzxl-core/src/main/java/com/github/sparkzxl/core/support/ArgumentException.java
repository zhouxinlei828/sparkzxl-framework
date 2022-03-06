package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.base.result.ExceptionErrorCode;

/**
 * description: 参数异常
 *
 * @author zhouxinlei
 * @since 2022-03-05 11:44:52
 */
public class ArgumentException extends BaseUncheckedException {

    public ArgumentException(Throwable cause) {
        super(cause);
    }

    public ArgumentException(String errorMessage) {
        super(ExceptionErrorCode.PARAM_VALID_ERROR.getErrorCode(), errorMessage);
    }

    public ArgumentException(String errorMessage, Throwable cause) {
        super(ExceptionErrorCode.PARAM_VALID_ERROR.getErrorCode(), errorMessage, cause);
    }

    public ArgumentException(final String format, Object... args) {
        super(ExceptionErrorCode.PARAM_VALID_ERROR.getErrorCode(), format, args);
    }

    @Override
    public String toString() {
        return "ArgumentException [message=" + getMessage() + ", code=" + getErrorCode() + "]";
    }

}
