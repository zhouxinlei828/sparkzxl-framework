package com.github.sparkzxl.data.sync.admin.handler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.core.util.StrPool;
import com.github.sparkzxl.data.sync.admin.DataSyncPushType;
import com.github.sparkzxl.data.sync.admin.config.nacos.NacosWatchProperties;
import com.github.sparkzxl.data.sync.common.constant.NacosPathConstants;
import com.github.sparkzxl.data.sync.common.entity.MetaData;
import com.github.sparkzxl.data.sync.common.entity.PushData;
import com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum;
import com.github.sparkzxl.data.sync.common.enums.DataEventTypeEnum;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * description: Nacos元数据合并处理
 *
 * @author zhouxinlei
 * @since 2022-09-05 14:50:02
 */
public class NacosMetaMergeDataHandler implements MergeDataHandler<MetaData> {

    private static final Logger logger = LoggerFactory.getLogger(NacosMetaMergeDataHandler.class);

    private final ConfigService configService;
    private final Map<String, String> watchConfigMap = Maps.newConcurrentMap();
    private static final ConcurrentMap<String, MetaData> META_DATA = Maps.newConcurrentMap();

    public NacosMetaMergeDataHandler(ConfigService configService,
                                     List<NacosWatchProperties> watchConfigs) {
        this.configService = configService;
        watchConfigMap.putAll(watchConfigs.stream().collect(Collectors.toMap(NacosWatchProperties::getDataId, NacosWatchProperties::getGroup)));
    }

    @Override
    public Map<String, MetaData> handle(PushData<MetaData> pushData) {
        updateMetaDataMap(getConfig(pushData.getConfigGroup()));
        List<MetaData> dataList = pushData.getData();
        DataEventTypeEnum eventType = DataEventTypeEnum.acquireByName(pushData.getEventType());
        switch (eventType) {
            case DELETE:
                dataList.forEach(meta -> {
                    String key = getKey(meta);
                    META_DATA.remove(key);
                });
                break;
            case REFRESH:
            case MYSELF:
                Set<String> set = new HashSet<>(META_DATA.keySet());
                dataList.forEach(meta -> {
                    String key = getKey(meta);
                    set.remove(key);
                    META_DATA.put(key, meta);
                });
                META_DATA.keySet().removeAll(set);
                break;
            default:
                dataList.forEach(meta -> {
                    String key = getKey(meta);
                    META_DATA
                            .values()
                            .stream()
                            .filter(md -> Objects.equals(md.getId(), meta.getId()))
                            .forEach(md -> META_DATA.remove(key));
                    META_DATA.put(key, meta);
                });
                break;
        }
        return META_DATA;
    }

    @Override
    public String configGroup() {
        return ConfigGroupEnum.META_DATA.getCode().concat(StrPool.COLON).concat(DataSyncPushType.NACOS.name().toLowerCase(Locale.ROOT));
    }

    private void updateMetaDataMap(final String configInfo) {
        JSONObject jsonObject = JSONObject.parseObject(configInfo);
        Set<String> set = new HashSet<>(META_DATA.keySet());
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            set.remove(entry.getKey());
            META_DATA.put(entry.getKey(), JSONObject.parseObject(JSONObject.toJSONString(entry.getValue()), MetaData.class));
        }
        META_DATA.keySet().removeAll(set);
    }

    private String getConfig(String dataId) {
        try {
            String group = watchConfigMap.get(dataId);
            String config = configService.getConfig(dataId, group, NacosPathConstants.DEFAULT_TIME_OUT);
            return StringUtils.isNotEmpty(config) ? config : NacosPathConstants.EMPTY_CONFIG_DEFAULT_VALUE;
        } catch (NacosException e) {
            logger.error("Get data from nacos error.", e);
            throw new BizException(e.getMessage());
        }
    }

    private String getKey(MetaData metaData) {
        return MessageFormat.format("{0}-{1}-{2}-{3}",
                StringUtils.isEmpty(metaData.getTenantId()) ? "default" : metaData.getTenantId(),
                StringUtils.isEmpty(metaData.getAreaCode()) ? "default" : metaData.getAreaCode(),
                metaData.getDataType(),
                metaData.getDataValue());
    }

}
