package com.github.sparkzxl.core.support;

import cn.hutool.core.util.StrUtil;
import com.github.sparkzxl.core.util.StrPool;
import com.github.sparkzxl.entity.response.IErrorCode;

/**
 * 非运行期异常基类，所有自定义非运行时异常继承该类
 *
 * @author zhouxinlei
 * @see RuntimeException
 */
public class BaseUncheckedException extends RuntimeException implements BaseException {

    private static final long serialVersionUID = 6352083473955354303L;

    /**
     * 异常信息
     */
    private String errorMessage;

    /**
     * 具体异常码
     */
    private String errorCode;

    public BaseUncheckedException(IErrorCode errorCode) {
        this.errorCode = errorCode.getErrorCode();
        this.errorMessage = errorCode.getErrorMessage();
    }

    public BaseUncheckedException(Throwable cause) {
        super(cause);
    }

    public BaseUncheckedException(final String errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }


    public BaseUncheckedException(final String errorCode, final String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BaseUncheckedException(final String errorCode,
                                  final String errorMessage,
                                  Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BaseUncheckedException(final String errorCode, final String format, Object... args) {
        super(StrUtil.contains(format, StrPool.BRACE) ?
                StrUtil.format(format, args) :
                String.format(format, args));
        this.errorCode = errorCode;
        this.errorMessage = StrUtil.contains(format, StrPool.BRACE) ?
                StrUtil.format(format, args) :
                String.format(format, args);
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
