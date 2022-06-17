package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.support.code.ResultErrorCode;
import com.github.sparkzxl.core.support.code.IErrorCode;
import lombok.Getter;

/**
 * description: jwt校验异常
 *
 * @author zhouxinlei
 */
@Getter
public class JwtInvalidException extends BaseUncheckedException {

    private static final long serialVersionUID = -2803534562798384761L;

    public JwtInvalidException(IErrorCode errorCode) {
        super(errorCode);
    }

    public JwtInvalidException(String message) {
        super(ResultErrorCode.TOKEN_VALID_ERROR.getErrorCode(), message);
    }

    public JwtInvalidException(Throwable cause) {
        super(cause);
    }
}
