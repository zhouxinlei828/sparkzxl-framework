package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.support.code.IErrorCode;
import com.github.sparkzxl.core.support.code.ResultErrorCode;

/**
 * description：全局异常处理
 *
 * @author zhouxinlei
 */
public class ExceptionAssert {


    public static void failure(String message) {
        throw new BizException(ResultErrorCode.FAILURE.getErrorCode(), message);
    }

    public static void failure(String code, String message) {
        throw new BizException(code, message);
    }

    public static void failure(IErrorCode errorCode) {
        throw new BizException(errorCode);
    }

}
