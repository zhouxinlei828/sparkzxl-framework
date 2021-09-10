package com.github.sparkzxl.mongodb.dynamic;

import com.github.sparkzxl.constant.ConfigurationConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

/**
 * description: MongoDB多数据源管理-> 用于动态切库
 *
 * @author zhouxinlei
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DynamicMongoProperties.class)
@ConditionalOnProperty(prefix = ConfigurationConstant.MONGO_PREFIX, name = "enabled", havingValue = "true")
@Slf4j
public class MongoDynamicAutoConfig {

    private final DynamicMongoProperties dynamicMongoProperties;

    public MongoDynamicAutoConfig(DynamicMongoProperties dynamicMongoProperties) {
        this.dynamicMongoProperties = dynamicMongoProperties;
        log.info("Mongodb动态数据源正在加载");
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicMongoDatabaseFactoryProvider dynamicMongoDatabaseFactoryProvider() {
        Map<String, DynamicMongoProperties.MongoDatabaseProperty> databasePropertyMap = dynamicMongoProperties.getProvider();
        return new YamlMongoDatabaseFactoryProvider(databasePropertyMap);
    }

    @Bean
    @ConditionalOnMissingBean
    public MongoDatabaseFactoryContext mongoDatabaseFactoryContext(DynamicMongoDatabaseFactoryProvider dynamicMongoDatabaseFactoryProvider) {
        String primary = dynamicMongoProperties.getPrimary();
        MongoDatabaseFactoryContext mongoDatabaseFactoryContext = new MongoDatabaseFactoryContext(dynamicMongoDatabaseFactoryProvider);
        mongoDatabaseFactoryContext.setPrimary(primary);
        return mongoDatabaseFactoryContext;
    }

    @Bean
    @ConditionalOnMissingBean
    public MongoTemplate mongoTemplate(MongoDatabaseFactoryContext mongoDatabaseFactoryContext) {
        return new DynamicMongoTemplate(mongoDatabaseFactoryContext.determinePrimaryMongoDatabaseFactory(), mongoDatabaseFactoryContext, dynamicMongoProperties.isRemoveClass());
    }

    @Bean
    public PlatformTransactionManager mongoTransactionManager(MongoDatabaseFactoryContext mongoDatabaseFactoryContext) {
        return new DynamicMongoTransactionManager(mongoDatabaseFactoryContext.determinePrimaryMongoDatabaseFactory(), mongoDatabaseFactoryContext);
    }

}
