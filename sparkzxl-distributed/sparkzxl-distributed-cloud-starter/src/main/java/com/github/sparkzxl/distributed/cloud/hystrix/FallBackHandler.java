package com.github.sparkzxl.distributed.cloud.hystrix;

import com.github.sparkzxl.core.context.BaseContextConstants;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;

/**
 * description: 降级处理类
 *
 * @author zhouxinlei
 */
public class FallBackHandler {

    public static void fallBack() {
        RequestContextHolderUtils.setAttribute(BaseContextConstants.FALLBACK, true);
    }
}
