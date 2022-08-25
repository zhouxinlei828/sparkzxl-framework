package com.guthub.sparkzxl.data.sync.websocket;

import com.github.sparkzxl.sync.data.api.MetaDataSubscriber;
import com.github.sparkzxl.sync.data.api.DataCallService;
import com.guthub.sparkzxl.data.sync.websocket.config.WebsocketConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableConfigurationProperties(WebsocketConfig.class)
@ConditionalOnProperty(prefix = "sparkzxl.data.call.websocket", name = "urls")
public class WebsocketSyncDataConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketSyncDataConfiguration.class);

    /**
     * Websocket sync data service.
     *
     * @param websocketConfig the websocket config
     * @param metaSubscribers the meta subscribers
     * @return the sync data service
     */
    @Bean
    public DataCallService websocketSyncDataService(final ObjectProvider<WebsocketConfig> websocketConfig,
                                                    final ObjectProvider<List<MetaDataSubscriber>> metaSubscribers) {
        logger.info("you use websocket sync shenyu data.......");
        return new WebsocketDataCallServiceImpl(websocketConfig.getIfAvailable(WebsocketConfig::new),
                metaSubscribers.getIfAvailable(Collections::emptyList));
    }

    /**
     * Config websocket config.
     *
     * @return the websocket config
     */
    @Bean
    public WebsocketConfig websocketConfig() {
        return new WebsocketConfig();
    }

}
