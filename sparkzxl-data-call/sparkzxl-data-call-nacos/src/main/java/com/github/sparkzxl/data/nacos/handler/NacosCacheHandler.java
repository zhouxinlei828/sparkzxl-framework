package com.github.sparkzxl.data.nacos.handler;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.data.nacos.constant.NacosPathConstants;
import com.github.sparkzxl.sync.data.api.MetaDataSubscriber;
import com.github.sparkzxl.data.common.entity.MetaData;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * description:  Nacos cache handler.
 *
 * @author zhouxinlei
 * @since 2022-08-24 14:05:11
 */
public class NacosCacheHandler {

    protected static final Map<String, List<Listener>> LISTENERS = Maps.newConcurrentMap();

    private static final Logger logger = LoggerFactory.getLogger(NacosCacheHandler.class);

    private final ConfigService configService;

    private final List<MetaDataSubscriber> metaDataSubscribers;

    public NacosCacheHandler(ConfigService configService,
                             List<MetaDataSubscriber> metaDataSubscribers) {
        this.configService = configService;
        this.metaDataSubscribers = metaDataSubscribers;
    }

    /**
     * get configService.
     *
     * @return configService
     */
    public ConfigService getConfigService() {
        return this.configService;
    }


    protected void updateMetaDataMap(final String configInfo) {
        List<MetaData> metaDataList = new ArrayList<>(JsonUtil.toMap(configInfo, MetaData.class).values());
        metaDataList.forEach(metaData -> metaDataSubscribers.forEach(subscriber -> {
            subscriber.unSubscribe(metaData);
            subscriber.onSubscribe(metaData);
        }));
    }

    protected void watcherData(final String dataId, final OnChange oc) {
        Listener listener = new Listener() {
            @Override
            public void receiveConfigInfo(final String configInfo) {
                oc.change(configInfo);
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        };
        oc.change(getConfigAndSignListener(dataId, listener));
        LISTENERS.computeIfAbsent(dataId, key -> new ArrayList<>()).add(listener);
    }

    private String getConfigAndSignListener(final String dataId, final Listener listener) {
        String config = null;
        try {
            config = configService.getConfigAndSignListener(dataId, NacosPathConstants.GROUP, 6000, listener);
        } catch (NacosException e) {
            logger.error(e.getMessage(), e);
        }
        if (Objects.isNull(config)) {
            config = "{}";
        }
        return config;
    }

    protected interface OnChange {
        void change(String changeData);
    }

}
