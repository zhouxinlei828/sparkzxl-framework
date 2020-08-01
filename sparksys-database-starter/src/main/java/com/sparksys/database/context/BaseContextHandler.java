package com.sparksys.database.context;

import com.sparksys.core.constant.BaseContextConstants;
import com.sparksys.core.utils.HttpCommonUtils;

/**
 * description: 上下文全局获取
 *
 * @author: zhouxinlei
 * @date: 2020-07-29 13:40:55
 */
public class BaseContextHandler {

    public static Long getUserId() {
        return (Long) HttpCommonUtils.getAttribute(BaseContextConstants.APPLICATION_AUTH_USER_ID);
    }

    public static String getAccount() {
        return HttpCommonUtils.getAttributeStr(BaseContextConstants.APPLICATION_AUTH_ACCOUNT);
    }

    public static String getUserName() {
        return HttpCommonUtils.getAttributeStr(BaseContextConstants.APPLICATION_AUTH_NAME);
    }

}
