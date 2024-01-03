package com.github.sparkzxl.data.sync.websocket.config;

import com.github.sparkzxl.data.sync.api.BusinessDataSubscriber;
import com.github.sparkzxl.data.sync.api.MetaDataSubscriber;
import com.github.sparkzxl.data.sync.common.constant.ConfigConstant;
import com.github.sparkzxl.data.sync.websocket.WebsocketDataSyncService;
import com.github.sparkzxl.data.sync.websocket.WebsocketDataSyncServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

/**
 * description: websocket data sync consumer autoconfig
 *
 * @author zhouxinlei
 * @since 2022-09-02 09:17:42
 */
@Configuration
@EnableScheduling
@EnableConfigurationProperties(WebsocketConsumerProperties.class)
@ConditionalOnProperty(prefix = ConfigConstant.DATA_SYNC_CONSUMER_PREFIX + "websocket", name = "enabled", havingValue = "true")
public class WebsocketConsumerAutoConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketConsumerAutoConfig.class);

    /**
     * Websocket data call service.
     *
     * @return DataCallService
     */
    @Bean
    public WebsocketDataSyncService websocketDataSyncService(DiscoveryClient discoveryClient,
                                                             WebsocketConsumerProperties websocketConsumerProperties,
                                                             List<MetaDataSubscriber> metaDataSubscribers,
                                                             List<BusinessDataSubscriber> businessDataSubscribers) {
        logger.info("websocket data sync initialization.");
        return new WebsocketDataSyncServiceImpl(discoveryClient, websocketConsumerProperties, metaDataSubscribers, businessDataSubscribers);
    }

}
