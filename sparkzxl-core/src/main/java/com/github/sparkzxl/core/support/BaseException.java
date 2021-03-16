package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.base.code.BaseEnumCode;
import lombok.Getter;

/**
 * description：BaseException
 *
 * @author zhouxinlei
 */
@Getter
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 5092096093495323869L;

    private int code;

    private Object[] args;

    private String message;

    public BaseException(BaseEnumCode baseEnumCode) {
        this.code = baseEnumCode.getCode();
        this.message = baseEnumCode.getMessage();
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseException(BaseEnumCode baseEnumCode, Object[] args, String message) {
        this.code = baseEnumCode.getCode();
        this.args = args;
        this.message = message;
    }

    public BaseException(BaseEnumCode baseEnumCode, Object[] args, String message, Throwable cause) {
        this.code = baseEnumCode.getCode();
        this.args = args;
        this.message = message;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
