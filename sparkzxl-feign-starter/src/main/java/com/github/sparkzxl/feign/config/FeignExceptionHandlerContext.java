package com.github.sparkzxl.feign.config;

import org.springframework.core.env.Environment;

/**
 * description:
 *
 * @author: zhouxinlei
 * @date: 2020-12-08 11:12:00
*/
public final class FeignExceptionHandlerContext {


    private static Environment ENVIRONMENT;


    public static String getApplicationName() {
        return ENVIRONMENT == null ? "unknownServer" : ENVIRONMENT.getProperty("spring.application.name");
    }

    public static Environment getEnvironment() {
        return ENVIRONMENT;
    }

    public static void setEnvironment(Environment environment) {
        if (ENVIRONMENT == null) {
            FeignExceptionHandlerContext.ENVIRONMENT = environment;
        }
    }
}
