package com.github.sparkzxl.database.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * description：数据源配置类
 *
 * @author zhouxinlei
 * @date： 2020-06-18 16:12:30
 */
@Data
@ConfigurationProperties(prefix = "sparkzxl.data")
public class DataProperties {

    private long workerId = 0;

    private long dataCenterId = 10;

}
