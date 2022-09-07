package com.github.sparkzxl.data.sync.admin.listener.nacos;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.data.sync.admin.config.NacosWatchProperties;
import com.github.sparkzxl.data.sync.admin.handler.MergeDataHandler;
import com.github.sparkzxl.data.sync.common.constant.NacosPathConstants;
import com.github.sparkzxl.data.sync.common.entity.PushData;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * description: Use nacos to push data changes.
 *
 * @author zhouxinlei
 * @since 2022-09-05 10:06:15
 */
public class NacosDataChangedListener extends AbstractDataChangedListener {

    private static final Logger LOG = LoggerFactory.getLogger(NacosDataChangedListener.class);

    private final ConfigService configService;
    private final Map<String, MergeDataHandler> mergeDataHandlerMap = Maps.newConcurrentMap();
    private final Map<String, String> watchConfigMap = Maps.newConcurrentMap();

    public NacosDataChangedListener(ConfigService configService,
                                    List<MergeDataHandler> mergeDataHandlerList,
                                    List<NacosWatchProperties> watchConfigs) {
        this.configService = configService;
        mergeDataHandlerList.forEach(mergeDataHandler -> mergeDataHandlerMap.put(mergeDataHandler.configGroup(), mergeDataHandler));
        watchConfigMap.putAll(watchConfigs.stream().collect(Collectors.toMap(NacosWatchProperties::getDataId, NacosWatchProperties::getGroup)));
    }


    @Override
    public void publishConfig(PushData<?> pushData) {
        try {
            String dataId = pushData.getConfigGroup();
            String group = watchConfigMap.get(dataId);
            MergeDataHandler mergeDataHandler = mergeDataHandlerMap.get(pushData.getConfigGroup());
            Object configData = mergeDataHandler.handle(pushData);
            configService.publishConfig(dataId, group, JSON.toJSONString(configData, SerializerFeature.PrettyFormat), ConfigType.JSON.getType());
        } catch (NacosException e) {
            LOG.error("Publish data to nacos error.", e);
            throw new BizException(e.getMessage());
        }
    }
}
