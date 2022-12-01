package com.github.sparkzxl.gateway.plugin.dubbo.handler;

import com.github.sparkzxl.gateway.common.constant.enums.RpcTypeEnum;
import com.github.sparkzxl.gateway.common.entity.MetaData;
import com.github.sparkzxl.gateway.plugin.handler.MetaDataHandler;
import com.google.common.collect.Maps;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * description:  The common dubbo meta data handler.
 *
 * @author zhouxinlei
 * @since 2022-08-12 15:26:49
 */
public abstract class AbstractDubboMetaDataHandler implements MetaDataHandler {

    private static final ConcurrentMap<String, MetaData> META_DATA = Maps.newConcurrentMap();

    @Override
    public void handle(MetaData metaData) {
        MetaData exist = META_DATA.get(metaData.getPath());
        if (Objects.isNull(exist) || !isInitialized(metaData)) {
            // The first initialization
            initReference(metaData);
        } else {
            // There are updates, which only support the update of four properties of serviceName rpcExt parameterTypes methodName,
            // because these four properties will affect the call of Dubbo;
            if (!Objects.equals(metaData.getServiceName(), exist.getServiceName())
                    || !Objects.equals(metaData.getRpcExt(), exist.getRpcExt())
                    || !Objects.equals(metaData.getParameterTypes(), exist.getParameterTypes())
                    || !Objects.equals(metaData.getMethodName(), exist.getMethodName())) {
                updateReference(metaData);
            }
        }
        META_DATA.put(metaData.getPath(), metaData);
    }


    /**
     * 是否初始化
     *
     * @param metaData 元数据
     * @return boolean
     */
    protected abstract boolean isInitialized(MetaData metaData);

    /**
     * 初始化
     *
     * @param metaData 元数据
     */
    protected abstract void initReference(MetaData metaData);

    /**
     * 更新
     *
     * @param metaData 元数据
     */
    protected abstract void updateReference(MetaData metaData);


    @Override
    public void remove(MetaData metaData) {
        invalidateReference(metaData.getPath());
        META_DATA.remove(metaData.getPath());
    }

    /**
     * 失效
     *
     * @param path
     */
    protected abstract void invalidateReference(String path);

    @Override
    public String rpcType() {
        return RpcTypeEnum.DUBBO.getName();
    }
}
