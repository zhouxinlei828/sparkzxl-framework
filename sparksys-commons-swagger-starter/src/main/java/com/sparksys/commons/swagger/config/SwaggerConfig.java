package com.sparksys.commons.swagger.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.sparksys.commons.core.entity.GlobalAuthUser;
import com.sparksys.commons.swagger.properties.SwaggerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
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
public class SwaggerConfig {

    @Autowired
    private SwaggerProperties swaggerProperties;

    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName(swaggerProperties.getGroupName())
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
                .paths(PathSelectors.any())
                .build()
                .ignoredParameterTypes(GlobalAuthUser.class);
    }



    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getApiInfo().getTitle())
                .description(swaggerProperties.getApiInfo().getDescription())
                .termsOfServiceUrl(swaggerProperties.getApiInfo().getTermsOfServiceUrl())
                .version(swaggerProperties.getApiInfo().getVersion())
                .build();
    }

}
