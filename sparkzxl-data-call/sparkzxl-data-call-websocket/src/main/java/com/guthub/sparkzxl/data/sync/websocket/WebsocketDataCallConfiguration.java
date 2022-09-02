package com.guthub.sparkzxl.data.sync.websocket;

import com.github.sparkzxl.sync.data.api.MetaDataSubscriber;
import com.github.sparkzxl.sync.data.api.DataCallService;
import com.guthub.sparkzxl.data.sync.websocket.config.WebsocketClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-09-02 09:17:42
 */
@Configuration
@EnableConfigurationProperties(WebsocketClientConfig.class)
@ConditionalOnProperty(prefix = "sparkzxl.data.call.websocket", name = "urls")
public class WebsocketDataCallConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketDataCallConfiguration.class);

    /**
     * Websocket data call service.
     *
     * @param websocketConfig the websocket config
     * @param metaSubscribers the meta subscribers
     * @return DataCallService
     */
    @Bean
    public DataCallService websocketDataCallService(final ObjectProvider<WebsocketClientConfig> websocketConfig,
                                                    final ObjectProvider<List<MetaDataSubscriber>> metaSubscribers) {
        logger.info("you use websocket data called.......");
        return new WebsocketDataCallServiceImpl(websocketConfig.getIfAvailable(WebsocketClientConfig::new),
                metaSubscribers.getIfAvailable(Collections::emptyList));
    }

    /**
     * Config websocket config.
     *
     * @return the websocket config
     */
    @Bean
    public WebsocketClientConfig websocketConfig() {
        return new WebsocketClientConfig();
    }

}
