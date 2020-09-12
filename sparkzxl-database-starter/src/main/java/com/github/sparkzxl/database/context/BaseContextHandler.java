package com.github.sparkzxl.database.context;

import com.github.sparkzxl.core.constant.BaseContextConstant;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;

/**
 * description: 上下文全局获取
 *
 * @author: zhouxinlei
 * @date: 2020-07-29 13:40:55
 */
public class BaseContextHandler {

    public static Long getUserId() {
        return (Long) RequestContextHolderUtils.getAttribute(BaseContextConstant.APPLICATION_AUTH_USER_ID);
    }

    public static String getAccount() {
        return RequestContextHolderUtils.getAttributeStr(BaseContextConstant.APPLICATION_AUTH_ACCOUNT);
    }

    public static String getUserName() {
        return RequestContextHolderUtils.getAttributeStr(BaseContextConstant.APPLICATION_AUTH_NAME);
    }

}
