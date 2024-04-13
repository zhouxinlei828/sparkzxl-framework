package com.github.sparkzxl.oss.support;

import com.github.sparkzxl.core.support.BaseException;
import com.github.sparkzxl.core.support.code.IErrorCode;

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
    private final String errorMsg;

    /**
     * 具体异常码
     */
    private final String errorCode;


    public OssException(String errorCode, String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }

    public OssException(IErrorCode errorCode) {
        super(errorCode.getErrorMsg());
        this.errorCode = errorCode.getErrorCode();
        this.errorMsg = errorCode.getErrorMsg();
    }

    public OssException(IErrorCode errorCode, Throwable cause) {
        super(errorCode.getErrorMsg(), cause);
        this.errorCode = errorCode.getErrorCode();
        this.errorMsg = errorCode.getErrorMsg();
    }

    public OssException(String errorCode, String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }
}
