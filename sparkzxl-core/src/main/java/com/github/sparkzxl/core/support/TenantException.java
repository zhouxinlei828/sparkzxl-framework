package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.support.code.ResultErrorCode;
import lombok.Getter;

/**
 * description: 租户异常类
 *
 * @author zhouxinlei
 */
@Getter
public class TenantException extends BaseUncheckedException {

    private static final long serialVersionUID = -2803534562798384761L;

    public TenantException(String message) {
        super(ResultErrorCode.PARAM_VALID_ERROR.getErrorCode(), message);
    }
}
