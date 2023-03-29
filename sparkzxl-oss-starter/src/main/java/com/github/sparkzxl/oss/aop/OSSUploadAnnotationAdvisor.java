package com.github.sparkzxl.oss.aop;

import java.lang.annotation.Annotation;
import lombok.NonNull;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * description: OSS Upload aop通知
 *
 * @author zhouxinlei
 * @since 2022-05-27 12:25:59
 */
public class OSSUploadAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    private final Advice advice;

    private final Pointcut pointcut;

    public OSSUploadAnnotationAdvisor(@NonNull OSSUploadInterceptor ossUploadInterceptor,
            Class<? extends Annotation> annotation,
            int order) {
        this.advice = ossUploadInterceptor;
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
