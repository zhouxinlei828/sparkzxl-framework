package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.base.code.BaseEnumCode;

import java.text.MessageFormat;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2020-06-04 22:45:18
 */
public interface BusinessEnumSysAssert extends BaseEnumCode, SparkZxlAssert {

    /**
     * 创建异常
     *
     * @param args 入参
     * @return BaseException
     */
    @Override
    default BaseException newException(Object... args) {
        String msg = MessageFormat.format(this.getMessage(), args);
        return new BusinessException(this, args, msg);
    }

    /**
     * 创建异常
     *
     * @param t    t
     * @param args 入参
     * @return BaseException
     */
    @Override
    default BaseException newException(Throwable t, Object... args) {
        String msg = MessageFormat.format(this.getMessage(), args);
        return new BusinessException(this, args, msg, t);
    }
}
