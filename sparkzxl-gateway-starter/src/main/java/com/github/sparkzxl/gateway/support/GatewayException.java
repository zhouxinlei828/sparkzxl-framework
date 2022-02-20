package com.github.sparkzxl.gateway.support;

import com.github.sparkzxl.entity.response.IErrorCode;
import com.github.sparkzxl.core.support.BaseException;
import lombok.Getter;

/**
 * description: 网关鉴权异常类
 *
 * @author zhouxinlei
 */
@Getter
public class GatewayException extends BaseException {

    private static final long serialVersionUID = 8151170674849761369L;

    public GatewayException(IErrorCode ICode) {
        super(ICode);
    }

    public GatewayException(IErrorCode ICode, Object[] args, String message) {
        super(ICode, args, message);
    }

    public GatewayException(String code, String message) {
        super(code, message);
    }

    public GatewayException(IErrorCode ICode, Object[] args, String message, Throwable cause) {
        super(ICode, args, message, cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
