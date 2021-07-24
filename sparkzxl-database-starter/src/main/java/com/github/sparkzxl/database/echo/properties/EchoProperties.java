package com.github.sparkzxl.database.echo.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: 回显配置类
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = ConfigurationConstant.DATA_ECHO_PREFIX)
public class EchoProperties {

    /**
     * 是否启用远程查询
     */
    private Boolean enabled = Boolean.TRUE;
    /**
     * 是否启用aop注解方式
     */
    private Boolean aopEnabled = Boolean.TRUE;

    /**
     * 字典类型 和 code 的分隔符
     */
    private String dictSeparator = "###";
    /**
     * 多个字典code 之间的的分隔符
     */
    private String dictItemSeparator = ",";

    /**
     * 递归最大深度
     */
    private Integer maxDepth = 3;

}
