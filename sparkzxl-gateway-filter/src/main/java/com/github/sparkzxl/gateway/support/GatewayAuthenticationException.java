package com.github.sparkzxl.gateway.support;

import com.github.sparkzxl.core.base.code.BaseEnumCode;
import com.github.sparkzxl.core.support.BaseException;
import lombok.Getter;

/**
 * description: 网关鉴权异常类
 *
 * @author zhouxinlei
 */
@Getter
public class GatewayAuthenticationException extends BaseException {

    private static final long serialVersionUID = 8151170674849761369L;

    public GatewayAuthenticationException(BaseEnumCode baseEnumCode) {
        super(baseEnumCode);
    }

    public GatewayAuthenticationException(BaseEnumCode baseEnumCode, Object[] args, String message) {
        super(baseEnumCode, args, message);
    }

    public GatewayAuthenticationException(int code, String message) {
        super(code, message);
    }

    public GatewayAuthenticationException(BaseEnumCode baseEnumCode, Object[] args, String message, Throwable cause) {
        super(baseEnumCode, args, message, cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
