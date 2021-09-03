package com.github.sparkzxl.mongodb.dynamic;

import org.springframework.data.mongodb.MongoDatabaseFactory;

/**
 * description: 数据源工厂构造接口
 *
 * @author zhouxinlei
 * @date 2021-09-03 08:55:03
 */
public interface MongoDatabaseFactoryCreator {

    /**
     * 创建数据源工厂
     *
     * @param mongoDatabaseProperty 数据源属性
     * @return MongoDatabaseFactory
     */
    MongoDatabaseFactory createMongoDatabaseFactory(DynamicMongoProperties.MongoDatabaseProperty mongoDatabaseProperty);

}
