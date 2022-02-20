package com.github.sparkzxl.core.support;

import com.github.sparkzxl.entity.response.IErrorCode;
import lombok.Getter;

/**
 * description：BaseException
 *
 * @author zhouxinlei
 */
@Getter
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 5092096093495323869L;

    private String code;

    private Object[] args;

    private String message;

    public BaseException(IErrorCode ICode) {
        this.code = ICode.getCode();
        this.message = ICode.getMessage();
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseException(IErrorCode ICode, Object[] args, String message) {
        this.code = ICode.getCode();
        this.args = args;
        this.message = message;
    }

    public BaseException(IErrorCode ICode, Object[] args, String message, Throwable cause) {
        this.code = ICode.getCode();
        this.args = args;
        this.message = message;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
