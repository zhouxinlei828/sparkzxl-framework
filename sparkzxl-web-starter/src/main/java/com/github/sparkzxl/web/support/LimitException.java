package com.github.sparkzxl.web.support;

import com.github.sparkzxl.core.support.BaseUncheckedException;
import com.github.sparkzxl.core.support.code.ExceptionErrorCode;

/**
 * description: 限流异常
 *
 * @author zhouxinlei
 * @since 2024-03-29 10:47:36
 */
public class LimitException extends BaseUncheckedException {

    public LimitException(Throwable cause) {
        super(cause);
    }

    public LimitException(String errorMessage) {
        super(ExceptionErrorCode.REQ_LIMIT.getErrorCode(), errorMessage);
    }

    public LimitException(String errorMessage, Throwable cause) {
        super(ExceptionErrorCode.REQ_LIMIT.getErrorCode(), errorMessage, cause);
    }

    public LimitException(final String format, Object... args) {
        super(ExceptionErrorCode.REQ_LIMIT.getErrorCode(), format, args);
    }

    @Override
    public String toString() {
        return "LimitException [message=" + getMessage() + ", code=" + getErrorCode() + "]";
    }

}
