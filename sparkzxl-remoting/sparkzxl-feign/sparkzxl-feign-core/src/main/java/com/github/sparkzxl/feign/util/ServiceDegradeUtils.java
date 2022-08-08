package com.github.sparkzxl.feign.util;

import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.util.RequestContextHolderUtils;

/**
 * description: 降级处理工具类
 *
 * @author zhouxinlei
 */
public class ServiceDegradeUtils {

    public static void fallBack() {
        RequestContextHolderUtils.setAttribute(BaseContextConstants.FALLBACK, Boolean.TRUE);
    }
}
