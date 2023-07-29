package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.support.code.IErrorCode;
import com.github.sparkzxl.core.support.code.ResultErrorCode;

/**
 * description: 用户不存在异常
 *
 * @author zhouxinlei
 * @since 2022-03-05 11:44:52
 */
public class UserNotFoundException extends BaseUncheckedException {

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }

    public UserNotFoundException(String errorMsg) {
        super(ResultErrorCode.USER_NOT_FOUND.getErrorCode(), errorMsg);
    }

    @Override
    public String toString() {
        return "UserNotFoundException [message=" + getMessage() + ", code=" + getErrorCode() + "]";
    }

}
