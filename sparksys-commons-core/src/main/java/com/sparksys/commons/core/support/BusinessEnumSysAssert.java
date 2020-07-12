package com.sparksys.commons.core.support;

import com.sparksys.commons.core.base.api.code.BaseEnumCode;

import java.text.MessageFormat;

/**
 * description:
 *
 * @author zhouxinlei
 * @date  2020-06-04 22:45:18
 */
public interface BusinessEnumSysAssert extends BaseEnumCode, SparkSysAssert {

    @Override
    default BaseException newException(Object... args) {
        String msg = MessageFormat.format(this.getMessage(), args);
        return new BusinessException(this, args, msg);
    }

    @Override
    default BaseException newException(Throwable t, Object... args) {
        String msg = MessageFormat.format(this.getMessage(), args);
        return new BusinessException(this, args, msg, t);
    }
}
