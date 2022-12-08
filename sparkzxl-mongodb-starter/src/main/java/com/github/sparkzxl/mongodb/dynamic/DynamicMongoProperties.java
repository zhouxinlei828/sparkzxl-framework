package com.github.sparkzxl.mongodb.dynamic;

import com.github.sparkzxl.constant.enums.IdTypeEnum;
import com.github.sparkzxl.constant.enums.MultiTenantType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

import static com.github.sparkzxl.mongodb.dynamic.DynamicMongoProperties.DYNAMIC_MONGO_PREFIX;

/**
 * description: 动态数据源配置属性
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = DYNAMIC_MONGO_PREFIX)
public class DynamicMongoProperties {

    public static final String DYNAMIC_MONGO_PREFIX = "spring.dynamic.mongodb";

    private boolean enabled;

    /**
     * 必须设置默认的库,默认master
     */
    private String primary = "master";

    private boolean removeClass = true;

    /**
     * 主键生成策略
     */
    private IdTypeEnum idType = IdTypeEnum.HU_TOOL;

    /**
     * 多租户模式
     */
    private MultiTenantType multiTenantType = MultiTenantType.DATASOURCE;

    /**
     * 租户库 前缀
     */
    private String tenantDatabasePrefix = "sparkzxl_auth";
    /**
     * 租户字段
     */
    private String tenantIdColumn = "tenant_id";

    private Map<String, MongoDatabaseProperty> provider;


    /**
     * description:  MongoDB数据源属性类
     *
     * @author zhouxinlei
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
