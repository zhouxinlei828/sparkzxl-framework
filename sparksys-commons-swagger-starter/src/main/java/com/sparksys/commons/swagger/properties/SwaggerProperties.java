package com.sparksys.commons.swagger.properties;


import com.sparksys.commons.swagger.ApiInfoEntity;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: swagger属性配置
 *
 * @author: zhouxinlei
 * @date: 2020-07-10 17:47:04
 */
@Data
@ConfigurationProperties(prefix = "sparksys.swagger")
public class SwaggerProperties {

    private String groupName;

    private String basePackage;

    private ApiInfoEntity apiInfo;


}
