package com.github.sparkzxl.mongodb.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import com.github.sparkzxl.constant.enums.IdTypeEnum;
import com.github.sparkzxl.constant.enums.MultiTenantType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: MongoDB配置属性
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = ConfigurationConstant.MONGO_PREFIX)
public class DataProperties {

    /**
     * 主键生成策略
     */
    private IdTypeEnum idType = IdTypeEnum.HU_TOOL;

    /**
     * 多租户模式
     */
    private MultiTenantType multiTenantType = MultiTenantType.NONE;

    /**
     * 租户库 前缀
     */
    private String tenantDatabasePrefix = "sparkzxl_auth";
    /**
     * 租户字段
     */
    private String tenantIdColumn = "tenant_id";

}
