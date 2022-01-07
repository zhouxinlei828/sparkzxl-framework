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
public class JwtExpireException extends BaseException {

    private static final long serialVersionUID = -2803534562798384761L;

    public JwtExpireException(String message) {
        super(ExceptionCode.FAILURE.getCode(), message);
    }

    public JwtExpireException(ICode ICode) {
        super(ICode);
    }

    public JwtExpireException(ICode ICode, Object[] args, String message) {
        super(ICode, args, message);
    }

    public JwtExpireException(ICode ICode, Object[] args, String message, Throwable cause) {
        super(ICode, args, message, cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
