package com.github.sparkzxl.core.support;

import cn.hutool.http.HttpStatus;
import com.github.sparkzxl.core.base.code.BaseEnumCode;
import lombok.Getter;

/**
 * description: 业务异常类
 *
 * @author zhouxinlei
 */
@Getter
public class TenantException extends BaseException {

    private static final long serialVersionUID = -2803534562798384761L;

    public TenantException(BaseEnumCode baseEnumCode) {
        super(baseEnumCode);
    }

    public TenantException(BaseEnumCode baseEnumCode, Object[] args, String message) {
        super(baseEnumCode, args, message);
    }

    public TenantException(int code, String message) {
        super(code, message);
    }

    public TenantException(String message) {
        super(HttpStatus.HTTP_BAD_REQUEST, message);
    }

    public TenantException(BaseEnumCode baseEnumCode, Object[] args, String message, Throwable cause) {
        super(baseEnumCode, args, message, cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
