package com.github.sparkzxl.gateway.support;

import com.github.sparkzxl.core.support.BaseUncheckedException;
import com.github.sparkzxl.core.support.code.IErrorCode;
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

    public GatewayException(Throwable cause) {
        super("500", cause.getMessage());
    }

    public GatewayException(IErrorCode errorCode, String errorMsg) {
        super(errorCode.getErrorCode(), errorMsg);
    }

    public GatewayException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
