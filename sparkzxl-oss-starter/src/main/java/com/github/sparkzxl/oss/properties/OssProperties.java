package com.github.sparkzxl.oss.properties;

import static com.github.sparkzxl.oss.properties.OssProperties.OSS_PREFIX;

import com.github.sparkzxl.oss.enums.RegisterMode;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: oss属性注入
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = OSS_PREFIX)
public class OssProperties {

    public static final String OSS_PREFIX = "oss";

    private boolean enabled = false;

    /**
     * 注册类型
     */
    private RegisterMode register;

    /**
     * yaml mode required
     */
    private List<Configuration> configs;

    /**
     * file mode required
     */
    private String path;

}
