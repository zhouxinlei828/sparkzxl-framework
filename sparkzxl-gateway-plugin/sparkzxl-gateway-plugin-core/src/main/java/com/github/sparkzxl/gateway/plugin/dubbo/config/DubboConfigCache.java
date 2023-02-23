package com.github.sparkzxl.gateway.plugin.dubbo.config;

import com.github.sparkzxl.core.json.JsonUtils;

/**
 * description: DubboConfigCache
 *
 * @author zhouxinlei
 * @since 2022-08-12 15:05:58
 */
public class DubboConfigCache {

    /**
     * parser the rpc ext to dubbo param.
     *
     * @param rpcExt the rpc ext
     * @return parsed dubbo param
     */
    protected DubboParam parserToDubboParam(final String rpcExt) {
        return JsonUtils.getJson().toJavaObject(rpcExt, DubboParam.class);
    }

}
