package com.github.sparkzxl.core.support;


import com.github.sparkzxl.core.support.code.IErrorCode;

/**
 * description: 服务降级异常
 *
 * @author zhouxinlei
 * @since 2022-03-05 11:44:17
 */
public class FallBackException extends BaseUncheckedException {

    private static final long serialVersionUID = 3834875038681694882L;

    public FallBackException(IErrorCode errorCode) {
        super(errorCode.getErrorCode(), errorCode.getErrorMsg());
    }
}
