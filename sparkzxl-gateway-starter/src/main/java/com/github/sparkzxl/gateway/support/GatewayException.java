package com.github.sparkzxl.gateway.support;

import com.github.sparkzxl.core.base.code.ICode;
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

    public GatewayException(ICode ICode) {
        super(ICode);
    }

    public GatewayException(ICode ICode, Object[] args, String message) {
        super(ICode, args, message);
    }

    public GatewayException(String code, String message) {
        super(code, message);
    }

    public GatewayException(ICode ICode, Object[] args, String message, Throwable cause) {
        super(ICode, args, message, cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
