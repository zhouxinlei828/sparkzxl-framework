package com.github.sparkzxl.patterns.strategy;

/***
 * description: 业务处理策略类
 *
 * @author: zhouxinlei
 * @date: 2020-08-03 14:36:15
 */
public interface BusinessHandler<R, T> {

    /**
     * 业务处理
     *
     * @param t 业务实体参数
     * @return R 返回结果
     */
    R businessHandler(T t);

}
