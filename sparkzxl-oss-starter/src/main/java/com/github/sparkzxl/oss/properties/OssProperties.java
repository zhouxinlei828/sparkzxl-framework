package com.github.sparkzxl.oss.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import com.github.sparkzxl.oss.enums.RegisterMode;
import com.github.sparkzxl.oss.executor.OssExecutor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * description: oss属性注入
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = ConfigurationConstant.OSS_PREFIX)
public class OssProperties {

    private boolean enabled = false;

    /**
     * 注册类型
     */
    private RegisterMode register;

    /**
     * yaml mode required
     */
    private List<OssConfigInfo> configInfos;

    /**
     * file mode required
     */
    private String path;

    /**
     * 默认主执行器
     */
    private Class<? extends OssExecutor> primaryExecutor;

}
