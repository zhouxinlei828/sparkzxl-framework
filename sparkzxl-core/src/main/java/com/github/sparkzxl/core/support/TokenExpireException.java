package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.support.code.IErrorCode;
import com.github.sparkzxl.core.support.code.ResultErrorCode;

/**
 * description: 登录过期异常
 *
 * @author zhouxinlei
 * @since 2022-03-05 11:44:52
 */
public class TokenExpireException extends BaseUncheckedException {

    public TokenExpireException(Throwable cause) {
        super(cause);
    }

    public TokenExpireException(String errorMsg) {
        super(ResultErrorCode.LOGIN_EXPIRE.getErrorCode(), errorMsg);
    }

    public TokenExpireException(IErrorCode errorCode) {
        super(errorCode.getErrorCode(), errorCode.getErrorMsg());
    }

    @Override
    public String toString() {
        return "TokenExpireException [message=" + getMessage() + ", code=" + getErrorCode() + "]";
    }


}
