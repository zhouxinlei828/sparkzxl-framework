package com.github.sparkzxl.data.sync.admin.listener.nacos;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.core.util.StrPool;
import com.github.sparkzxl.data.sync.admin.DataSyncPushType;
import com.github.sparkzxl.data.sync.common.entity.PushData;
import com.google.common.collect.Maps;
import com.github.sparkzxl.data.sync.admin.config.nacos.NacosWatchProperties;
import com.github.sparkzxl.data.sync.admin.handler.MergeDataHandler;
import com.github.sparkzxl.data.sync.admin.listener.AbstractDataChangedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * description: Use nacos to synchronized data changes.
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
        watchConfigMap.putAll(
                watchConfigs.stream().collect(Collectors.toMap(NacosWatchProperties::getDataId, NacosWatchProperties::getGroup)));
    }


    @Override
    public void publishConfig(PushData<?> pushData) {
        try {
            String dataId = pushData.getConfigGroup();
            String group = watchConfigMap.get(dataId);
            MergeDataHandler mergeDataHandler = mergeDataHandlerMap.get(
                    pushData.getConfigGroup().concat(StrPool.COLON).concat(DataSyncPushType.NACOS.name().toLowerCase(Locale.ROOT)));
            Object configData = mergeDataHandler.handle(pushData);
            configService.publishConfig(dataId, group, JsonUtils.getJson().toJsonPretty(configData), ConfigType.JSON.getType());
        } catch (NacosException e) {
            LOG.error("Publish data to nacos error.", e);
            throw new BizException(e.getMessage());
        }
    }
}
