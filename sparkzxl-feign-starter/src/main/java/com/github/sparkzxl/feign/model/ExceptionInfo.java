package com.github.sparkzxl.feign.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * description: 异常实体类封装
 *
 * @author: zhouxinlei
 * @date: 2020-12-08 14:19:15
*/
@Getter
@Setter
public class ExceptionInfo {

    /**
     * 发生时间
     */
    private Date timestamp;

    /**
     * 相应状态
     */
    private Integer status;

    /**
     * 错误原因
     */
    private String error;

    /**
     * exception中包含的信息
     */
    private String message;

    /**
     * 出错的路径
     */
    private String path;

    /**
     * 抛出的异常 全称 java.lang.RuntimeException
     */
    private String throwExceptionClass;

    /**
     * 异常链
     */
    private List<ExceptionChain> exceptionChain;

}
