package com.github.sparkzxl.swagger.config;

import com.github.sparkzxl.core.entity.AuthUserInfo;
import com.github.sparkzxl.swagger.properties.SwaggerProperties;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.collect.Lists;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;
import java.util.Objects;

/**
 * description: Swagger2文档配置
 *
 * @author zhouxinlei
 */
@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerAutoConfiguration {

    @ConditionalOnProperty(name = {"knife4j.enable"}, havingValue = "true")
    @Bean(value = "defaultApi2")
    public Docket defaultApi2(SwaggerProperties swaggerProperties) {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo(swaggerProperties))
                .groupName(swaggerProperties.getGroupName())
                .globalOperationParameters(
                        buildGlobalOperationParametersFromSwaggerProperties(
                                swaggerProperties.getGlobalOperationParameters()))
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
                .paths(PathSelectors.any())
                .build()
                .ignoredParameterTypes(AuthUserInfo.class);
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


    private List<Parameter> buildGlobalOperationParametersFromSwaggerProperties(
            List<SwaggerProperties.GlobalOperationParameter> globalOperationParameters) {
        List<Parameter> parameters = Lists.newArrayList();

        if (Objects.isNull(globalOperationParameters)) {
            return parameters;
        }
        for (SwaggerProperties.GlobalOperationParameter globalOperationParameter : globalOperationParameters) {
            parameters.add(new ParameterBuilder()
                    .name(globalOperationParameter.getName())
                    .description(globalOperationParameter.getDescription())
                    .defaultValue(globalOperationParameter.getDefaultValue())
                    .required(globalOperationParameter.getRequired())
                    .allowMultiple(globalOperationParameter.getAllowMultiple())
                    .parameterType(globalOperationParameter.getParameterType())
                    .modelRef(new ModelRef(globalOperationParameter.getModelRef()))
                    .hidden(globalOperationParameter.getHidden())
                    .pattern(globalOperationParameter.getPattern())
                    .collectionFormat(globalOperationParameter.getCollectionFormat())
                    .allowEmptyValue(globalOperationParameter.getAllowEmptyValue())
                    .order(globalOperationParameter.getOrder())
                    .build());
        }
        return parameters;
    }
}
