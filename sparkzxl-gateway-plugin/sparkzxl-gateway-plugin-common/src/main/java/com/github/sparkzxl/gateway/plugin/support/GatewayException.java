package com.github.sparkzxl.gateway.plugin.support;

import com.github.sparkzxl.core.support.BaseUncheckedException;
import com.github.sparkzxl.entity.response.IErrorCode;
import lombok.Getter;

/**
 * description: 网关异常
 *
 * @author zhouxinlei
 */
@Getter
public class GatewayException extends BaseUncheckedException {

    private static final long serialVersionUID = 8151170674849761369L;

    public GatewayException(IErrorCode errorCode) {
        super(errorCode);
    }

    public GatewayException(IErrorCode errorCode, String errorMessage) {
        super(errorCode.getErrorCode(), errorMessage);
    }

    public GatewayException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
