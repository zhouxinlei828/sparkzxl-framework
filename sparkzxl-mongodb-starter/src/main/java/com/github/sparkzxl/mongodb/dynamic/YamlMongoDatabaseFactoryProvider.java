package com.github.sparkzxl.mongodb.dynamic;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.MongoDatabaseFactory;

import java.util.Map;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2021-09-02 08:41:55
 */
@AllArgsConstructor
public class YamlMongoDatabaseFactoryProvider extends AbstractMongoDatabaseFactoryProvider {

    private final Map<String, DynamicMongoProperties.MongoDatabaseProperty> mongoDatabasePropertyMap;

    @Override
    public Map<String, MongoDatabaseFactory> loadMongoDatabaseFactories() {
        return createMongoDatabaseMap(mongoDatabasePropertyMap);
    }
}
