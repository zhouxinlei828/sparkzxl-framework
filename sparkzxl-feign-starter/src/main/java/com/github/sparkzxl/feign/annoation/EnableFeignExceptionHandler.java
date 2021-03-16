package com.github.sparkzxl.feign.annoation;

import com.github.sparkzxl.feign.config.RegistryFeignExceptionHandler;
import com.github.sparkzxl.feign.default_.FeignExceptionDecoder;
import com.github.sparkzxl.feign.default_.FeignExceptionHandler;
import feign.codec.ErrorDecoder;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
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
     * 异常抛出处理类, 必须要有无参构造方法
     *
     * @return Class<? extends ErrorAttributes>
     */
    Class<? extends ErrorAttributes> handlerClass() default FeignExceptionHandler.class;

    /**
     * 异常解析处理类, 必须要有无参构造方法
     *
     * @return Class<? extends ErrorDecoder>
     */
    Class<? extends ErrorDecoder> decoderClass() default FeignExceptionDecoder.class;

}
