
package com.github.sparkzxl.data.sync.admin.listener.nacos;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.data.sync.admin.handler.MergeDataHandler;
import com.github.sparkzxl.data.sync.common.constant.NacosPathConstants;
import com.github.sparkzxl.data.sync.common.entity.PushData;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

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

    public NacosDataChangedListener(ConfigService configService,
                                    List<MergeDataHandler> mergeDataHandlerList) {
        this.configService = configService;
        for (MergeDataHandler<?> mergeDataHandler : mergeDataHandlerList) {
            mergeDataHandlerMap.put(mergeDataHandler.configGroup(), mergeDataHandler);
        }
    }


    @Override
    public void publishConfig(PushData<?> pushData) {
        try {
            MergeDataHandler mergeDataHandler = mergeDataHandlerMap.get(pushData.getConfigGroup());
            Object configData = mergeDataHandler.handle(pushData);
            configService.publishConfig(pushData.getConfigGroup().concat(".json"), NacosPathConstants.GROUP, JSON.toJSONString(configData));
        } catch (NacosException e) {
            LOG.error("Publish data to nacos error.", e);
            throw new BizException(e.getMessage());
        }
    }
}
