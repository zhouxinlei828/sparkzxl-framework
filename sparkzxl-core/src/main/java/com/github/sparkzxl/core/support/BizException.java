package com.github.sparkzxl.core.support;

import com.github.sparkzxl.entity.response.IErrorCode;
import lombok.Getter;

/**
 * description: 业务异常类
 *
 * @author zhouxinlei
 */
@Getter
public class BizException extends BaseUncheckedException {

    private static final long serialVersionUID = -3238517855583910821L;


    public BizException(IErrorCode errorCode) {
        super(errorCode.getErrorCode(), errorCode.getErrorMessage());
    }

    public BizException(IErrorCode errorCode, String errorMessage) {
        super(errorCode.getErrorCode(), errorMessage);
    }

    public BizException(String code, String message) {
        super(code, message);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
