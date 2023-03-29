package com.github.sparkzxl.oss.annotation;

import com.github.sparkzxl.oss.listener.DefaultUploadListener;
import com.github.sparkzxl.oss.listener.UploadListener;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description: OSSUpload 注解
 *
 * @author zhouxinlei
 * @since 2022-09-27 09:26:10
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OSSUpload {

    /**
     * 是否启用OSS文件上传
     *
     * @return 是否启用
     */
    boolean enabled() default true;

    /**
     * 上传监听
     *
     * @return Class<? extends UploadStrategy>
     */
    Class<? extends UploadListener> listener() default DefaultUploadListener.class;

}
