package com.github.sparkzxl.lock.aop;

import com.github.sparkzxl.lock.annotation.DistributedLock;
import lombok.NonNull;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * description: 分布式锁Aop通知
 *
 * @author zhouxinlei
 * @since 2022-05-01 22:13:48
 */
public class LockAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    private final Advice advice;

    private final Pointcut pointcut = AnnotationMatchingPointcut.forMethodAnnotation(DistributedLock.class);

    public LockAnnotationAdvisor(@NonNull LockInterceptor lockInterceptor, int order) {
        this.advice = lockInterceptor;
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