package com.github.sparkzxl.core.context;

import lombok.Data;

import java.io.Serializable;

/**
 * description: 线程变量封装的参数
 *
 * @author zhouxinlei
 */
@Data
public class ThreadLocalParam implements Serializable {

    private String realm;

    private Long userId;

    private String name;

    private String account;
}
