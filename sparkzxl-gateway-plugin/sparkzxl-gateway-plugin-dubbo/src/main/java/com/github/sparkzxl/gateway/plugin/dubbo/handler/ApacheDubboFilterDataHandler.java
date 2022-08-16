package com.github.sparkzxl.gateway.plugin.dubbo.handler;

import com.github.sparkzxl.gateway.plugin.dubbo.config.ApacheDubboConfigCache;
import com.github.sparkzxl.gateway.plugin.dubbo.config.DubboRegisterConfig;

/**
 * description:  Apache Dubbo Filter Data Handler
 *
 * @author zhouxinlei
 * @since 2022-08-15 15:02:01
 */
public class ApacheDubboFilterDataHandler extends AbstractDubboFilterDataHandler {

    @Override
    protected void initConfigCache(DubboRegisterConfig dubboRegisterConfig) {
        ApacheDubboConfigCache.getInstance().init(dubboRegisterConfig);
        ApacheDubboConfigCache.getInstance().invalidateAll();
    }
}
