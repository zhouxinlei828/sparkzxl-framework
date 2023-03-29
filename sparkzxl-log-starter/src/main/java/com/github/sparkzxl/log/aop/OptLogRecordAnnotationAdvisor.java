package com.github.sparkzxl.log.aop;

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
 * description: 用户操作行为AOP通知
 *
 * @author zhouxinlei
 * @since 2022-05-27 14:20:18
 */
public class OptLogRecordAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    private final Advice advice;

    private final Pointcut pointcut;

    public OptLogRecordAnnotationAdvisor(@NonNull OptLogRecordInterceptor requestLogInterceptor,
            Class<? extends Annotation> annotation,
            int order) {
        this.advice = requestLogInterceptor;
        this.pointcut = AnnotationMatchingPointcut.forMethodAnnotation(annotation);
        setOrder(order);
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }
}
