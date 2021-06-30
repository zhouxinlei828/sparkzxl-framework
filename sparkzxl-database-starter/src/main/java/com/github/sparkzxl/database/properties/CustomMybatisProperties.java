package com.github.sparkzxl.database.properties;

import com.baomidou.mybatisplus.annotation.DbType;
import com.github.sparkzxl.constant.ConfigurationConstant;
import com.github.sparkzxl.constant.enums.IdTypeEnum;
import com.github.sparkzxl.constant.enums.MultiTenantType;
import com.github.sparkzxl.database.echo.properties.EchoProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * description: CustomMybatisProperties配置属性类
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(
        prefix = ConfigurationConstant.DATA_PREFIX
)
public class CustomMybatisProperties {

    /**
     * 主键生成策略
     */
    private IdTypeEnum idType = IdTypeEnum.SNOWFLAKE_ID;

    private long workerId = 0;

    private long dataCenterId = 10;

    /**
     * 使用分页
     */
    private boolean enablePage;

    /**
     * 数据库类型
     */
    private DbType dbType = DbType.MYSQL;

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
     * mapper扫包路径
     */
    private String[] mapperScan;

    @NestedConfigurationProperty
    private EchoProperties echo = new EchoProperties();
}
