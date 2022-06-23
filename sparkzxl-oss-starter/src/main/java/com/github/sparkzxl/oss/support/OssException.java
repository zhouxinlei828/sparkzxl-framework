package com.github.sparkzxl.oss.support;

import com.github.sparkzxl.core.support.BaseException;
import com.github.sparkzxl.entity.response.IErrorCode;

/**
 * description: oss异常
 *
 * @author zhouxinlei
 * @since 2022-05-03 17:13:39
 */
public class OssException extends RuntimeException implements BaseException {

    /**
     * 异常信息
     */
    private final String errorMessage;

    /**
     * 具体异常码
     */
    private final String errorCode;


    public OssException(String errorMessage, String errorCode) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public OssException(IErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode.getErrorCode();
        this.errorMessage = errorCode.getErrorMessage();
    }


    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }
}
