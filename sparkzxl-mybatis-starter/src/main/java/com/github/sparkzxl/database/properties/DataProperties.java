package com.github.sparkzxl.database.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import com.github.sparkzxl.constant.enums.IdTypeEnum;
import com.github.sparkzxl.constant.enums.MultiTenantType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: CustomMybatisProperties配置属性类
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(
        prefix = ConfigurationConstant.DATA_PREFIX
)
public class DataProperties {


    private String driverClassName = "com.mysql.cj.jdbc.Driver";
    private String ip = "127.0.0.1";
    private int port = 3306;
    private String username = "root";

    private String password = "123456";

    private String database = "sparkzxl_tenant";

    /**
     * 主键生成策略
     */
    private IdTypeEnum idType = IdTypeEnum.SNOWFLAKE_ID;

    private long workerId = 0;

    private long dataCenterId = 10;

    /**
     * 使用多租户
     */
    private boolean enableTenant;

    /**
     * 多租户模式
     */
    private MultiTenantType multiTenantType = MultiTenantType.COLUMN;

    /**
     * 租户库 前缀
     */
    private String tenantDatabasePrefix = "sparkzxl_auth";
    /**
     * 租户字段
     */
    private String tenantIdColumn = "tenant_id";

    /**
     * 租户默认不填充数据表
     */
    private String[] ignoreTable;

    /**
     * 是否启用  sql性能规范插件
     */
    private Boolean isIllegalSql = false;

    /**
     * 是否启用控制台sql记录
     */
    private Boolean p6spy = false;
}
