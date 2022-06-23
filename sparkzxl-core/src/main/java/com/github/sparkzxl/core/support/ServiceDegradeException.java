package com.github.sparkzxl.core.support;

import com.github.sparkzxl.entity.response.IErrorCode;

/**
 * description: 服务降级异常
 *
 * @author zhouxinlei
 * @since 2022-03-05 11:44:17
 */
public class ServiceDegradeException extends BaseUncheckedException {

    private static final long serialVersionUID = -7216954836495541312L;

    public ServiceDegradeException(IErrorCode errorCode) {
        super(errorCode.getErrorCode(), errorCode.getErrorMessage());
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
