package com.github.sparkzxl.swagger.properties;


import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import springfox.documentation.service.AllowableValues;

import java.util.List;

/**
 * description: swagger属性配置
 *
 * @author zhouxinlei
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

    /**
     * 全局参数配置
     **/
    private List<GlobalOperationParameter> globalOperationParameters;

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

    @Setter
    @Getter
    public static class GlobalOperationParameter {
        /**
         * 参数名
         **/
        private String name;

        /**
         * 描述信息
         **/
        private String description = "全局参数";

        /**
         * 指定参数类型
         **/
        private String modelRef = "String";

        /**
         * 参数放在哪个地方:header,query,path,body.form
         **/
        private String parameterType = "header";

        /**
         * 参数是否必须传
         **/
        private Boolean required = false;
        private Boolean allowMultiple = false;
        private AllowableValues allowableValues;
        private Boolean hidden = false;
        private String pattern = "";
        private String collectionFormat = "";
        /**
         * 默认值
         */
        private String defaultValue = "";
        /**
         * 允许为空
         */
        private Boolean allowEmptyValue = true;
        /**
         * 排序
         */
        private int order = 1;
    }
}
