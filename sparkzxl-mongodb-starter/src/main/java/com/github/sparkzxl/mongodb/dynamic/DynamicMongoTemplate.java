package com.github.sparkzxl.mongodb.dynamic;

import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2021-09-01 17:43:21
 */
@Slf4j
public class DynamicMongoTemplate extends MongoTemplate {

    private final MongoDatabaseFactoryContext mongoDatabaseFactoryContext;

    public DynamicMongoTemplate(MongoDatabaseFactory mongoDbFactory, MongoDatabaseFactoryContext mongoDatabaseFactoryContext, boolean removeClass) {
        super(mongoDbFactory);
        this.mongoDatabaseFactoryContext = mongoDatabaseFactoryContext;
        MongoConverter converter = getConverter();
        if (removeClass) {
            ((MappingMongoConverter) converter).setTypeMapper(new DefaultMongoTypeMapper(null));
        }
    }

    @Override
    protected MongoDatabase doGetDatabase() {
        MongoDatabaseFactory databaseFactory = mongoDatabaseFactoryContext.determineMongoDatabaseFactory();
        return databaseFactory == null ? super.doGetDatabase() : databaseFactory.getMongoDatabase();
    }
}
