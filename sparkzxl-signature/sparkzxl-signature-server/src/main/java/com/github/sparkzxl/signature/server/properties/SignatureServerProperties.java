package com.github.sparkzxl.signature.server.properties;

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.List;

import static com.github.sparkzxl.signature.server.properties.SignatureServerProperties.CONFIG_PREFIX;

@Data
@ConfigurationProperties(prefix = CONFIG_PREFIX)
public class SignatureServerProperties implements Serializable {

    private static final long serialVersionUID = 2139244261223119041L;
    /**
     * 告警线程池配置属性前缀
     */
    public static final String CONFIG_PREFIX = "phoenix.signature.server";

    private boolean enabled = true;
    /**
     * 判断时间是否大于xx秒(防止重放攻击)
     */
    private Long nonceTimeoutSeconds = 60L;

    private List<String> includePatterns = Lists.newArrayList("/**");

    private List<String> excludePatterns = Lists.newArrayList();

}
