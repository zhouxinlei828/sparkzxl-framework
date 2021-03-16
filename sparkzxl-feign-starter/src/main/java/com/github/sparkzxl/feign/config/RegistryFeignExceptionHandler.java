package com.github.sparkzxl.feign.config;

import com.github.sparkzxl.feign.annoation.EnableFeignExceptionHandler;
import feign.codec.ErrorDecoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Objects;

import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;

/**
 * description:
 *
 * @author zhouxinlei
 */
@Slf4j
public class RegistryFeignExceptionHandler implements ImportBeanDefinitionRegistrar, EnvironmentAware, Ordered {

    @Override
    @SneakyThrows
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {

        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableFeignExceptionHandler.class.getName()));
        Class<? extends ErrorDecoder> decoderClass = annotationAttributes.getClass("decoderClass");
        ErrorDecoder errorDecoder = BeanUtils.instantiateClass(decoderClass);

        AbstractBeanDefinition decoder = BeanDefinitionBuilder
                .genericBeanDefinition(ErrorDecoder.class, () -> errorDecoder)
                .setAutowireMode(AUTOWIRE_BY_TYPE)
                .getBeanDefinition();
        registry.registerBeanDefinition(Objects.requireNonNull(decoder.getBeanClassName()), decoder);

        Class<? extends ErrorAttributes> handlerClass = annotationAttributes.getClass("handlerClass");

        if (ObjectUtils.isNotEmpty(handlerClass)) {
            ErrorAttributes errorAttributes = BeanUtils.instantiateClass(handlerClass);
            AbstractBeanDefinition handler = BeanDefinitionBuilder
                    .genericBeanDefinition(ErrorAttributes.class, () -> errorAttributes)
                    .setAutowireMode(AUTOWIRE_BY_TYPE)
                    .getBeanDefinition();
            registry.registerBeanDefinition(Objects.requireNonNull(handler.getBeanClassName()), handler);
            boolean infoEnabled = log.isInfoEnabled();
            if (infoEnabled) {
                log.info("'{}' and '{}' has been successfully registered", handler.getBeanClassName(), decoder.getBeanClassName());
            }
        }
    }


    @Override
    public void setEnvironment(Environment environment) {
        //get the application name of project
        FeignExceptionHandlerContext.setEnvironment(environment);
    }

    @Override
    public int getOrder() {
        return 88;
    }
}
