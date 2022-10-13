package com.github.sparkzxl.gateway.plugin.dubbo.handler;

import com.github.sparkzxl.gateway.plugin.common.entity.MetaData;
import com.github.sparkzxl.gateway.plugin.dubbo.config.ApacheDubboConfigCache;

import java.util.Objects;

/**
 * description: The type Apache dubbo meta data handler.
 *
 * @author zhouxinlei
 * @since 2022-08-12 15:28:16
 */
public class ApacheDubboMetaDataHandler extends AbstractDubboMetaDataHandler {

    @Override
    protected boolean isInitialized(MetaData metaData) {
        return Objects.nonNull(ApacheDubboConfigCache.getInstance().get(metaData.getPath()));
    }

    @Override
    protected void initReference(MetaData metaData) {
        ApacheDubboConfigCache.getInstance().initRef(metaData);
    }

    @Override
    protected void updateReference(MetaData metaData) {
        ApacheDubboConfigCache.getInstance().build(metaData);
    }

    @Override
    protected void invalidateReference(String path) {
        ApacheDubboConfigCache.getInstance().invalidate(path);
    }
}
