package com.github.sparkzxl.gateway.support;

import com.github.sparkzxl.core.support.BaseUncheckedException;
import com.github.sparkzxl.core.support.code.ExceptionErrorCode;
import com.github.sparkzxl.core.support.code.IErrorCode;

/**
 * description: 认证异常
 *
 * @author zhouxinlei
 * @since 2023-05-05 09:44:32
 */
public class AuthenticationException extends BaseUncheckedException {

    private static final long serialVersionUID = -4905180354183850985L;

    public AuthenticationException(IErrorCode errorCode) {
        super(errorCode);
    }

    public AuthenticationException(Throwable cause) {
        super(ExceptionErrorCode.FAILURE.getErrorCode(), cause.getMessage());
    }

    public AuthenticationException(IErrorCode errorCode, String errorMsg) {
        super(errorCode.getErrorCode(), errorMsg);
    }

    public AuthenticationException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
