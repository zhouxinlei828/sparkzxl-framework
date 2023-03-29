package com.github.sparkzxl.mongodb.dynamic;

import com.mongodb.MongoException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.util.StringUtils;

/**
 * description: 动态数据源工厂应用上下文
 *
 * @author zhouxinlei
 */
@Slf4j
public class MongoDatabaseFactoryContext implements InitializingBean, DisposableBean {

    private final Map<String, MongoDatabaseFactory> databaseFactories = new ConcurrentHashMap<>();
    private final DynamicMongoDatabaseFactoryProvider mongoDataSourceProvider;
    @Setter
    private String primary = "master";

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
        log.info("dynamic-database-factory - add a database-factory named [{}] success", ds);
    }

    /**
     * 删除数据源
     *
     * @param ds 数据源名称
     */
    public synchronized void removeDataSource(String ds) {
        if (!StringUtils.hasText(ds)) {
            throw new RuntimeException("remove parameter could not be empty");
        }
        if (primary.equals(ds)) {
            throw new RuntimeException("could not remove primary database-factory");
        }
        if (databaseFactories.containsKey(ds)) {
            databaseFactories.remove(ds);
            log.info("dynamic-database-factory - remove the database named [{}] success", ds);
        } else {
            log.warn("dynamic-database-factory - could not find a database named [{}]", ds);
        }
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
            log.debug("dynamic-database-factory switch to the database-factory named [{}]", ds);
            return databaseFactories.get(ds);
        }
        return determinePrimaryMongoDatabaseFactory();
    }

    public MongoDatabaseFactory determinePrimaryMongoDatabaseFactory() {
        log.debug("dynamic-database-factory switch to the primary database-factory");
        MongoDatabaseFactory databaseFactory = databaseFactories.get(primary);
        if (databaseFactory != null) {
            return databaseFactory;
        }
        throw new MongoException("dynamic-database-factory can not find primary database-factory");
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
            throw new MongoException("默认数据源工厂未配置");
        }
    }
}
