package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.base.code.ICode;
import lombok.Getter;

/**
 * description: 业务异常类
 *
 * @author zhouxinlei
 */
@Getter
public class BizException extends BaseException {

    private static final long serialVersionUID = -2803534562798384761L;

    public BizException(ICode ICode) {
        super(ICode);
    }

    public BizException(ICode ICode, Object[] args, String message) {
        super(ICode, args, message);
    }

    public BizException(String code, String message) {
        super(code, message);
    }

    public BizException(ICode ICode, Object[] args, String message, Throwable cause) {
        super(ICode, args, message, cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
