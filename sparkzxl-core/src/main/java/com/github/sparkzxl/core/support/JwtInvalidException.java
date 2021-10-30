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
public class JwtInvalidException extends BaseException {

    private static final long serialVersionUID = -2803534562798384761L;

    public JwtInvalidException(ResponseInfo responseInfo, Object[] args, String message) {
        super(responseInfo, args, message);
    }

    public JwtInvalidException(ResponseInfo responseInfo, Object[] args, String message, Throwable cause) {
        super(responseInfo, args, message, cause);
    }

    public JwtInvalidException(String message) {
        super(ResponseInfoStatus.FAILURE.getCode(), message);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
