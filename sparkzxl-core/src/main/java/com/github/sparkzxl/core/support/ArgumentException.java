package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.support.code.ResultErrorCode;

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

    public ArgumentException(String errorMsg) {
        super(ResultErrorCode.PARAM_VALID_ERROR.getErrorCode(), errorMsg);
    }

    public ArgumentException( String errorMsg, Throwable cause) {
        super(ResultErrorCode.PARAM_VALID_ERROR.getErrorCode(), errorMsg, cause);
    }

    public ArgumentException(final String format, Object... args) {
        super(ResultErrorCode.PARAM_VALID_ERROR.getErrorCode(), format, args);
    }

    @Override
    public String toString() {
        return "ArgumentException [message=" + getMessage() + ", code=" + getErrorCode() + "]";
    }

}
