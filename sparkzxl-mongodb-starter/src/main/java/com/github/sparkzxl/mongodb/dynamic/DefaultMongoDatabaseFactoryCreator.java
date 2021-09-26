package com.github.sparkzxl.mongodb.dynamic;

import cn.hutool.core.text.StrFormatter;
import com.mongodb.ConnectionString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

/**
 * description: 默认数据源工厂构造实现
 *
 * @author zhouxinlei
 */
@Slf4j
public class DefaultMongoDatabaseFactoryCreator implements MongoDatabaseFactoryCreator {

    @Override
    public MongoDatabaseFactory createMongoDatabaseFactory(DynamicMongoProperties.MongoDatabaseProperty mongoDatabaseProperty) {
        ConnectionString connectionString;
        if (StringUtils.isNotBlank(mongoDatabaseProperty.getUri())) {
            connectionString = new ConnectionString(mongoDatabaseProperty.getUri());
        } else {
            String uriTemplate = "mongodb://{}:{}@{}:{}/{}";
            String uri = StrFormatter.format(uriTemplate, mongoDatabaseProperty.getUsername(),
                    mongoDatabaseProperty.getPassword(), mongoDatabaseProperty.getHost(),
                    mongoDatabaseProperty.getPort(), StringUtils.isBlank(mongoDatabaseProperty.getDatabase()) ? mongoDatabaseProperty.getGridFsDatabase() : mongoDatabaseProperty.getDatabase());
            connectionString = new ConnectionString(uri);
        }
        return new SimpleMongoClientDatabaseFactory(connectionString);
    }
}
