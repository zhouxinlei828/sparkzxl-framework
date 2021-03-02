package com.github.sparkzxl.database.properties;

import com.github.sparkzxl.database.enums.IdTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(
        prefix = "mybatis-plus.custom"
)
public class CustomMybatisProperties {

    private IdTypeEnum idType = IdTypeEnum.SNOWFLAKE_ID;

    private long workerId = 0;

    private long dataCenterId = 10;

    private boolean enableTenant;

    private String tenantIdColumn = "tenant_code";

    private String[] ignoreTable;

    private String[] mapperScan;

    @NestedConfigurationProperty
    private InjectionProperties injection = new InjectionProperties();
}
