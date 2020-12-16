package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.base.code.BaseEnumCode;
import lombok.Getter;

/**
 * description: 业务异常类
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:49:04
 */
@Getter
public class JwtExpireException extends BaseException {

    private static final long serialVersionUID = -2803534562798384761L;

    public JwtExpireException(String message) {
        super(ResponseResultStatus.FAILURE.code, message);
    }

    public JwtExpireException(BaseEnumCode baseEnumCode, Object[] args, String message) {
        super(baseEnumCode, args, message);
    }

    public JwtExpireException(BaseEnumCode baseEnumCode, Object[] args, String message, Throwable cause) {
        super(baseEnumCode, args, message, cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
