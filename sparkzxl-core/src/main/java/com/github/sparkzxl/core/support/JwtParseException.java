package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.support.code.ExceptionErrorCode;
import lombok.Getter;

/**
 * description: jwt解析异常类
 *
 * @author zhouxinlei
 */
@Getter
public class JwtParseException extends BaseUncheckedException {

    private static final long serialVersionUID = 6898087804057803400L;

    public JwtParseException() {
        super(ExceptionErrorCode.JSON_TRANSFORM_ERROR);
    }

    public JwtParseException(String errorMsg) {
        super(ExceptionErrorCode.JSON_TRANSFORM_ERROR.getErrorCode(), errorMsg);
    }
}
