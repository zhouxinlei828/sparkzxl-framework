package com.github.sparkzxl.database.properties;

import com.baomidou.mybatisplus.annotation.DbType;
import com.github.sparkzxl.database.echo.properties.EchoProperties;
import com.github.sparkzxl.database.enums.IdTypeEnum;
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
        prefix = "mybatis-plus.custom"
)
public class CustomMybatisProperties {

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
     * 使用分页
     */
    private boolean enablePage;

    /**
     * 数据库类型
     */
    private DbType dbType = DbType.MYSQL;

    /**
     * 租户字段
     */
    private String tenantIdColumn = "realm_code";

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
