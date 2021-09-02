package com.github.sparkzxl.mongodb.dynamic;

import com.mongodb.MongoException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description: 动态数据源工厂应用上下文
 *
 * @author zhouxinlei
 * @date 2021-09-02 21:32:10
 */
@Slf4j
public class MongoDatabaseFactoryContext implements InitializingBean, DisposableBean {

    @Setter
    private String primary = "master";

    private final Map<String, MongoDatabaseFactory> databaseFactories = new ConcurrentHashMap<>();

    private final DynamicMongoDatabaseFactoryProvider mongoDataSourceProvider;

    public MongoDatabaseFactoryContext(DynamicMongoDatabaseFactoryProvider mongoDataSourceProvider) {
        this.mongoDataSourceProvider = mongoDataSourceProvider;
    }

    /**
     * 添加数据源
     *
     * @param ds              数据源名称
     * @param databaseFactory 数据源
     */
    public synchronized void addDatabaseFactory(String ds, MongoDatabaseFactory databaseFactory) {
        databaseFactories.put(ds, databaseFactory);
        log.info("dynamic-database-factory - add a datasource named [{}] success", ds);
    }

    public MongoDatabaseFactory determineMongoDatabaseFactory() {
        String dsKey = DynamicDatabaseContextHolder.peek();
        return getDatabaseFactory(dsKey);
    }

    /**
     * 获取数据源
     *
     * @param ds 数据源名称
     * @return 数据源
     */
    public MongoDatabaseFactory getDatabaseFactory(String ds) {
        if (StringUtils.isEmpty(ds)) {
            return determinePrimaryMongoDatabaseFactory();
        } else if (databaseFactories.containsKey(ds)) {
            log.debug("dynamic-database-factory switch to the datasource named [{}]", ds);
            return databaseFactories.get(ds);
        }
        return determinePrimaryMongoDatabaseFactory();
    }

    public MongoDatabaseFactory determinePrimaryMongoDatabaseFactory() {
        log.debug("dynamic-database-factory switch to the primary datasource");
        MongoDatabaseFactory databaseFactory = databaseFactories.get(primary);
        if (databaseFactory != null) {
            return databaseFactory;
        }
        throw new MongoException("dynamic-database-factory can not find primary datasource");
    }

    @Override
    public void destroy() {
        log.info("dynamic-database-factory start closing ....");
        databaseFactories.clear();
        log.info("dynamic-database-factory all closed success,bye");
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, MongoDatabaseFactory> databaseFactories = mongoDataSourceProvider.loadMongoDatabaseFactories();
        for (Map.Entry<String, MongoDatabaseFactory> mongoDatabaseFactoryEntry : databaseFactories.entrySet()) {
            addDatabaseFactory(mongoDatabaseFactoryEntry.getKey(), mongoDatabaseFactoryEntry.getValue());
        }

        // 检测默认数据源是否设置
        MongoDatabaseFactory mongoDatabaseFactory = databaseFactories.get(primary);
        if (ObjectUtils.isEmpty(mongoDatabaseFactory)) {
            throw new MongoException("默认数据源未配置");
        }
    }
}
