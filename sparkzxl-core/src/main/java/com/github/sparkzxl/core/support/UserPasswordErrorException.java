package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.support.code.ResultErrorCode;

/**
 * description: 用户密码错误异常
 *
 * @author zhouxinlei
 * @since 2022-03-05 11:44:52
 */
public class UserPasswordErrorException extends BaseUncheckedException {

    public UserPasswordErrorException(Throwable cause) {
        super(cause);
    }

    public UserPasswordErrorException(String errorMsg) {
        super(ResultErrorCode.USER_PASSWORD_ERROR.getErrorCode(), errorMsg);
    }

    @Override
    public String toString() {
        return "UserNotFoundException [message=" + getMessage() + ", code=" + getErrorCode() + "]";
    }

}
