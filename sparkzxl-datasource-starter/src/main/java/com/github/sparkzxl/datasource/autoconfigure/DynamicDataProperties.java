package com.github.sparkzxl.datasource.autoconfigure;

import com.github.sparkzxl.datasource.constant.DataSourceProviderEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: 动态数据源配置类
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(
        prefix = com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties.PREFIX
)
public class DynamicDataProperties {

    private boolean enabled = true;

    private DataSourceProviderEnum dataProvider = DataSourceProviderEnum.MEMORY;

}
