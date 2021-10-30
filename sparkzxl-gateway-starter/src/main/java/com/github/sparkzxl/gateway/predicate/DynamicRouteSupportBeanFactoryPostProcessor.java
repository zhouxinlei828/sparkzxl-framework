package com.github.sparkzxl.gateway.predicate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Configuration;

/**
 * description: DynamicRoute BeanFactoryPostProcessor
 *
 * @author zhoux
 * @date 2021-10-23 17:30:42
 */
@Configuration
@Slf4j
public class DynamicRouteSupportBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        if (configurableListableBeanFactory.containsBeanDefinition("routePredicateHandlerMapping")) {
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableListableBeanFactory;
            beanFactory.removeBeanDefinition("routePredicateHandlerMapping");
            log.debug("Remove Bean Definition Bean Name : routePredicateHandlerMapping ");
        }
    }
}
