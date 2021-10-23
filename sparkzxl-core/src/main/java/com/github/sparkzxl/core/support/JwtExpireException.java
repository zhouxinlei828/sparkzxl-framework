package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.base.code.ResponseInfo;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import lombok.Getter;

/**
 * description: 业务异常类
 *
 * @author zhouxinlei
 */
@Getter
public class JwtExpireException extends BaseException {

    private static final long serialVersionUID = -2803534562798384761L;

    public JwtExpireException(String message) {
        super(ResponseInfoStatus.FAILURE.getCode(), message);
    }

    public JwtExpireException(ResponseInfo responseInfo) {
        super(responseInfo);
    }

    public JwtExpireException(ResponseInfo responseInfo, Object[] args, String message) {
        super(responseInfo, args, message);
    }

    public JwtExpireException(ResponseInfo responseInfo, Object[] args, String message, Throwable cause) {
        super(responseInfo, args, message, cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
