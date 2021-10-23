package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.base.code.ResponseInfo;
import lombok.Getter;

/**
 * description: 业务异常类
 *
 * @author zhouxinlei
 */
@Getter
public class BizException extends BaseException {

    private static final long serialVersionUID = -2803534562798384761L;

    public BizException(ResponseInfo responseInfo) {
        super(responseInfo);
    }

    public BizException(ResponseInfo responseInfo, Object[] args, String message) {
        super(responseInfo, args, message);
    }

    public BizException(int code, String message) {
        super(code, message);
    }

    public BizException(ResponseInfo responseInfo, Object[] args, String message, Throwable cause) {
        super(responseInfo, args, message, cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
