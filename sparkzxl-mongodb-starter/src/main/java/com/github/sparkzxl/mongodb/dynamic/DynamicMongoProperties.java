package com.github.sparkzxl.mongodb.dynamic;

import com.github.sparkzxl.constant.ConfigurationConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2021-09-02 08:43:30
 */
@Data
@ConfigurationProperties(prefix = ConfigurationConstant.MONGO_PREFIX)
public class DynamicMongoProperties {

    private boolean enabled;

    /**
     * 必须设置默认的库,默认master
     */
    private String primary = "master";

    private boolean removeClass = true;

    private Map<String, MongoDatabaseProperty> provider;


    /**
     * description:  MongoDB数据源属性类
     *
     * @author zhouxinlei
     * @date 2021-09-03 13:29:25
     */
    @Data
    public static class MongoDatabaseProperty {

        private String host;

        private Integer port = null;

        private String uri;

        private String database;

        private String gridFsDatabase;

        private String username;

        private String password;

    }

}
