package com.github.sparkzxl.patterns.strategy;

/***
 * description: 业务处理策略类
 *
 * @author zhouxinlei
 */
public interface BusinessHandler<R, T> {

    /**
     * 业务处理
     *
     * @param t 业务实体参数
     * @return R 返回结果
     */
    R execute(T t);

}
