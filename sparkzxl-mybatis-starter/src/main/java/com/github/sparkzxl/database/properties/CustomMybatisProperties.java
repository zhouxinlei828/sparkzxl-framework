package com.github.sparkzxl.database.properties;

import com.baomidou.mybatisplus.annotation.DbType;
import com.github.sparkzxl.constant.ConfigurationConstant;
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
        prefix = ConfigurationConstant.MYBATIS_CUSTOM_PREFIX
)
public class CustomMybatisProperties {


    /**
     * mapper扫包路径
     */
    private String[] mapperScan;

    /**
     * 使用分页
     */
    private boolean enablePage;

    /**
     * 数据库类型
     */
    private DbType dbType = DbType.MYSQL;

}
