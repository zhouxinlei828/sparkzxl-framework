package com.github.sparkzxl.feign.exception;

import com.github.sparkzxl.feign.entity.PredicateMessage;

/**
 * description: feign异常断言
 *
 * @author zhouxinlei
 * @since 2022-05-09 12:19:56
 */
public abstract class AbstractExceptionPredicateFactory<C> extends AbstractConfigurable<C> implements ExceptionPredicateFactory<C> {

    private PredicateMessage predicateMessage;

    protected AbstractExceptionPredicateFactory(Class<C> configClass) {
        super(configClass);
    }

    public PredicateMessage getPredicateMessage() {
        return predicateMessage;
    }

    public void setPredicateMessage(PredicateMessage predicateMessage) {
        this.predicateMessage = predicateMessage;
    }
}
