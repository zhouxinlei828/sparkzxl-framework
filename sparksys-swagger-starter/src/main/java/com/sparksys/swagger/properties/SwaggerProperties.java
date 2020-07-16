package com.sparksys.swagger.properties;


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

    private ApiInfo apiInfo;


    @Data
    public static class ApiInfo {
        private String title;
        private String description;
        private String version;
        private String termsOfServiceUrl;
    }


}
