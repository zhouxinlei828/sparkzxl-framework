package com.github.sparkzxl.oss.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import com.github.sparkzxl.oss.enums.FileOperateSource;
import com.github.sparkzxl.oss.executor.OssExecutor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * description: oss属性注入
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = ConfigurationConstant.OSS_PREFIX)
public class OssProperties {

    private boolean enabled = false;

    private FileOperateSource store;

    @NestedConfigurationProperty
    private OssConfigInfo configInfo;

    /**
     * 默认主执行器
     */
    private Class<? extends OssExecutor> primaryExecutor;

}
