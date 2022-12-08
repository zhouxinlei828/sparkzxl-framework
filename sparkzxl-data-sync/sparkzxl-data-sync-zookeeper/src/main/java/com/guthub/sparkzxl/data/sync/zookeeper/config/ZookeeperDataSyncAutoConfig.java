package com.guthub.sparkzxl.data.sync.zookeeper.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * description: Zookeeper data sync configuration for spring boot.
 *
 * @author zhouxinlei
 * @since 2022-09-08 11:12:14
 */
@Configuration
@ConditionalOnProperty(prefix = "shenyu.sync.zookeeper", name = "url")
@EnableConfigurationProperties(CuratorProperties.class)
public class ZookeeperDataSyncAutoConfig {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperDataSyncAutoConfig.class);

}
