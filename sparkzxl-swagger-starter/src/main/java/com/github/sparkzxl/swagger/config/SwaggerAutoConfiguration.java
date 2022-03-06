package com.github.sparkzxl.swagger.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.sparkzxl.swagger.properties.SwaggerProperties;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * description: swagger 包扫描配置
 *
 * @author zhouxinlei
 */
@Import({
        Swagger2Configuration.class
})
@ConditionalOnProperty(prefix = "knife4j", name = "enable", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration implements BeanFactoryAware {
    private static final String SEMICOLON = ";";
    private final SwaggerProperties swaggerProperties;
    private final OpenApiExtensionResolver openApiExtensionResolver;
    private BeanFactory beanFactory;

    public SwaggerAutoConfiguration(SwaggerProperties swaggerProperties, OpenApiExtensionResolver openApiExtensionResolver) {
        this.swaggerProperties = swaggerProperties;
        this.openApiExtensionResolver = openApiExtensionResolver;
    }

    private static Predicate<String> ant(final String antPattern) {
        return input -> new AntPathMatcher().match(antPattern, input);
    }

    private static Predicate<RequestHandler> basePackage(final String basePackage) {
        return input -> declaringClass(input).map(handlerPackage(basePackage)).orElse(true);
    }

    private static Function<Class<?>, Boolean> handlerPackage(final String basePackage) {
        return input -> StrUtil.split(basePackage, SEMICOLON).stream().anyMatch(ClassUtils.getPackageName(input)::startsWith);
    }

    private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
        return Optional.ofNullable(input.declaringClass());
    }

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public List<Docket> createRestApi() {
        ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
        List<Docket> docketList = new LinkedList<>();

        // 没有分组
        if (swaggerProperties.getDocket().isEmpty()) {
            Docket docket = createDocket(swaggerProperties);
            configurableBeanFactory.registerSingleton(swaggerProperties.getTitle(), docket);
            docketList.add(docket);
            return docketList;
        }

        // 分组创建
        for (String groupName : swaggerProperties.getDocket().keySet()) {
            SwaggerProperties.DocketInfo docketInfo = swaggerProperties.getDocket().get(groupName);

            ApiInfo apiInfo = new ApiInfoBuilder()
                    .title(docketInfo.getTitle().isEmpty() ? swaggerProperties.getTitle() : docketInfo.getTitle())
                    .description(docketInfo.getDescription().isEmpty() ? swaggerProperties.getDescription() : docketInfo.getDescription())
                    .version(docketInfo.getVersion().isEmpty() ? swaggerProperties.getVersion() : docketInfo.getVersion())
                    .license(docketInfo.getLicense().isEmpty() ? swaggerProperties.getLicense() : docketInfo.getLicense())
                    .licenseUrl(docketInfo.getLicenseUrl().isEmpty() ? swaggerProperties.getLicenseUrl() : docketInfo.getLicenseUrl())
                    .contact(
                            new Contact(
                                    docketInfo.getContact().getName().isEmpty() ? swaggerProperties.getContact().getName() : docketInfo.getContact().getName(),
                                    docketInfo.getContact().getUrl().isEmpty() ? swaggerProperties.getContact().getUrl() : docketInfo.getContact().getUrl(),
                                    docketInfo.getContact().getEmail().isEmpty() ?
                                            swaggerProperties.getContact().getEmail() :
                                            docketInfo.getContact().getEmail()
                            )
                    )
                    .termsOfServiceUrl(
                            docketInfo.getTermsOfServiceUrl().isEmpty() ? swaggerProperties.getTermsOfServiceUrl() : docketInfo.getTermsOfServiceUrl())
                    .build();

            // base-path处理
            // 当没有配置任何path的时候，解析/**
            if (docketInfo.getIncludePath().isEmpty()) {
                docketInfo.getIncludePath().add("/**");
            }
            List<Predicate<String>> includePath = new ArrayList<>(docketInfo.getIncludePath().size());
            for (String path : docketInfo.getIncludePath()) {
                includePath.add(ant(path));
            }

            // exclude-path处理
            List<Predicate<String>> excludePath = new ArrayList<>(docketInfo.getExcludePath().size());
            for (String path : docketInfo.getExcludePath()) {
                excludePath.add(ant(path));
            }
            List<Parameter> parameters = assemblyGlobalOperationParameters(swaggerProperties.getGlobalOperationParameters(),
                    docketInfo.getGlobalOperationParameters());

            Docket docket = new Docket(DocumentationType.SWAGGER_2)
                    .host(swaggerProperties.getHost())
                    .apiInfo(apiInfo)
                    .globalOperationParameters(parameters)
                    .groupName(docketInfo.getGroup())
                    .select()
                    .apis(basePackage(docketInfo.getBasePackage()))
                    .paths(Predicates.and(Predicates.not(Predicates.or(excludePath)), Predicates.or(includePath)))
                    .build()
                    .securitySchemes(securitySchemes(swaggerProperties.getAuthorization(), docketInfo.getAuthorization(), swaggerProperties.getApiKeys(),
                            docketInfo.getApiKeys()))
                    .securityContexts(securityContexts(swaggerProperties.getAuthorization(), docketInfo.getAuthorization()))
                    .globalResponseMessage(RequestMethod.GET, getResponseMessages())
                    .globalResponseMessage(RequestMethod.POST, getResponseMessages())
                    .globalResponseMessage(RequestMethod.PUT, getResponseMessages())
                    .globalResponseMessage(RequestMethod.DELETE, getResponseMessages())
                    .extensions(openApiExtensionResolver.buildExtensions(docketInfo.getGroup()));
            if (ArrayUtil.isNotEmpty(swaggerProperties.getIgnoredClasses())) {
                docket.ignoredParameterTypes(swaggerProperties.getIgnoredClasses());
            }
            configurableBeanFactory.registerSingleton(groupName, docket);
            docketList.add(docket);
        }
        return docketList;
    }

    /**
     * 创建 Docket对象
     *
     * @param swaggerProperties swagger配置
     * @return Docket
     */
    private Docket createDocket(SwaggerProperties swaggerProperties) {
        //API 基础信息
        ApiInfo apiInfo = new ApiInfoBuilder()
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

        // base-path处理
        // 当没有配置任何path的时候，解析/**
        if (swaggerProperties.getIncludePath().isEmpty()) {
            swaggerProperties.getIncludePath().add("/**");
        }
        List<Predicate<String>> basePath = new ArrayList<>();
        for (String path : swaggerProperties.getIncludePath()) {
            basePath.add(ant(path));
        }

        // exclude-path处理
        List<Predicate<String>> excludePath = new ArrayList<>();
        for (String path : swaggerProperties.getExcludePath()) {
            excludePath.add(ant(path));
        }

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .host(swaggerProperties.getHost())
                .apiInfo(apiInfo)
                .groupName(swaggerProperties.getGroup())
                .globalOperationParameters(
                        buildGlobalOperationParametersFromSwaggerProperties(
                                swaggerProperties.getGlobalOperationParameters()))
                .select()

                .apis(basePackage(swaggerProperties.getBasePackage()))
                .paths(Predicates.and(Predicates.not(Predicates.or(excludePath)), Predicates.or(basePath)))
                .build()
                .securitySchemes(securitySchemes(swaggerProperties.getAuthorization(), null, swaggerProperties.getApiKeys(), null))
                .securityContexts(securityContexts(swaggerProperties.getAuthorization(), null))
                .globalResponseMessage(RequestMethod.GET, getResponseMessages())
                .globalResponseMessage(RequestMethod.POST, getResponseMessages())
                .globalResponseMessage(RequestMethod.PUT, getResponseMessages())
                .globalResponseMessage(RequestMethod.DELETE, getResponseMessages())
                .extensions(openApiExtensionResolver.buildExtensions(swaggerProperties.getGroup()));
        if (ArrayUtil.isNotEmpty(swaggerProperties.getIgnoredClasses())) {
            docket.ignoredParameterTypes(swaggerProperties.getIgnoredClasses());
        }
        return docket;
    }

    private List<ResponseMessage> getResponseMessages() {
        return Arrays.asList(
                new ResponseMessageBuilder().code(200).message("成功").build(),
                new ResponseMessageBuilder().code(500).message("哎呀，开了个小差，请稍后再试").build(),
                new ResponseMessageBuilder().code(401).message("暂未登录或者token失效").build(),
                new ResponseMessageBuilder().code(403).message("无权限访问").build(),
                new ResponseMessageBuilder().code(230).message("Token过期").build()
        );
    }

    /**
     * 默认的全局鉴权策略
     */
    private List<SecurityReference> defaultAuth(SwaggerProperties.Authorization authorization) {
        ArrayList<AuthorizationScope> authorizationScopeList = new ArrayList<>();
        authorization.getAuthorizationScopeList()
                .forEach(authorizationScope -> authorizationScopeList.add(
                        new AuthorizationScope(authorizationScope.getScope(), authorizationScope.getDescription())));
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[authorizationScopeList.size()];
        return Collections.singletonList(SecurityReference.builder()
                .reference(authorization.getName())
                .scopes(authorizationScopeList.toArray(authorizationScopes))
                .build());
    }

    /**
     * 配置默认的全局鉴权策略的开关，通过正则表达式进行匹配；默认匹配所有URL
     * 感觉这里设置了没什么卵用？
     */
    private List<SecurityContext> securityContexts(SwaggerProperties.Authorization globalAuthorization,
                                                   SwaggerProperties.Authorization docketAuthorization) {
        SwaggerProperties.Authorization authorization = docketAuthorization == null ? globalAuthorization : docketAuthorization;
        return authorization == null ? Collections.emptyList()
                : Collections.singletonList(SecurityContext.builder()
                .securityReferences(defaultAuth(authorization))
                .forPaths(PathSelectors.regex(authorization.getAuthRegex()))
                .build());
    }

    /**
     * 控制 Authorize 界面
     */

    private List<SecurityScheme> securitySchemes(SwaggerProperties.Authorization globalAuthorization,
                                                 SwaggerProperties.Authorization docketAuthorization,
                                                 List<SwaggerProperties.ApiKey> globalApiKeys,
                                                 List<SwaggerProperties.ApiKey> docketApiKeys) {
        List<SecurityScheme> list = new ArrayList<>();

        SwaggerProperties.Authorization authorization = docketAuthorization == null ? globalAuthorization : docketAuthorization;

        if (authorization != null) {
            ArrayList<AuthorizationScope> authorizationScopeList = new ArrayList<>();
            authorization.getAuthorizationScopeList().forEach(authorizationScope ->
                    authorizationScopeList.add(new AuthorizationScope(authorizationScope.getScope(), authorizationScope.getDescription())));
            ArrayList<GrantType> grantTypes = new ArrayList<>();
            authorization.getTokenUrlList().forEach(tokenUrl -> grantTypes.add(new ResourceOwnerPasswordCredentialsGrant(tokenUrl)));
            OAuth oAuth = new OAuth(authorization.getName(), authorizationScopeList, grantTypes);
            list.add(oAuth);
        }

        List<SwaggerProperties.ApiKey> apiKeys = CollUtil.isEmpty(docketApiKeys) ? globalApiKeys : docketApiKeys;
        if (CollUtil.isNotEmpty(apiKeys)) {
            List<SwaggerProperties.ApiKey> allApiKeys = new ArrayList<>(apiKeys);
            if (globalApiKeys != null && !apiKeys.equals(globalApiKeys)) {
                allApiKeys = new ArrayList<>(globalApiKeys);

                Set<String> docketNames = allApiKeys.stream()
                        .map(SwaggerProperties.ApiKey::getKeyName)
                        .collect(Collectors.toSet());

                for (SwaggerProperties.ApiKey ak : docketApiKeys) {
                    if (!docketNames.contains(ak.getKeyName())) {
                        allApiKeys.add(ak);
                    }
                }
            }
            List<ApiKey> apiKeyList =
                    allApiKeys.stream().map(item -> new ApiKey(item.getName(), item.getKeyName(), item.getPassAs())).collect(Collectors.toList());
            list.addAll(apiKeyList);
        }
        return list;
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

    /**
     * 局部参数按照name覆盖局部参数
     */
    private List<Parameter> assemblyGlobalOperationParameters(
            List<SwaggerProperties.GlobalOperationParameter> globalOperationParameters,
            List<SwaggerProperties.GlobalOperationParameter> docketOperationParameters) {

        if (Objects.isNull(docketOperationParameters) || docketOperationParameters.isEmpty()) {
            return buildGlobalOperationParametersFromSwaggerProperties(globalOperationParameters);
        }

        Set<String> docketNames = docketOperationParameters.stream()
                .map(SwaggerProperties.GlobalOperationParameter::getName)
                .collect(Collectors.toSet());

        List<SwaggerProperties.GlobalOperationParameter> resultOperationParameters = Lists.newArrayList();

        if (Objects.nonNull(globalOperationParameters)) {
            for (SwaggerProperties.GlobalOperationParameter parameter : globalOperationParameters) {
                if (!docketNames.contains(parameter.getName())) {
                    resultOperationParameters.add(parameter);
                }
            }
        }

        resultOperationParameters.addAll(docketOperationParameters);
        return buildGlobalOperationParametersFromSwaggerProperties(resultOperationParameters);
    }
}
