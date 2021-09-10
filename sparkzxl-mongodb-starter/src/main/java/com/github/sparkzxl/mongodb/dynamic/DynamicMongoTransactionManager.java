package com.github.sparkzxl.mongodb.dynamic;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

/**
 * description: 动态数据工厂事务管理器
 *
 * @author zhouxinlei
 * @date 2021-09-02 14:22:22
 */
@Slf4j
public class DynamicMongoTransactionManager extends MongoTransactionManager {

    private final MongoDatabaseFactoryContext mongoDatabaseFactoryContext;

    public DynamicMongoTransactionManager(MongoDatabaseFactory dbFactory, MongoDatabaseFactoryContext mongoDatabaseFactoryContext) {
        super(dbFactory);
        this.mongoDatabaseFactoryContext = mongoDatabaseFactoryContext;
    }

    @Override
    public MongoDatabaseFactory getDbFactory() {
        MongoDatabaseFactory databaseFactory = mongoDatabaseFactoryContext.determineMongoDatabaseFactory();
        if (ObjectUtils.isEmpty(databaseFactory)) {
            return super.getDbFactory();
        }
        return databaseFactory;
    }

    @Override
    public MongoDatabaseFactory getResourceFactory() {
        MongoDatabaseFactory databaseFactory = mongoDatabaseFactoryContext.determineMongoDatabaseFactory();
        if (ObjectUtils.isEmpty(databaseFactory)) {
            return super.getResourceFactory();
        }
        return databaseFactory;
    }
}
