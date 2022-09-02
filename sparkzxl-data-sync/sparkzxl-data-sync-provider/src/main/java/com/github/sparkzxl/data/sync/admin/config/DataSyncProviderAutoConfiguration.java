package com.github.sparkzxl.data.sync.admin.config;

import com.github.sparkzxl.data.sync.admin.DataChangedEventDispatcher;
import com.github.sparkzxl.data.sync.admin.ProviderStartRunner;
import com.github.sparkzxl.data.sync.admin.listener.DataChangedListener;
import com.github.sparkzxl.data.sync.admin.listener.websocket.WebsocketCollector;
import com.github.sparkzxl.data.sync.admin.listener.websocket.WebsocketDataChangedListener;
import com.github.sparkzxl.data.sync.common.constant.ConfigConstant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * description: The type Data sync configuration.
 *
 * @author zhouxinlei
 * @since 2022-08-25 15:19:14
 */
@Configuration
@EnableConfigurationProperties(value = {DataSyncProviderProperties.class, WebsocketProviderProperties.class})
public class DataSyncProviderAutoConfiguration {

    @Bean
    public ProviderStartRunner providerStartRunner(final ApplicationContext applicationContext) {
        return new ProviderStartRunner(applicationContext);
    }

    @Bean
    public DataChangedEventDispatcher getDataChangedEventDispatcher(ApplicationContext applicationContext) {
        return new DataChangedEventDispatcher(applicationContext);
    }

    /**
     * The WebsocketListener(default strategy).
     */
    @Configuration
    @ConditionalOnProperty(name = ConfigConstant.DATA_SYNC_PROVIDER_PREFIX + "websocket.enabled", havingValue = "true", matchIfMissing = true)
    static class WebsocketListener {

        /**
         * Config event listener data changed listener.
         *
         * @return the data changed listener
         */
        @Bean
        @ConditionalOnMissingBean(WebsocketDataChangedListener.class)
        public DataChangedListener websocketDataChangedListener() {
            return new WebsocketDataChangedListener();
        }

        /**
         * Websocket collector.
         *
         * @return the websocket collector
         */
        @Bean
        @ConditionalOnMissingBean(WebsocketCollector.class)
        public WebsocketCollector websocketCollector() {
            return new WebsocketCollector();
        }

        /**
         * Server endpoint exporter server endpoint exporter.
         *
         * @return the server endpoint exporter
         */
        @Bean
        @ConditionalOnMissingBean(ServerEndpointExporter.class)
        public ServerEndpointExporter serverEndpointExporter() {
            return new ServerEndpointExporter();
        }
    }
}

