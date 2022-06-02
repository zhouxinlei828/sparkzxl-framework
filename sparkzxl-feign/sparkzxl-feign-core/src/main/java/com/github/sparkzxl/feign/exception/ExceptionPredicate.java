package com.github.sparkzxl.feign.exception;

import com.github.sparkzxl.feign.entity.PredicateMessage;

import java.util.function.Predicate;

/**
 * description: 异常断言
 *
 * @author zhouxinlei
 * @since 2022-05-09 12:17:43
 */
public interface ExceptionPredicate<T> extends Predicate<T> {

    /**
     * 谓词结果
     *
     * @return PredicateMessage
     */
    PredicateMessage getPredicateMessage();

}
