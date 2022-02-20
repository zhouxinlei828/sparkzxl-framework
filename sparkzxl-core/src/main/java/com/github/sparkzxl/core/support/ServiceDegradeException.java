package com.github.sparkzxl.core.support;

import com.github.sparkzxl.entity.response.IErrorCode;

/**
 * description: 服务降级异常
 *
 * @author zhouxinlei
 */

public class ServiceDegradeException extends BaseException {

    private static final long serialVersionUID = -2803534562798384761L;

    public ServiceDegradeException(IErrorCode ICode) {
        super(ICode);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
