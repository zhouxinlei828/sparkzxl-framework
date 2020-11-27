package com.github.sparkzxl.core.support;

import com.github.sparkzxl.core.base.code.BaseEnumCode;

/**
 * description：断言异常全局处理
 *
 * @author zhouxinlei
 * @date 2020/6/4 10:10 下午
 */
public class SparkZxlExceptionAssert {


    public static void businessFail(String message) {
        throw new BusinessException(ResponseResultStatus.FAILURE, null, message);
    }

    public static void businessFail(BaseEnumCode baseEnumCode) {
        throw new BusinessException(baseEnumCode);
    }

    /**
     * 服务降级异常处理
     */
    public static void serviceDegrade() {
        throw new ServiceDegradeException(ResponseResultStatus.SERVICE_DEGRADATION);
    }

}
