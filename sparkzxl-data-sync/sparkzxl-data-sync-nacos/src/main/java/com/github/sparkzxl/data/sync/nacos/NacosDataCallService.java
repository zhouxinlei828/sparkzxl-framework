package com.github.sparkzxl.data.sync.nacos;

import com.alibaba.nacos.api.config.ConfigService;
import com.github.sparkzxl.data.sync.nacos.constant.NacosPathConstants;
import com.github.sparkzxl.data.sync.nacos.handler.NacosCacheHandler;
import com.github.sparkzxl.data.sync.api.MetaDataSubscriber;
import com.github.sparkzxl.data.sync.api.DataCallService;

import java.util.List;

/**
 * description: The type Nacos sync data service.
 *
 * @author zhouxinlei
 * @since 2022-08-24 14:14:37
 */
public class NacosDataCallService extends NacosCacheHandler implements DataCallService {

    /**
     * Instantiates a new Nacos sync data service.
     *
     * @param configService       the config service
     * @param metaDataSubscribers the meta data subscribers
     */
    public NacosDataCallService(final ConfigService configService, final List<MetaDataSubscriber> metaDataSubscribers) {
        super(configService, metaDataSubscribers);
        start();
    }

    /**
     * Start.
     */
    public void start() {
        watcherData(NacosPathConstants.META_DATA_ID, this::updateMetaDataMap);
    }

    @Override
    public void close() {
        LISTENERS.forEach((dataId, ls) -> {
            ls.forEach(listener -> getConfigService().removeListener(dataId, NacosPathConstants.GROUP, listener));
            ls.clear();
        });
        LISTENERS.clear();
    }
}
