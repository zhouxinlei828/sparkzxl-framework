package com.github.sparkzxl.core.assert_;

import com.github.sparkzxl.core.support.BaseException;

/**
 * 断言异常处理接口
 *
 * @author zhouxinlei
 */
public interface BusinessAssert {
    /**
     * 创建异常
     *
     * @param args 入参
     * @return BaseException
     */
    BaseException newException(Object... args);

    /**
     * 创建异常
     *
     * @param t    t
     * @param args 入参
     * @return BaseException
     */
    BaseException newException(Throwable t, Object... args);

    /**
     * 断言对象obj是否为True
     *
     * @param obj 入参
     */
    default void assertNotTrue(Boolean obj) {
        if (!obj) {
            throw newException(false);
        }
    }

    /**
     * 断言对象比较大小，start<end 抛出异常
     *
     * @param start start
     * @param end   end
     */
    default void assertCompare(long start, long end) {
        if (start < end) {
            throw newException(-1);
        }
    }

    /**
     * 断言对象obj非空。如果对象obj为空，则抛出异常
     *
     * @param obj 入参
     */
    default void assertNotNull(Object obj) {
        if (obj == null) {
            throw newException(obj);
        }
    }

    /**
     * 断言对象obj非空。如果对象obj为空，则抛出异常
     * 异常信息message支持传递参数方式，避免在判断之前进行字符串拼接操作
     *
     * @param obj  随想
     * @param args message
     */
    default void assertNotNull(Object obj, Object... args) {
        if (obj == null) {
            throw newException(args);
        }
    }

    /**
     * 默认断言异常
     * 常用于处理已知异常
     */
    default void assertException() {
        throw newException();
    }
}
