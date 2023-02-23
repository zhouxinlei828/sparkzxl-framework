package com.github.sparkzxl.feign.util;

import com.github.sparkzxl.core.constant.BaseContextConstants;
import com.github.sparkzxl.core.util.RequestContextUtils;

/**
 * description: 降级处理工具类
 *
 * @author zhouxinlei
 */
public class ServiceDegradeUtils {

    public static void fallBack() {
        RequestContextUtils.setAttribute(BaseContextConstants.FALLBACK, Boolean.TRUE);
    }
}
