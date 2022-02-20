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
public class JwtInvalidException extends BaseException {

    private static final long serialVersionUID = -2803534562798384761L;

    public JwtInvalidException(IErrorCode ICode, Object[] args, String message) {
        super(ICode, args, message);
    }

    public JwtInvalidException(IErrorCode ICode, Object[] args, String message, Throwable cause) {
        super(ICode, args, message, cause);
    }

    public JwtInvalidException(IErrorCode ICode) {
        super(ICode);
    }

    public JwtInvalidException(String message) {
        super(ExceptionErrorCode.FAILURE.getCode(), message);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
