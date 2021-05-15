package com.github.sparkzxl.core.annotation;

import java.lang.annotation.*;

/**
 * description: 白名单
 * 有些接口可能根据业务需要或者协议需要不能使用统一返回体，例如支付的通知应答。这就需要一个类似白名单的机制来绕过统一返回体控制器通知类。我们可以借助于ResponseBodyAdvice<T>的下列方法实现：
 *
 * @author zhouxinlei
 * @date 2021-05-15 12:47:37
 */
@Documented
@Inherited
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreRestBody {
}
