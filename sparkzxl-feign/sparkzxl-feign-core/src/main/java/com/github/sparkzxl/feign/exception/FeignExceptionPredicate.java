package com.github.sparkzxl.feign.exception;

import com.github.sparkzxl.feign.entity.PredicateMessage;
import feign.Response;

/**
 * description: feign请求异常断言
 *
 * @author zhouxinlei
 * @since 2022-06-01 09:00:36
 */
public class FeignExceptionPredicate implements ExceptionPredicate<Response> {

    private PredicateMessage predicateMessage;

    @Override
    public PredicateMessage getPredicateMessage() {
        return predicateMessage;
    }

    @Override
    public boolean test(Response response) {
        return false;
    }

    public void setPredicateMessage(PredicateMessage predicateMessage) {
        this.predicateMessage = predicateMessage;
    }
}
