package com.guthub.sparkzxl.data.sync.websocket.config;

import com.github.sparkzxl.data.sync.api.DataSyncService;
import com.github.sparkzxl.data.sync.api.MetaDataSubscriber;
import com.github.sparkzxl.data.sync.common.constant.ConfigConstant;
import com.guthub.sparkzxl.data.sync.websocket.WebsocketDataSyncServiceImpl;
import com.guthub.sparkzxl.data.sync.websocket.handler.DataHandler;
import com.guthub.sparkzxl.data.sync.websocket.handler.MetaDataHandler;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-09-02 09:17:42
 */
@Configuration
@EnableConfigurationProperties(WebsocketConsumerProperties.class)
@ConditionalOnProperty(prefix = ConfigConstant.DATA_SYNC_CONSUMER_PREFIX + "websocket", name = "urls")
public class WebsocketDataSyncConsumerAutoConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketDataSyncConsumerAutoConfig.class);


    @Bean
    public MetaDataHandler metaDataHandler(final ObjectProvider<List<MetaDataSubscriber>> subscribers) {
        return new MetaDataHandler(subscribers.getIfAvailable(Collections::emptyList));
    }

    /**
     * Websocket data call service.
     *
     * @param websocketConsumerProperties the websocket config
     * @param dataHandlerList             the dataHandlers
     * @return DataCallService
     */
    @Bean
    public DataSyncService websocketDataCallService(final ObjectProvider<WebsocketConsumerProperties> websocketConsumerProperties,
            final ObjectProvider<List<DataHandler>> dataHandlerList) {
        logger.info("websocket data sync initialization.");
        return new WebsocketDataSyncServiceImpl(websocketConsumerProperties.getIfAvailable(WebsocketConsumerProperties::new),
                dataHandlerList.getIfAvailable(Collections::emptyList));
    }

    /**
     * Config websocket config.
     *
     * @return the websocket config
     */
    @Bean
    public WebsocketConsumerProperties websocketConsumerProperties() {
        return new WebsocketConsumerProperties();
    }

}
