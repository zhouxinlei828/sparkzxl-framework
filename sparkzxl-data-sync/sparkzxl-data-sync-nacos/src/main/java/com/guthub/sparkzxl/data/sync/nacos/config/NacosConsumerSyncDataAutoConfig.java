package com.guthub.sparkzxl.data.sync.nacos.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.github.sparkzxl.data.sync.api.DataSubscriber;
import com.github.sparkzxl.data.sync.api.DataSyncService;
import com.github.sparkzxl.data.sync.common.constant.ConfigConstant;
import com.guthub.sparkzxl.data.sync.nacos.NacosDataSyncServiceImpl;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: Nacos sync data configuration for spring boot.
 *
 * @author zhouxinlei
 * @since 2022-09-06 10:11:26
 */
@Configuration
@EnableConfigurationProperties(NacosConsumerProperties.class)
@ConditionalOnProperty(prefix = ConfigConstant.DATA_SYNC_CONSUMER_PREFIX + "nacos", name = "url")
public class NacosConsumerSyncDataAutoConfig {

    private static final Logger logger = LoggerFactory.getLogger(NacosConsumerSyncDataAutoConfig.class);

    /**
     * Nacos sync data service.
     *
     * @param configService   the config service
     * @param dataSubscribers the data subscribers
     * @return the sync data service
     */
    @Bean
    public DataSyncService nacosSyncDataService(final ObjectProvider<ConfigService> configService,
            final ObjectProvider<List<DataSubscriber>> dataSubscribers,
            final NacosConsumerProperties nacosConsumerProperties) {
        logger.info("nacos sync data initialization.");
        return new NacosDataSyncServiceImpl(configService.getIfAvailable(),
                dataSubscribers.getIfAvailable(Collections::emptyList), nacosConsumerProperties.getWatchConfigs());
    }

    /**
     * Nacos config service.
     *
     * @param nacosConsumerProperties the nacos config
     * @return the config service
     * @throws Exception the exception
     */
    @Bean
    public ConfigService nacosConfigService(final NacosConsumerProperties nacosConsumerProperties) throws Exception {
        Properties properties = new Properties();
        if (nacosConsumerProperties.getAcm() != null && nacosConsumerProperties.getAcm().isEnabled()) {
            properties.put(PropertyKeyConst.ENDPOINT, nacosConsumerProperties.getAcm().getEndpoint());
            properties.put(PropertyKeyConst.NAMESPACE, nacosConsumerProperties.getAcm().getNamespace());
            properties.put(PropertyKeyConst.ACCESS_KEY, nacosConsumerProperties.getAcm().getAccessKey());
            properties.put(PropertyKeyConst.SECRET_KEY, nacosConsumerProperties.getAcm().getSecretKey());
        } else {
            properties.put(PropertyKeyConst.SERVER_ADDR, nacosConsumerProperties.getUrl());
            if (StringUtils.isNotBlank(nacosConsumerProperties.getNamespace())) {
                properties.put(PropertyKeyConst.NAMESPACE, nacosConsumerProperties.getNamespace());
            }
            if (nacosConsumerProperties.getUsername() != null) {
                properties.put(PropertyKeyConst.USERNAME, nacosConsumerProperties.getUsername());
            }
            if (nacosConsumerProperties.getPassword() != null) {
                properties.put(PropertyKeyConst.PASSWORD, nacosConsumerProperties.getPassword());
            }
        }
        return NacosFactory.createConfigService(properties);
    }
}
