package com.github.sparkzxl.distributed.cloud.hystrix;

import com.github.sparkzxl.core.constant.CoreConstant;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;

/**
 * description: 降级处理类
 *
 * @author: zhouxinlei
 * @date: 2020-08-13 21:00:52
 */
public class FallBackHandler {

    public static void fallBack() {
        RequestContextHolderUtils.setAttribute(CoreConstant.FALLBACK, true);
    }

}
