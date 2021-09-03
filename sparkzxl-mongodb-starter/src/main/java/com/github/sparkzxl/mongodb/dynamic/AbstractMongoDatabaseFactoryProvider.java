package com.github.sparkzxl.mongodb.dynamic;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.MongoDatabaseFactory;

import java.util.Map;

/**
 * description: 抽象mongodb多数据源加载
 *
 * @author zhouxinlei
 * @date 2021-09-03 08:57:16
 */
@Slf4j
public abstract class AbstractMongoDatabaseFactoryProvider implements DynamicMongoDatabaseFactoryProvider {

    private final DefaultMongoDatabaseFactoryCreator mongoDatabaseFactoryCreator = new DefaultMongoDatabaseFactoryCreator();

    protected Map<String, MongoDatabaseFactory> createMongoDatabaseMap(Map<String, DynamicMongoProperties.MongoDatabaseProperty> mongoDatabasePropertyMap) {
        log.info("MongoDatabase创建连接工厂====");
        Map<String, MongoDatabaseFactory> databaseFactoryMap = Maps.newHashMapWithExpectedSize(mongoDatabasePropertyMap.size() * 2);
        for (Map.Entry<String, DynamicMongoProperties.MongoDatabaseProperty> databasePropertyEntry : mongoDatabasePropertyMap.entrySet()) {
            String key = databasePropertyEntry.getKey();
            DynamicMongoProperties.MongoDatabaseProperty databaseProperty = databasePropertyEntry.getValue();
            databaseFactoryMap.put(key, mongoDatabaseFactoryCreator.createMongoDatabaseFactory(databaseProperty));
        }
        return databaseFactoryMap;
    }

}
