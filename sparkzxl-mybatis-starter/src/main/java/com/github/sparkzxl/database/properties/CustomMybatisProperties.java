package com.github.sparkzxl.database.properties;

import com.baomidou.mybatisplus.annotation.DbType;
import com.github.sparkzxl.constant.ConfigurationConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
     * 数据库类型
     */
    private DbType dbType = DbType.MYSQL;

}
