package com.sparksys.commons.core.support;

/**
 * 断言异常处理接口
 *
 * @author zhouxinlei
 */
public interface SparkSysAssert {
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
     * @return
     */
    BaseException newException(Throwable t, Object... args);


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
}
