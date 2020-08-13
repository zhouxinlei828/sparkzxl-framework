package com.sparksys.cloud.hystrix;

import com.sparksys.core.constant.CoreConstant;
import com.sparksys.core.support.ResponseResultStatus;
import com.sparksys.core.utils.RequestContextHolderUtils;

/**
 * description: 降级处理类
 *
 * @author: zhouxinlei
 * @date: 2020-08-13 21:00:52
 */
public class FallBackHandler {

    public static void fallBack() {
        RequestContextHolderUtils.setAttribute(CoreConstant.FALLBACK, true);
        ResponseResultStatus.SERVICE_DEGRADATION.assertException();
    }

}
