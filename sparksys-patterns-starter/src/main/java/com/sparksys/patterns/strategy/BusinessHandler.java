package com.sparksys.patterns.strategy;

/***
 * description: 业务处理策略类
 *
 * @author: zhouxinlei
 * @date: 2020-08-03 14:36:15
 */
public interface BusinessHandler {

    /**
     * 业务处理
     *
     * @param r   处理返回
     * @param t   业务实体参数
     * @param <T> 业务实体
     * @param <R> 返回结果
     */
    <T, R> void businessHandler(R r, T... t);


}
