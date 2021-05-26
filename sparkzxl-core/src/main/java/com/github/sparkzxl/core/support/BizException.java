package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.base.code.BaseEnumCode;
import lombok.Getter;

/**
 * description: 业务异常类
 *
 * @author zhouxinlei
 */
@Getter
public class BizException extends BaseException {

    private static final long serialVersionUID = -2803534562798384761L;

    public BizException(BaseEnumCode baseEnumCode) {
        super(baseEnumCode);
    }

    public BizException(BaseEnumCode baseEnumCode, Object[] args, String message) {
        super(baseEnumCode, args, message);
    }

    public BizException(int code, String message) {
        super(code, message);
    }

    public BizException(BaseEnumCode baseEnumCode, Object[] args, String message, Throwable cause) {
        super(baseEnumCode, args, message, cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
