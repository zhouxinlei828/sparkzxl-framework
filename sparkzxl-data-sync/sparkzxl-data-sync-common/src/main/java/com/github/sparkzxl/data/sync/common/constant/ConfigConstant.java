package com.github.sparkzxl.data.sync.common.constant;


import com.github.sparkzxl.core.util.StrPool;

/**
 * description:  Config Constant
 *
 * @author zhouxinlei
 * @since 2022-09-02 16:06:33
 */
public class ConfigConstant {

    public static final String DATA_SYNC_PREFIX = "phoenix.data.sync";

    public static final String DATA_SYNC_PROVIDER_PREFIX = DATA_SYNC_PREFIX + StrPool.DOT + "provider" + StrPool.DOT;

    public static final String DATA_SYNC_CONSUMER_PREFIX = DATA_SYNC_PREFIX + StrPool.DOT + "consumer" + StrPool.DOT;
}
