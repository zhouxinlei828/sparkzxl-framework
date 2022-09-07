package com.guthub.sparkzxl.data.sync.nacos.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.github.sparkzxl.data.sync.api.DataSubscriber;
import com.github.sparkzxl.data.sync.common.constant.NacosPathConstants;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * description: Nacos cache handler.
 *
 * @author zhouxinlei
 * @since 2022-09-06 10:00:54
 */
public class NacosCacheHandler {

    protected static final Map<String, List<Listener>> LISTENERS = Maps.newConcurrentMap();

    private static final Logger logger = LoggerFactory.getLogger(NacosCacheHandler.class);

    private final ConfigService configService;

    private final Map<String, List<DataSubscriber>> dataSubscriberMap = Maps.newConcurrentMap();


    public NacosCacheHandler(final ConfigService configService,
                             final List<DataSubscriber> dataSubscribers) {
        this.configService = configService;
        dataSubscriberMap.putAll(dataSubscribers.stream().collect(Collectors.groupingBy(DataSubscriber::group)));
    }

    /**
     * get configService.
     *
     * @return configService
     */
    public ConfigService getConfigService() {
        return this.configService;
    }


    protected void updateDataMap(final String dataId, final String configInfo) {
        try {
            List<DataSubscriber> subscribers = dataSubscriberMap.get(dataId);
            JSONArray jsonArray = JSONArray.parseArray(configInfo);
            jsonArray.forEach(metaData -> subscribers.forEach(subscriber -> {
                subscriber.unSubscribe(metaData);
                subscriber.onSubscribe(metaData);
            }));
        } catch (Exception e) {
            logger.error("sync meta data have error:", e);
        }
    }

    private String getConfigAndSignListener(final String dataId, final String group, final Listener listener) {
        String config = null;
        try {
            config = configService.getConfigAndSignListener(dataId, group, 6000, listener);
        } catch (NacosException e) {
            logger.error(e.getMessage(), e);
        }
        if (Objects.isNull(config)) {
            config = "{}";
        }
        return config;
    }

    protected void watcherData(final String dataId, final String group, final OnChange oc) {
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
        oc.change(getConfigAndSignListener(dataId, group, listener));
        LISTENERS.computeIfAbsent(dataId, key -> new ArrayList<>()).add(listener);
    }

    protected interface OnChange {
        void change(String changeData);
    }
}
