package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.support.code.ExceptionErrorCode;

/**
 * description: 登录过期异常
 *
 * @author zhouxinlei
 * @since 2022-03-05 11:44:52
 */
public class LoginExpireException extends BaseUncheckedException {

    public LoginExpireException() {
        super(ExceptionErrorCode.LOGIN_EXPIRE);
    }

    public LoginExpireException(String message) {
        super(ExceptionErrorCode.LOGIN_EXPIRE.getErrorCode(), message);
    }
}
