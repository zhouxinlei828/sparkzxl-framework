package com.github.sparkzxl.data.sync.admin.listener.nacos;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.data.sync.admin.config.nacos.NacosWatchProperties;
import com.github.sparkzxl.data.sync.admin.listener.AbstractDataChangedInit;
import com.github.sparkzxl.data.sync.common.constant.NacosPathConstants;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * description: NacosDataChangedInit
 *
 * @author zhouxinlei
 * @since 2022-09-05 16:27:08
 */
public class NacosDataChangedInit extends AbstractDataChangedInit {

    private static final Logger logger = LoggerFactory.getLogger(NacosDataChangedInit.class);

    private final ConfigService configService;
    private final Map<String, String> watchConfigMap = Maps.newConcurrentMap();

    public NacosDataChangedInit(ConfigService configService,
                                List<NacosWatchProperties> watchConfigs) {
        this.configService = configService;
        watchConfigMap.putAll(watchConfigs.stream().collect(Collectors.toMap(NacosWatchProperties::getDataId, NacosWatchProperties::getGroup)));
    }

    @Override
    protected boolean notExist() {
        boolean exists = false;
        for (Map.Entry<String, String> entry : watchConfigMap.entrySet()) {
            exists = dataIdNotExist(entry.getKey(), entry.getValue());
        }
        return exists;
    }

    private boolean dataIdNotExist(final String dataId, String group) {
        try {
            return Objects.isNull(configService.getConfig(dataId, group, NacosPathConstants.DEFAULT_TIME_OUT));
        } catch (NacosException e) {
            logger.error("Get data from nacos error.", e);
            throw new BizException(e.getMessage());
        }
    }

}
