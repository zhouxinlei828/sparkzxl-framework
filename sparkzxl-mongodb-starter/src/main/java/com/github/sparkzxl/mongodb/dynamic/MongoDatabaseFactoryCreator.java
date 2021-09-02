package com.github.sparkzxl.mongodb.dynamic;

import org.springframework.data.mongodb.MongoDatabaseFactory;

public interface MongoDatabaseFactoryCreator {

    MongoDatabaseFactory createMongoDatabaseFactory(DynamicMongoProperties.MongoDatabaseProperty mongoDatabaseProperty);

}
