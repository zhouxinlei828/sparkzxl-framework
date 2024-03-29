package com.github.sparkzxl.web.aop;

import lombok.NonNull;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import java.lang.annotation.Annotation;

/**
 * description: api limit aop通知
 *
 * @author zhouxinlei
 * @since 2022-05-27 12:25:59
 */
public class ApiLimitAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    private final Advice advice;

    private final Pointcut pointcut;

    public ApiLimitAnnotationAdvisor(@NonNull ApiLimitInterceptor apiLimitInterceptor,
                                     Class<? extends Annotation> annotation,
                                     int order) {
        this.advice = apiLimitInterceptor;
        this.pointcut = AnnotationMatchingPointcut.forMethodAnnotation(annotation);
        setOrder(order);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }

}
