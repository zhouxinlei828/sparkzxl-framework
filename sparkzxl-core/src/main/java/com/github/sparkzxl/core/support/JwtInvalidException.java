package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.base.code.BaseEnumCode;
import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import lombok.Getter;

/**
 * description: 业务异常类
 *
 * @author zhouxinlei
 */
@Getter
public class JwtInvalidException extends BaseException {

    private static final long serialVersionUID = -2803534562798384761L;

    public JwtInvalidException(BaseEnumCode baseEnumCode, Object[] args, String message) {
        super(baseEnumCode, args, message);
    }

    public JwtInvalidException(BaseEnumCode baseEnumCode, Object[] args, String message, Throwable cause) {
        super(baseEnumCode, args, message, cause);
    }

    public JwtInvalidException(String message) {
        super(ApiResponseStatus.FAILURE.getCode(), message);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
