package com.sparksys.commons.mybatis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description：数据源配置类
 *
 * @author zhouxinlei
 * @date： 2020-06-18 16:12:30
 * @version： v1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sparksys.data")
public class DataProperties {

    private long workerId;

    private long dataCenterId;

    private String mapperScan;
}
