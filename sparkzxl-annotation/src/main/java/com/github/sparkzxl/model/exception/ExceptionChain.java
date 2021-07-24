package com.github.sparkzxl.model.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * description: 异常链实体
 *
 * @author zhouxinlei
 */
@Getter
@Setter
public class ExceptionChain {

    /**
     * happened timestamp
     */
    private Date timestamp;

    /**
     * happened exceptionClass
     */
    private String exceptionClass;

    /**
     * message of exception
     */
    private String msg;

    /**
     * the feign client path url
     */
    private String path;

    /**
     *
     */
    private String applicationName;


    public boolean isAssignableFrom(Class<? extends Throwable> exception) {
        try {
            return exception.isAssignableFrom(Class.forName(exceptionClass));
        } catch (Exception e) {
            return false;
        }
    }
}
