package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.base.code.ICode;
import com.github.sparkzxl.core.base.result.ExceptionStatusConstant;
import lombok.Getter;

/**
 * description: 业务异常类
 *
 * @author zhouxinlei
 */
@Getter
public class TenantException extends BaseException {

    private static final long serialVersionUID = -2803534562798384761L;

    public TenantException(ICode ICode) {
        super(ICode);
    }

    public TenantException(ICode ICode, Object[] args, String message) {
        super(ICode, args, message);
    }

    public TenantException(String code, String message) {
        super(code, message);
    }

    public TenantException(String message) {
        super(ExceptionStatusConstant.HTTP_BAD_REQUEST, message);
    }

    public TenantException(ICode ICode, Object[] args, String message, Throwable cause) {
        super(ICode, args, message, cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
