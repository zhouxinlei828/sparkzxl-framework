package com.github.sparkzxl.gateway.support;

import com.github.sparkzxl.core.base.code.ResponseInfo;
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

    public GatewayException(ResponseInfo responseInfo) {
        super(responseInfo);
    }

    public GatewayException(ResponseInfo responseInfo, Object[] args, String message) {
        super(responseInfo, args, message);
    }

    public GatewayException(int code, String message) {
        super(code, message);
    }

    public GatewayException(ResponseInfo responseInfo, Object[] args, String message, Throwable cause) {
        super(responseInfo, args, message, cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
