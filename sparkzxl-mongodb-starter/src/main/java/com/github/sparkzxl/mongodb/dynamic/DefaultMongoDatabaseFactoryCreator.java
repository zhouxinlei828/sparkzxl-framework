package com.github.sparkzxl.mongodb.dynamic;

import com.mongodb.ConnectionString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2021-09-01 18:10:48
 */
@Slf4j
public class DefaultMongoDatabaseFactoryCreator implements MongoDatabaseFactoryCreator {

    @Override
    public MongoDatabaseFactory createMongoDatabaseFactory(DynamicMongoProperties.MongoDatabaseProperty mongoDatabaseProperty) {
        ConnectionString connectionString = new ConnectionString(mongoDatabaseProperty.getUrl());
        return new SimpleMongoClientDatabaseFactory(connectionString);
    }
}
