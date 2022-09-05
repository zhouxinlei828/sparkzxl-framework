package com.github.sparkzxl.data.sync.admin.handler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.core.util.KeyGeneratorUtil;
import com.github.sparkzxl.data.sync.common.constant.NacosPathConstants;
import com.github.sparkzxl.data.sync.common.entity.MetaData;
import com.github.sparkzxl.data.sync.common.entity.PushData;
import com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum;
import com.github.sparkzxl.data.sync.common.enums.DataEventTypeEnum;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * description: 元数据合并处理
 *
 * @author zhouxinlei
 * @since 2022-09-05 14:50:02
 */
public class MetaMergeDataHandler implements MergeDataHandler<MetaData> {

    private static final Logger logger = LoggerFactory.getLogger(MetaMergeDataHandler.class);

    private final ConfigService configService;
    private static final ConcurrentMap<String, MetaData> META_DATA = Maps.newConcurrentMap();

    public MetaMergeDataHandler(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public Object handle(PushData<MetaData> pushData) {
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
        return ConfigGroupEnum.META_DATA.name();
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

    private String getConfig(final String dataId) {
        try {
            String config = configService.getConfig(dataId.concat(".json"), NacosPathConstants.GROUP, NacosPathConstants.DEFAULT_TIME_OUT);
            return StringUtils.hasLength(config) ? config : NacosPathConstants.EMPTY_CONFIG_DEFAULT_VALUE;
        } catch (NacosException e) {
            logger.error("Get data from nacos error.", e);
            throw new BizException(e.getMessage());
        }
    }


    private String getKey(MetaData metaData) {
        return KeyGeneratorUtil.generateKey("{}-{}-{}-{}",
                metaData.getTenantId(), metaData.getAreaCode(),
                metaData.getDataType(), metaData.getDataValue());
    }

}
