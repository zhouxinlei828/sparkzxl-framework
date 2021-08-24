package com.github.sparkzxl.feign.annoation;

import com.github.sparkzxl.feign.config.FeignExceptionDecoder;
import com.github.sparkzxl.feign.config.RegistryFeignExceptionHandler;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * description:
 *
 * @author zhouxinlei
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({RegistryFeignExceptionHandler.class})
public @interface EnableFeignExceptionHandler {

    /**
     * 异常解析处理类, 必须要有无参构造方法
     *
     * @return Class<? extends ErrorDecoder>
     */
    Class<? extends ErrorDecoder> decoderClass() default FeignExceptionDecoder.class;

    /**
     * 异常传递
     *
     * @return boolean
     */
    boolean transferException() default false;

}
