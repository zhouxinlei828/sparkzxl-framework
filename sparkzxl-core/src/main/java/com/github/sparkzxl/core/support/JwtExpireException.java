package com.github.sparkzxl.core.support;

import com.github.sparkzxl.entity.response.IErrorCode;
import com.github.sparkzxl.core.base.result.ExceptionErrorCode;
import lombok.Getter;

/**
 * description: 业务异常类
 *
 * @author zhouxinlei
 */
@Getter
public class JwtExpireException extends BaseException {

    private static final long serialVersionUID = -2803534562798384761L;

    public JwtExpireException(String message) {
        super(ExceptionErrorCode.FAILURE.getCode(), message);
    }

    public JwtExpireException(IErrorCode ICode) {
        super(ICode);
    }

    public JwtExpireException(IErrorCode ICode, Object[] args, String message) {
        super(ICode, args, message);
    }

    public JwtExpireException(IErrorCode IErrorCode, Object[] args, String message, Throwable cause) {
        super(IErrorCode, args, message, cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
