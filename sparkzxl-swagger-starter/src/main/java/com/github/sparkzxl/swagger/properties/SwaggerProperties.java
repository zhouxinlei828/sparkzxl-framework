package com.github.sparkzxl.swagger.properties;


import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: swagger属性配置
 *
 * @author: zhouxinlei
 * @date: 2020-07-10 17:47:04
 */
@Data
@ConfigurationProperties(
        prefix = "knife4j"
)
public class SwaggerProperties {

    private String groupName;

    private String basePackage;

    private String title;

    private String description;

    private String version;

    /**
     * 许可证
     **/
    private String license = "";

    /**
     * 许可证URL
     **/
    private String licenseUrl = "";

    private String termsOfServiceUrl;

    private Contact contact = new Contact();


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Contact {
        /**
         * 联系人
         **/
        private String name = "";
        /**
         * 联系人url
         **/
        private String url = "";
        /**
         * 联系人email
         **/
        private String email = "";
    }

}
