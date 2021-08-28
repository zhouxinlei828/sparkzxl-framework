package com.github.sparkzxl.mongodb.config;

import com.github.sparkzxl.mongodb.event.MongoInsertEventListener;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * description: MongoDB事务自动管理
 *
 * @author zhouxinlei
 */
@Configuration
@EnableTransactionManagement
public class MongoAutoConfig {

    @Bean
    public MongoInsertEventListener mongoInsertEventListener(){
        return new MongoInsertEventListener();
    }
    // 目的，就是为了移除 _class field 。参考博客 https://blog.csdn.net/bigtree_3721/article/details/82787411
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory mongoDatabaseFactory,
                                                       MongoMappingContext context,
                                                       BeanFactory beanFactory) {
        // 创建 DbRefResolver 对象
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDatabaseFactory);
        // 创建 MappingMongoConverter 对象
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
        // 设置 conversions 属性
        try {
            mappingConverter.setCustomConversions(beanFactory.getBean(MongoCustomConversions.class));
        } catch (NoSuchBeanDefinitionException ignore) {
        }
        // 设置 typeMapper 属性，从而移除 _class field 。
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return mappingConverter;
    }

    @Bean
    public MongoTransactionManager mongoTransactionManager(@Autowired MongoDatabaseFactory mongoDbFactory) {
        return new MongoTransactionManager(mongoDbFactory);
    }

}
