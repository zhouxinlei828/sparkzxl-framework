package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.base.code.BaseEnumCode;

/**
 * description: 服务降级异常
 *
 * @author: zhouxinlei
 * @date: 2020-11-27 16:18:20
 */

public class ServiceDegradeException extends BaseException {

    private static final long serialVersionUID = -2803534562798384761L;

    public ServiceDegradeException(BaseEnumCode baseEnumCode) {
        super(baseEnumCode);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
