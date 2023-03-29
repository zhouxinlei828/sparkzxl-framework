package com.github.sparkzxl.mongodb.dynamic;

import java.util.Map;
import org.springframework.data.mongodb.MongoDatabaseFactory;

/**
 * description: mongodb多数据源加载接口，默认的实现为从yml信息中加载所有数据源 你可以自己实现从其他地方加载所有数据源
 *
 * @author zhouxinlei
 */
public interface DynamicMongoDatabaseFactoryProvider {

    /**
     * 加载所有数据源
     *
     * @return 所有数据源，key为数据源名称
     */
    Map<String, MongoDatabaseFactory> loadMongoDatabaseFactories();

}
