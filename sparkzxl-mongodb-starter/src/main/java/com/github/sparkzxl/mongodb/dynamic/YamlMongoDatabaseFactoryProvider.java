package com.github.sparkzxl.mongodb.dynamic;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.MongoDatabaseFactory;

import java.util.Map;

/**
 * description: 通过yaml属性加载数据源工厂
 *
 * @author zhouxinlei
 */
@AllArgsConstructor
public class YamlMongoDatabaseFactoryProvider extends AbstractMongoDatabaseFactoryProvider {

    private final Map<String, DynamicMongoProperties.MongoDatabaseProperty> mongoDatabasePropertyMap;

    @Override
    public Map<String, MongoDatabaseFactory> loadMongoDatabaseFactories() {
        return createMongoDatabaseFactoryMap(mongoDatabasePropertyMap);
    }
}
