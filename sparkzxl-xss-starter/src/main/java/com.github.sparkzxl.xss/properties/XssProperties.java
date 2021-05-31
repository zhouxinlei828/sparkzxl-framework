package com.github.sparkzxl.xss.properties;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * description: 忽略XSS 配置类
 *
 * @author zhouxinlei
 * @date 2021-05-31 13:54:47
 */
@Data
@ConfigurationProperties(prefix = XssProperties.PREFIX)
public class XssProperties {

    public static final String PREFIX = "sparkzxl.xss";

    private Boolean enabled = true;
    /**
     * 是否启用 RequestBody 注解标记的参数 反序列化时过滤XSS
     */
    private Boolean requestBodyEnabled = false;
    private int order = 1;
    private List<String> patterns = CollUtil.newArrayList("/*");
    private List<String> ignorePaths = CollUtil.newArrayList("favicon.ico",
            "/**/doc.html",
            "/**/swagger-ui.html",
            "/csrf",
            "/webjars/**",
            "/v2/**",
            "/swagger-resources/**",
            "/resources/**",
            "/static/**",
            "/public/**",
            "/classpath:*",
            "/actuator/**",
            "/**/noxss/**",
            "/**/activiti/**",
            "/**/service/model/**",
            "/**/service/editor/**"
    );
    private List<String> ignoreParamValues = CollUtil.newArrayList("noxss");


}
