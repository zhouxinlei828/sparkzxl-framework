package com.github.sparkzxl.feign.utils;

import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;

/**
 * description: 降级处理工具类
 *
 * @author zhouxinlei
 */
public class FallBackHandlerTool {

    public static void fallBack() {
        RequestContextHolderUtils.setAttribute(BaseContextConstants.FALLBACK, true);
    }
}
