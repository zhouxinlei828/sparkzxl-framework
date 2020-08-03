package com.sparksys.core.support;

import com.sparksys.core.base.code.BaseEnumCode;
import lombok.Getter;

/**
 * description: 业务异常类
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:49:04
 */
@Getter
public class BusinessException extends BaseException {

    private static final long serialVersionUID = -2803534562798384761L;

    public BusinessException(BaseEnumCode baseEnumCode, Object[] args, String message) {
        super(baseEnumCode, args, message);
    }

    public BusinessException(BaseEnumCode baseEnumCode, Object[] args, String message, Throwable cause) {
        super(baseEnumCode, args, message, cause);
    }

}
