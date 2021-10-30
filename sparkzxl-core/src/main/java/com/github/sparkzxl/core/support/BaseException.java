package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.base.code.ResponseInfo;
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

    public BaseException(ResponseInfo responseInfo) {
        this.code = responseInfo.getCode();
        this.message = responseInfo.getMessage();
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseException(ResponseInfo responseInfo, Object[] args, String message) {
        this.code = responseInfo.getCode();
        this.args = args;
        this.message = message;
    }

    public BaseException(ResponseInfo responseInfo, Object[] args, String message, Throwable cause) {
        this.code = responseInfo.getCode();
        this.args = args;
        this.message = message;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
