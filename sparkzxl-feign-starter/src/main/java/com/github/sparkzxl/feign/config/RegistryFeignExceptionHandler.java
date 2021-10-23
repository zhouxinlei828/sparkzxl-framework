package com.github.sparkzxl.feign.config;

import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.feign.annoation.EnableFeignExceptionHandler;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Objects;

import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;

/**
 * description: feign异常处理
 *
 * @author zhouxinlei
 */
@Slf4j
public class RegistryFeignExceptionHandler implements ImportBeanDefinitionRegistrar, Ordered {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableFeignExceptionHandler.class.getName()));
        Class<? extends ErrorDecoder> decoderClass = annotationAttributes.getClass("decoderClass");
        ErrorDecoder errorDecoder = BeanUtils.instantiateClass(decoderClass);

        AbstractBeanDefinition decoder = BeanDefinitionBuilder
                .genericBeanDefinition(ErrorDecoder.class, () -> errorDecoder)
                .setAutowireMode(AUTOWIRE_BY_TYPE)
                .getBeanDefinition();
        registry.registerBeanDefinition(Objects.requireNonNull(decoder.getBeanClassName()), decoder);
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.REGISTRY_FEIGN_FILTER.getOrder();
    }
}
