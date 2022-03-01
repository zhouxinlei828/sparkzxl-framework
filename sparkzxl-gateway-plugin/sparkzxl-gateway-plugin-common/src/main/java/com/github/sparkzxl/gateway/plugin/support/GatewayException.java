package com.github.sparkzxl.gateway.plugin.support;

import com.github.sparkzxl.core.support.BaseException;
import com.github.sparkzxl.entity.response.IErrorCode;
import lombok.Getter;

/**
 * description: 网关异常
 *
 * @author zhouxinlei
 */
@Getter
public class GatewayException extends BaseException {

    private static final long serialVersionUID = 8151170674849761369L;

    public GatewayException(IErrorCode errorCode) {
        super(errorCode);
    }

    public GatewayException(IErrorCode errorCode, Object[] args, String message) {
        super(errorCode, args, message);
    }

    public GatewayException(String code, String message) {
        super(code, message);
    }

    public GatewayException(IErrorCode errorCode, Object[] args, String message, Throwable cause) {
        super(errorCode, args, message, cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
