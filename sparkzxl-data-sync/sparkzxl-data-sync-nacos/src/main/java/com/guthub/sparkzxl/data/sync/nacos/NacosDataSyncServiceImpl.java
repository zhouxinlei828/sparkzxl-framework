package com.guthub.sparkzxl.data.sync.nacos;

import com.alibaba.nacos.api.config.ConfigService;
import com.github.sparkzxl.data.sync.api.DataSubscriber;
import com.github.sparkzxl.data.sync.api.DataSyncService;
import com.google.common.collect.Maps;
import com.guthub.sparkzxl.data.sync.nacos.config.NacosWatchProperties;
import com.guthub.sparkzxl.data.sync.nacos.handler.NacosCacheHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * description: The type Nacos sync data service.
 *
 * @author zhouxinlei
 * @since 2022-09-06 10:05:07
 */
public class NacosDataSyncServiceImpl extends NacosCacheHandler implements DataSyncService {

    private final Map<String, String> watchConfigMap = Maps.newConcurrentMap();

    /**
     * Instantiates a new Nacos sync data service.
     *
     * @param configService   the config service
     * @param dataSubscribers the data subscribers
     * @param watchConfigs    the nacos data group
     * @param watchConfigs
     */
    public NacosDataSyncServiceImpl(final ConfigService configService,
                                    final List<DataSubscriber> dataSubscribers,
                                    List<NacosWatchProperties> watchConfigs) {
        super(configService, dataSubscribers);
        watchConfigMap.putAll(watchConfigs.stream().collect(Collectors.toMap(NacosWatchProperties::getDataId, NacosWatchProperties::getGroup)));
        start();
    }

    /**
     * Start.
     */
    public void start() {
        for (Map.Entry<String, String> entry : watchConfigMap.entrySet()) {
            watcherData(entry.getKey(), entry.getValue(), this::updateDataMap);
        }
    }

    @Override
    public void close() {
        LISTENERS.forEach((dataId, lss) -> {
            lss.forEach(listener -> {
                String group = watchConfigMap.get(dataId);
                getConfigService().removeListener(dataId, group, listener);
            });
            lss.clear();
        });
        LISTENERS.clear();
    }
}
