package com.github.sparkzxl.data.sync.admin.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.github.sparkzxl.data.sync.admin.DataChangedEventDispatcher;
import com.github.sparkzxl.data.sync.admin.ProviderStartRunner;
import com.github.sparkzxl.data.sync.admin.config.condition.ZookeeperCondition;
import com.github.sparkzxl.data.sync.admin.config.nacos.NacosProviderProperties;
import com.github.sparkzxl.data.sync.admin.config.websocket.WebsocketProviderProperties;
import com.github.sparkzxl.data.sync.admin.config.zookeeper.CuratorProperties;
import com.github.sparkzxl.data.sync.admin.handler.MergeDataHandler;
import com.github.sparkzxl.data.sync.admin.handler.NacosMetaMergeDataHandler;
import com.github.sparkzxl.data.sync.admin.handler.ZkMetaMergeDataHandler;
import com.github.sparkzxl.data.sync.admin.listener.DataChangedInit;
import com.github.sparkzxl.data.sync.admin.listener.DataChangedListener;
import com.github.sparkzxl.data.sync.admin.listener.nacos.NacosDataChangedInit;
import com.github.sparkzxl.data.sync.admin.listener.nacos.NacosDataChangedListener;
import com.github.sparkzxl.data.sync.admin.listener.websocket.WebsocketCollector;
import com.github.sparkzxl.data.sync.admin.listener.websocket.WebsocketDataChangedListener;
import com.github.sparkzxl.data.sync.admin.listener.zookeeper.ZookeeperDataChangedInit;
import com.github.sparkzxl.data.sync.admin.listener.zookeeper.ZookeeperDataChangedListener;
import com.github.sparkzxl.data.sync.common.constant.ConfigConstant;
import com.github.sparkzxl.data.sync.common.entity.MetaData;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * description: The type Data sync configuration.
 *
 * @author zhouxinlei
 * @since 2022-08-25 15:19:14
 */
@Configuration
@EnableConfigurationProperties(value = {DataSyncProviderProperties.class, WebsocketProviderProperties.class, NacosProviderProperties.class})
public class DataSyncProviderAutoConfig {

    @Bean
    public ProviderStartRunner providerStartRunner(final ApplicationContext applicationContext) {
        return new ProviderStartRunner(applicationContext);
    }

    @Bean
    public DataChangedEventDispatcher getDataChangedEventDispatcher(ApplicationContext applicationContext) {
        return new DataChangedEventDispatcher(applicationContext);
    }

    /**
     * description: The WebsocketListener(default strategy).
     *
     * @author zhouxinlei
     * @since 2022-09-05 09:59:26
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

    /**
     * description: The type Nacos listener.
     *
     * @author zhouxinlei
     * @since 2022-09-05 09:59:16
     */
    @Configuration
    @ConditionalOnProperty(prefix = ConfigConstant.DATA_SYNC_PROVIDER_PREFIX + "nacos", name = "url")
    static class NacosListener {

        /**
         * register configService in spring ioc.
         *
         * @param nacosProviderProperties the nacos configuration
         * @return ConfigService {@linkplain ConfigService}
         * @throws Exception the exception
         */
        @Bean
        @ConditionalOnMissingBean(ConfigService.class)
        public ConfigService nacosConfigService(final NacosProviderProperties nacosProviderProperties) throws Exception {
            Properties properties = new Properties();
            if (Objects.nonNull(nacosProviderProperties.getAcm()) && nacosProviderProperties.getAcm().isEnabled()) {
                // Use aliyun ACM service
                properties.put(PropertyKeyConst.ENDPOINT, nacosProviderProperties.getAcm().getEndpoint());
                properties.put(PropertyKeyConst.NAMESPACE, nacosProviderProperties.getAcm().getNamespace());
                // Use subaccount ACM administrative authority
                properties.put(PropertyKeyConst.ACCESS_KEY, nacosProviderProperties.getAcm().getAccessKey());
                properties.put(PropertyKeyConst.SECRET_KEY, nacosProviderProperties.getAcm().getSecretKey());
            } else {
                properties.put(PropertyKeyConst.SERVER_ADDR, nacosProviderProperties.getUrl());
                if (StringUtils.isNotBlank(nacosProviderProperties.getNamespace())) {
                    properties.put(PropertyKeyConst.NAMESPACE, nacosProviderProperties.getNamespace());
                }
                if (StringUtils.isNotBlank(nacosProviderProperties.getUsername())) {
                    properties.put(PropertyKeyConst.USERNAME, nacosProviderProperties.getUsername());
                }
                if (StringUtils.isNotBlank(nacosProviderProperties.getPassword())) {
                    properties.put(PropertyKeyConst.PASSWORD, nacosProviderProperties.getPassword());
                }
            }
            return NacosFactory.createConfigService(properties);
        }

        /**
         * Nacos data init nacos data init.
         *
         * @param configService the config service
         * @return the nacos data init
         */
        @Bean
        @ConditionalOnMissingBean(NacosDataChangedInit.class)
        public DataChangedInit nacosDataChangedInit(final ConfigService configService,
                                                    final NacosProviderProperties nacosProviderProperties) {
            return new NacosDataChangedInit(configService, nacosProviderProperties.getWatchConfigs());
        }

        /**
         * Data changed listener data changed listener.
         *
         * @param configService the config service
         * @return the data changed listener
         */
        @Bean
        @ConditionalOnMissingBean(NacosDataChangedListener.class)
        public DataChangedListener nacosDataChangedListener(final ConfigService configService,
                                                            final List<MergeDataHandler> mergeDataHandlerList,
                                                            final NacosProviderProperties nacosProviderProperties) {

            return new NacosDataChangedListener(configService, mergeDataHandlerList, nacosProviderProperties.getWatchConfigs());
        }

        @Bean
        @ConditionalOnMissingBean(NacosMetaMergeDataHandler.class)
        public MergeDataHandler<MetaData> metaMergeDataHandler(final ConfigService configService,
                                                               final NacosProviderProperties nacosProviderProperties) {
            return new NacosMetaMergeDataHandler(configService, nacosProviderProperties.getWatchConfigs());
        }

    }

    /**
     * description: The type Zookeeper listener.
     *
     * @author zhouxinlei
     * @since 2022-09-05 09:59:16
     */
    @Conditional(ZookeeperCondition.class)
    @Configuration
    @EnableConfigurationProperties(CuratorProperties.class)
    static class ZookeeperListener {

        @Bean(initMethod = "start", destroyMethod = "close")
        @ConditionalOnMissingBean(CuratorFramework.class)
        public CuratorFramework curatorFramework(CuratorProperties curatorProperties) {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(curatorProperties.getTimeout(), curatorProperties.getRetryCount());
            return CuratorFrameworkFactory.newClient(
                    curatorProperties.getZkServers(),
                    curatorProperties.getSessionTimeoutMs(),
                    curatorProperties.getConnectionTimeoutMs(),
                    retryPolicy);
        }

        @Bean
        @ConditionalOnMissingBean(ZookeeperDataChangedInit.class)
        public DataChangedInit zookeeperDataChangedInit(CuratorFramework curatorFramework,
                                                        CuratorProperties curatorProperties) {
            return new ZookeeperDataChangedInit(curatorFramework, curatorProperties.getWatchConfigs());
        }

        @Bean
        @ConditionalOnMissingBean(ZookeeperDataChangedListener.class)
        public DataChangedListener zookeeperDataChangedListener(CuratorFramework curatorFramework,
                                                                List<MergeDataHandler> mergeDataHandlerList) {
            return new ZookeeperDataChangedListener(curatorFramework, mergeDataHandlerList);
        }

        @Bean
        @ConditionalOnMissingBean(ZkMetaMergeDataHandler.class)
        public MergeDataHandler<MetaData> zkMetaMergeDataHandler() {
            return new ZkMetaMergeDataHandler();
        }

    }
}

