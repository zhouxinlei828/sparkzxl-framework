package com.github.sparkzxl.swagger.config;

import com.github.sparkzxl.swagger.properties.SwaggerProperties;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.github.sparkzxl.core.entity.AuthUserInfo;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * description: Swagger2文档配置
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:24:39
 */
@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerAutoConfiguration {

    private final OpenApiExtensionResolver openApiExtensionResolver;

    public SwaggerAutoConfiguration(OpenApiExtensionResolver openApiExtensionResolver) {
        this.openApiExtensionResolver = openApiExtensionResolver;
    }

    @Bean(value = "defaultApi2")
    public Docket defaultApi2(SwaggerProperties swaggerProperties) {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo(swaggerProperties))
                .groupName(swaggerProperties.getGroupName())
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
                .paths(PathSelectors.any())
                .build()
                .ignoredParameterTypes(AuthUserInfo.class)
                .extensions(openApiExtensionResolver.buildExtensions(swaggerProperties.getGroupName()));
    }


    private ApiInfo apiInfo(SwaggerProperties swaggerProperties) {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .version(swaggerProperties.getVersion())
                .license(swaggerProperties.getLicense())
                .licenseUrl(swaggerProperties.getLicenseUrl())
                .contact(new Contact(swaggerProperties.getContact().getName(),
                        swaggerProperties.getContact().getUrl(),
                        swaggerProperties.getContact().getEmail()))
                .termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
                .build();
    }

}
