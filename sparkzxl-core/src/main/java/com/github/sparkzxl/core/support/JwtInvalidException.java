package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.base.code.ICode;
import com.github.sparkzxl.core.base.result.ExceptionCode;
import lombok.Getter;

/**
 * description: 业务异常类
 *
 * @author zhouxinlei
 */
@Getter
public class JwtInvalidException extends BaseException {

    private static final long serialVersionUID = -2803534562798384761L;

    public JwtInvalidException(ICode ICode, Object[] args, String message) {
        super(ICode, args, message);
    }

    public JwtInvalidException(ICode ICode, Object[] args, String message, Throwable cause) {
        super(ICode, args, message, cause);
    }

    public JwtInvalidException(ICode ICode) {
        super(ICode);
    }

    public JwtInvalidException(String message) {
        super(ExceptionCode.FAILURE.getCode(), message);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
